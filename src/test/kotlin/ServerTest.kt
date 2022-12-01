import org.im.service.log.isDebugLog
import org.im.service.server.controller.SocketService
import org.im.service.server.controller.config.SocketConfig
import org.im.service.utils.networkAddress

/**
 * @author: liuzhongao
 * @date: 2022/11/29 20:55
 */
fun main() {
    isDebugLog = true
    val address = networkAddress?.hostAddress ?: "127.0.0.1"
    val port = 8080
    val serverConfig = SocketConfig()
    val service = SocketService()

    serverConfig.address = address
    serverConfig.ports = intArrayOf(port)
    service.init(serverConfig)

}