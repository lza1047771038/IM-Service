import org.im.service.Const
import org.im.service.metadata.*
import org.im.service.server.controller.SocketService
import org.im.service.server.controller.config.SocketConfig
import org.im.service.server.impl.NoEncryptor
import org.im.service.utils.readResponse
import org.im.service.utils.sendRequest
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

fun main() {
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
            val selectionKeys = clientSelector.selectedKeys().iterator()
            while (selectionKeys.hasNext()) {
                val selectionKey: SelectionKey? = selectionKeys.next()
                when {
                    selectionKey == null || !selectionKey.isValid -> continue
                    selectionKey.isConnectable -> {
                        val request = ClientRequest(method = Const.RequestMethod.USER_AUTHORIZATION)
                        val channel = selectionKey.channel() as? SocketChannel ?: continue
                        request.clientToken = clientToken
                        request.clientUserId = "1000022131"
                        synchronized(receiveBuffer) {
                            channel.sendRequest(receiveBuffer, request)
                        }
                        clientSocketChannel.register(clientSelector, SelectionKey.OP_READ)
                    }
                    selectionKey.isReadable -> {
                        val socketChannel = selectionKey.channel() as? SocketChannel ?: continue
                        val request = socketChannel.readResponse(receiveBuffer, NoEncryptor())
                        clientToken = request?.clientToken ?: ""
                        println("login in with account token: ${request?.clientToken}")
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
            request.clientToken = clientToken
            synchronized(receiveBuffer) {
                clientSocketChannel.sendRequest(receiveBuffer, request)
            }
        }
    }

    val thread = Thread(clientConnectionRunnable)
    thread.start()

    val userInputThread = Thread(userInputRunnable)
    userInputThread.start()
}