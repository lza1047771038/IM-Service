package org.im.service.client.interfaces.callback

/**
 * @author: liuzhongao
 * @date: 2022/11/28 下午10:38
 */
interface GlobalCallback {

    fun onConnectionSuccess()

    fun onServiceLoginSuccess(sessionId: String)
}