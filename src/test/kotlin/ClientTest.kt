import org.im.service.client.impl.msgClient
import org.im.service.client.interfaces.sendTextMessage
import org.im.service.client.utils.onConnectionEstablished
import org.im.service.client.utils.onDisconnected
import org.im.service.log.isDebugLog
import org.im.service.client.metadata.IMInitConfig
import org.im.service.client.metadata.LoginParams
import org.im.service.client.metadata.SessionType
import org.im.service.utils.networkAddress
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author: liuzhongao
 * @date: 2022/11/28 下午10:31
 */

fun main() {
    isDebugLog = true
    val address = networkAddress?.hostAddress ?: "127.0.0.1"
    val port = 8080

    val clientConfig = IMInitConfig.Builder()
        .address(address)
        .port(port)
        .build()
    clientConfig.serverAddress = address
    clientConfig.port = port

    val loginParams = LoginParams()
    loginParams.uid = "123455512"

    val messageOperator = msgClient.msgOperator(SessionType.P2P)
    val session = messageOperator.openSession("MTIzNDU1NTIyMw==")

    msgClient.onConnectionEstablished {
        msgClient.authorization().login(loginParams)
    }
    msgClient.onDisconnected {
        msgClient.init(clientConfig)
    }
    msgClient.init(clientConfig)

    val sendTextRunnable = Runnable {
        val currentThread = Thread.currentThread()
        while (currentThread.isAlive && !currentThread.isInterrupted) {
            var userInput = ""
            val bufferedReader = BufferedReader(InputStreamReader(System.`in`))
            while (bufferedReader.readLine().also { userInput = it } != "exit") {
                session.sendTextMessage(userInput, SessionType.P2P)
            }
        }
    }
    Thread(sendTextRunnable).start()
}