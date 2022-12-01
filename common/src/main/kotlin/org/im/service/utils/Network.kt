package org.im.service.utils

import java.net.InetAddress
import java.net.NetworkInterface
import java.net.UnknownHostException
import java.util.*


/**
 * @author: liuzhongao
 * @date: 2022/12/1 下午8:51
 */
val networkAddress: InetAddress?
    get() = getLocalHostLANAddress()


private fun getLocalHostLANAddress(): InetAddress? {
    return try {
        var candidateAddress: InetAddress? = null
        // 遍历所有的网络接口
        val ifaces: Enumeration<*> = NetworkInterface.getNetworkInterfaces()
        while (ifaces.hasMoreElements()) {
            val iface = ifaces.nextElement() as NetworkInterface
            // 在所有的接口下再遍历IP
            val inetAddrs: Enumeration<*> = iface.inetAddresses
            while (inetAddrs.hasMoreElements()) {
                val inetAddr = inetAddrs.nextElement() as InetAddress
                if (!inetAddr.isLoopbackAddress) { // 排除loopback类型地址
                    if (inetAddr.isSiteLocalAddress) {
                        // 如果是site-local地址，就是它了
                        return inetAddr
                    } else if (candidateAddress == null) {
                        // site-local类型的地址未被发现，先记录候选地址
                        candidateAddress = inetAddr
                    }
                }
            }
        }
        if (candidateAddress != null) {
            return candidateAddress
        }
        // 如果没有发现 non-loopback地址.只能用最次选的方案
        val jdkSuppliedAddress = InetAddress.getLocalHost() ?: throw UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.")
        jdkSuppliedAddress
    } catch (e: Exception) {
        val unknownHostException = UnknownHostException("Failed to determine LAN address: $e")
        unknownHostException.initCause(e)
        throw unknownHostException
    }
}