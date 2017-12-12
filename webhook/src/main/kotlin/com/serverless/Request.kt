package com.serverless

import java.nio.charset.StandardCharsets
import java.util.Base64

class Request {

    var body: String? = null
    var signature: String? = null

    val decodedBody: String
        get() = String(Base64.getDecoder().decode(body!!.toByteArray(StandardCharsets.UTF_8)))
}
