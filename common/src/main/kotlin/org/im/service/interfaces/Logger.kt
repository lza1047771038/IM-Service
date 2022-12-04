package org.im.service.interfaces

/**
 * @author: liuzhongao
 * @date: 2022/11/28 13:19
 */
interface Logger {
    enum class Type {
        Normal,
        Error
    }

    fun log(tagName: String, message: String)

    fun log(tagName: String, message: String, type: Type)
}