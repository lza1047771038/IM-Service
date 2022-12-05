package org.im.service.client.metadata

import java.io.Serializable

open class IMInitConfig constructor(): Serializable {
    var serverAddress: String = ""
    var port: Int = 8080

    open class Builder {
        private var address: String = ""
        private var port: Int = 8080

        fun setAddress(address: String) = apply { this.address = address }

        fun setPort(port: Int) = apply { this.port = port }

        fun build(): IMInitConfig {
            return IMInitConfig().also { config ->
                config.serverAddress = address
                config.port = port
            }
        }

    }
}