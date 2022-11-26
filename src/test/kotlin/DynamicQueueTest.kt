import org.im.service.message.queue.DynamicMessageQueue
import org.im.service.message.queue.metadata.Message

const val NAME_PRODUCER_THREAD = "thread-producer"
const val NAME_CONSUMER_THREAD = "thread-consumer"

fun main() {
    val dynamicQueue = DynamicMessageQueue()

    val consumerRunnable = Runnable {
        val currentThread = Thread.currentThread()
        while (currentThread.isAlive && !currentThread.isInterrupted) {
            val message = dynamicQueue.dequeue()
            message.operation?.run()
        }
    }

    val consumerThread1 = Thread(consumerRunnable,"$NAME_CONSUMER_THREAD-1")
    val consumerThread2 = Thread(consumerRunnable,"$NAME_CONSUMER_THREAD-2")

    val producerRunnable = Runnable {
        for (index in 0..1000) {
            val message = Message {
                args = System.nanoTime()
                val currentThread = Thread.currentThread()
                println("operation runs in thread: ${currentThread.name}, object: $this")
                if (index > 1000 - 3) {
                    currentThread.interrupt()
                }
            }
            dynamicQueue.enqueue(message)
            Thread.sleep(250L)
        }
    }

    val producerThread1 = Thread(producerRunnable, "$NAME_PRODUCER_THREAD-1")

    producerThread1.start()
    consumerThread1.start()
    consumerThread2.start()

    producerThread1.join()
    consumerThread1.join()
    consumerThread2.join()
}