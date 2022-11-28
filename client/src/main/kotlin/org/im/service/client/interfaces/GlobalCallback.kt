package org.im.service.client.interfaces

/**
 * @author: liuzhongao
 * @date: 2022/11/28 下午10:38
 */
interface GlobalCallback {

    fun onConnectionSuccess()

    fun onServiceLoginSuccess(sessionId: String)
}