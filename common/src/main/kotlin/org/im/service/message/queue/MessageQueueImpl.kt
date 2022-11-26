package org.im.service.message.queue

import org.im.service.message.queue.interfaces.MessageQueue
import org.im.service.message.queue.metadata.Message
import java.util.LinkedList

class MessageQueueImpl(processorCount: Int): MessageQueue {
    companion object {
        private var threadCount: Int = 0
    }
    private val threadName: String
        get() = "${this.javaClass.simpleName}-thread-${threadCount++}"

    private val delegate = DynamicMessageQueue()
    private val threads: LinkedList<Thread> = LinkedList()

    init {
        for (index in 0 until processorCount) {
            val thread = Thread({
                val currentThread = Thread.currentThread()
                while (currentThread.isAlive && !currentThread.isInterrupted) {
                    val message = dequeue()
                    kotlin.runCatching { message.operation?.run() }.onFailure { it.printStackTrace() }
                }
            }, threadName)
            threads.add(thread)
        }
        threads.forEach { it.start() }
    }

    override fun enqueue(message: Message) = delegate.enqueue(message)

    override fun dequeue(): Message = delegate.dequeue()
}