package org.im.service.message.queue

import org.im.service.message.queue.interfaces.MessageQueue
import org.im.service.message.queue.metadata.Message
import java.util.LinkedList
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

/**
 * a single message queue for message dispatching designs
 */
class DynamicMessageQueue: MessageQueue {
    private val innerMessageQueue: LinkedList<Message> by lazy { LinkedList() }
    private val reentrantLock: ReentrantLock by lazy { ReentrantLock(true) }
    private val condition: Condition by lazy { reentrantLock.newCondition() }

    override fun enqueue(message: Message) {
        // ensure only one thread operates the message queue at the same time!!!
        reentrantLock.lock()
        innerMessageQueue.offer(message)
        condition.signal()
        reentrantLock.unlock()
        // notify probably waiting threads
    }

    override fun dequeue(): Message {
        reentrantLock.lock()
        var getLast = innerMessageQueue.pollFirst()
        if (getLast == null) {
           condition.await()
           getLast = dequeue()
        }
        reentrantLock.unlock()
        return getLast
    }
}