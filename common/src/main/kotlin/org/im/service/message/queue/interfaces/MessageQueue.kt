package org.im.service.message.queue.interfaces

import org.im.service.message.queue.metadata.Message


interface MessageQueue {
    fun enqueue(message: Message)
    fun dequeue(): Message
}

fun MessageQueue.execute(block: () -> Unit) = enqueue(Message { block() })