package com.serverless

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.linecorp.bot.client.LineMessagingServiceBuilder
import com.linecorp.bot.model.PushMessage
import com.linecorp.bot.model.message.TextMessage
import org.apache.log4j.Logger

import java.io.IOException

class Handler : RequestHandler<Map<String, String>, ApiGatewayResponse> {

    /***
     * Receive String, and send Push message
     * @param sendMessage
     */
    private fun push(sendMessage: String) {
        val textMessage = TextMessage(sendMessage);
        val pushMessage = PushMessage(USER_ID, textMessage);

        val client = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()

        try {
            val response = client.pushMessage(pushMessage).execute()
            when {
                response.isSuccessful -> {
                    LOG.info(response.message())
                }
                else -> {
                    LOG.warn(response.errorBody().string())
                }
            }
        } catch (e1: IOException) {
            LOG.error(e1)
        }
    }

    override fun handleRequest(input: Map<String, String>, context: Context): ApiGatewayResponse {
        try {
            push(input.get("message")!!)
        } catch (e: Exception) {
            LOG.error(e)
        }

        return ApiGatewayResponse.builder().setStatusCode(200).build()
    }

    companion object {
        private val LOG = Logger.getLogger(Handler::class.java)
        private val CHANNEL_ACCESS_TOKEN = System.getenv("CHANNEL_ACCESS_TOKEN")
        private val USER_ID = System.getenv("USER_ID")
    }

}
