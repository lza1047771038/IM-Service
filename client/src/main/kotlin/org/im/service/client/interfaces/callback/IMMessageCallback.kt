package org.im.service.client.interfaces.callback

import org.im.service.client.metadata.NotifyWrapper

/**
 * @author: liuzhongao
 * @date: 2022/11/30 21:18
 */
interface IMMessageCallback {
    fun onNotify(wrapper: NotifyWrapper?)
}