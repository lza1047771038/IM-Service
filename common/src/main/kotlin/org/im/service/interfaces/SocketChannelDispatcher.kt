package org.im.service.interfaces

import java.nio.channels.Selector

/**
 * @author: liuzhongao
 * @date: 2022/11/28 10:34
 */
interface SocketChannelDispatcher {
    fun dispatch(selector: Selector)
}