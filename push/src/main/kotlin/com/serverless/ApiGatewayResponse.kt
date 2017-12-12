package com.serverless

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.log4j.Logger

import java.nio.charset.StandardCharsets
import java.util.Base64

class ApiGatewayResponse(val statusCode: Int, val body: String?, val headers: Map<String, String>, // API Gateway expects the property to be called "isBase64Encoded" => isIs
                         val isIsBase64Encoded: Boolean) {

    class Builder {

        private var statusCode = 200
        private var headers = emptyMap<String, String>()
        private var rawBody: String? = null
        private var objectBody: Any? = null
        private var binaryBody: ByteArray? = null
        private var base64Encoded: Boolean = false

        fun setStatusCode(statusCode: Int): Builder {
            this.statusCode = statusCode
            return this
        }

        fun build(): ApiGatewayResponse {
            var body: String? = null
            when {
                rawBody != null -> {
                    body = rawBody
                }
                objectBody != null -> {
                    try {
                        body = objectMapper.writeValueAsString(objectBody)
                    } catch (e: JsonProcessingException) {
                        LOG.error("failed to serialize object", e)
                        throw RuntimeException(e)
                    }
                }
                binaryBody != null -> {
                    body = String(Base64.getEncoder().encode(binaryBody!!), StandardCharsets.UTF_8)
                }
            }
            return ApiGatewayResponse(statusCode, body, headers, base64Encoded)
        }

        companion object {
            private val LOG = Logger.getLogger(Builder::class.java)
            private val objectMapper = ObjectMapper()
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}
