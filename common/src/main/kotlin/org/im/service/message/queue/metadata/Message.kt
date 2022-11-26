package org.im.service.message.queue.metadata

class Message {
    var what: Int? = null
    var args: Any? = null
    var operation: Runnable? = null

    constructor()
    constructor(block: Message.() -> Unit) {
        operation = Runnable {
            block()
        }
    }

    override fun toString(): String {
        return "what: $what, args: $args, operation: $operation"
    }
}
