package org.im.service.client.metadata

import java.io.Serializable

open class IMInitConfig protected constructor(builder: Builder) : Serializable {
    var serverAddress: String = ""
    var port: Int = 8080

    init {
        serverAddress = builder.address
        port = builder.port
    }

    open class Builder {
        var address: String = ""
        var port: Int = 8080

        open fun address(address: String) = apply { this.address = address }

        open fun port(port: Int) = apply { this.port = port }

        open fun build(): IMInitConfig {
            return IMInitConfig(this)
        }

    }
}