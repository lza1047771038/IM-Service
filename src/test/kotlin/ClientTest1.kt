import org.im.service.client.impl.msgClient
import org.im.service.client.interfaces.sendTextMessage
import org.im.service.client.utils.onConnectionEstablished
import org.im.service.log.isDebugLog
import org.im.service.metadata.SessionType
import org.im.service.metadata.client.IMInitConfig
import org.im.service.metadata.client.LoginParams
import org.im.service.utils.networkAddress
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author: liuzhongao
 * @date: 2022/11/29 20:53
 */
fun main() {
    isDebugLog = true
    val address = networkAddress?.hostAddress ?: "127.0.0.1"
    val port = 8080

    val clientConfig = IMInitConfig()
    clientConfig.serverAddress = address
    clientConfig.port = port

    val loginParams = LoginParams()
    loginParams.uid = "1234555223"

    val messageOperator = msgClient.msgOperator()
    val session = messageOperator.openSession("MTIzNDU1NTEy", SessionType.P2P)

    msgClient.onConnectionEstablished {
        msgClient.authorization().login(loginParams)
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