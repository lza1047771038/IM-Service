import org.im.service.Const
import org.im.service.log.isDebugLog
import org.im.service.log.logger
import org.im.service.metadata.*
import org.im.service.server.controller.SocketService
import org.im.service.server.controller.config.SocketConfig
import org.im.service.impl.NoEncryptor
import org.im.service.utils.closeSilently
import org.im.service.utils.readResponse
import org.im.service.utils.sendRequest
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

fun main() {
    isDebugLog = true

    val service = SocketService()
    service.init(SocketConfig())

    // client
    val clientSocketChannel = SocketChannel.open()
    val clientSelector = Selector.open()
    val address = InetSocketAddress("127.0.0.1", 8080)
    val receiveBuffer = ByteBuffer.allocate(10240)

    var clientToken: String = ""
    val clientConnectionRunnable = Runnable {
        clientSocketChannel.connect(address)
        val currentThread = Thread.currentThread()
        while (currentThread.isAlive && !currentThread.isInterrupted) {
            runCatching { clientSelector.select() }.onFailure { it.printStackTrace() }
            if (!clientSocketChannel.isOpen) {
                break
            }
            val selectionKeys = clientSelector.selectedKeys().iterator()
            while (selectionKeys.hasNext()) {
                val selectionKey: SelectionKey? = selectionKeys.next()
                when {
                    selectionKey == null || !selectionKey.isValid -> continue
                    selectionKey.isConnectable -> {
                        val request = ClientRequest(method = Const.RequestMethod.USER_AUTHORIZATION)
                        val channel = selectionKey.channel() as? SocketChannel ?: continue
                        request.clientSessionId = clientToken
                        request.clientUserId = "1000022131"
                        synchronized(receiveBuffer) {
                            channel.sendRequest(receiveBuffer, request)
                        }
                        clientSocketChannel.register(clientSelector, SelectionKey.OP_READ)
                    }
                    selectionKey.isReadable -> {
                        val socketChannel = selectionKey.channel() as? SocketChannel ?: continue
                        val request = socketChannel.readResponse(receiveBuffer, NoEncryptor())
                        clientToken = request?.sessionId ?: ""
                        logger.log("TestMain", "login in with account token: ${request?.sessionId}")
                    }
                }
                selectionKeys.remove()
            }
        }
    }
    clientSocketChannel.configureBlocking(false)
    clientSocketChannel.register(clientSelector, SelectionKey.OP_CONNECT)

    val userInputRunnable = Runnable {
        val bufferReader = BufferedReader(InputStreamReader(System.`in`))
        var line: String = ""
        while (bufferReader.readLine().also { line = it } != "exit") {
            val request = ClientRequest(method = Const.RequestMethod.MESSAGE_TEXT)
            request.content = line
            request.clientSessionId = clientToken
            synchronized(receiveBuffer) {
                clientSocketChannel.sendRequest(receiveBuffer, request)
            }
        }
        clientSelector.closeSilently()
        clientSocketChannel.closeSilently()
    }

    val thread = Thread(clientConnectionRunnable)
    thread.start()

    val userInputThread = Thread(userInputRunnable)
    userInputThread.start()
}