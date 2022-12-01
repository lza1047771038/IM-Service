package org.im.service.utils

import java.net.InetAddress
import java.net.NetworkInterface
import java.net.UnknownHostException


/**
 * @author: liuzhongao
 * @date: 2022/12/1 下午8:51
 */
val networkAddress: InetAddress?
    get() = getLocalHostLANAddress()

private fun getLocalHostLANAddress(): InetAddress? {
    return try {
        var candidateAddress: InetAddress? = null
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val networkAddress = networkInterface.inetAddresses
            while (networkAddress.hasMoreElements()) {
                val iNetworkAddress = networkAddress.nextElement()
                if (!iNetworkAddress.isLoopbackAddress) {
                    if (iNetworkAddress.isSiteLocalAddress) {
                        return iNetworkAddress
                    } else if (candidateAddress == null) {
                        candidateAddress = iNetworkAddress
                    }
                }
            }
        }
        if (candidateAddress != null) {
            return candidateAddress
        }
        val jdkSuppliedAddress = InetAddress.getLocalHost() ?: throw UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.")
        jdkSuppliedAddress
    } catch (e: Exception) {
        val unknownHostException = UnknownHostException("Failed to determine LAN address: $e")
        unknownHostException.initCause(e)
        throw unknownHostException
    }
}