import org.im.service.client.impl.msgClient
import org.im.service.client.interfaces.GlobalCallback
import org.im.service.log.isDebugLog
import org.im.service.log.logDebug
import org.im.service.metadata.client.IMInitConfig
import org.im.service.metadata.client.LoginParams
import org.im.service.server.controller.SocketService
import org.im.service.server.controller.config.SocketConfig

/**
 * @author: liuzhongao
 * @date: 2022/11/28 下午10:31
 */

fun main() {
    isDebugLog = true
    val address = "127.0.0.1"
    val port = 8080
    val serverConfig = SocketConfig()
    val service = SocketService()

    serverConfig.address = address
    serverConfig.ports = intArrayOf(port)
    service.init(serverConfig)

    val clientConfig = IMInitConfig()
    clientConfig.serverAddress = address
    clientConfig.port = port

    val loginParams = LoginParams()
    loginParams.uid = "123455512"
    msgClient.addGlobalCallback(object: GlobalCallback {
        override fun onConnectionSuccess() {
            msgClient.authorization().login(loginParams)
        }

        override fun onServiceLoginSuccess(sessionId: String) {
            logDebug("onServiceLoginSuccess, sessionId: $sessionId")
        }
    })
    msgClient.init(clientConfig)
}