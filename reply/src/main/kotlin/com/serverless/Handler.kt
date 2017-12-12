package com.serverless

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.linecorp.bot.client.LineMessagingServiceBuilder
import com.linecorp.bot.model.ReplyMessage
import com.linecorp.bot.model.event.*
import com.linecorp.bot.model.event.message.*
import com.linecorp.bot.model.message.Message
import com.linecorp.bot.model.message.TextMessage
import org.apache.log4j.Logger

import java.io.IOException
import java.util.ArrayList

class Handler : RequestHandler<DynamodbEvent, ApiGatewayResponse> {

    /***
     * Receive Messaging API request, and send Reply message
     * @param callbackRequest
     */
    private fun reply(callbackRequest: CallbackRequest) {
        callbackRequest.events.forEach { e ->

            when (e) {
                is MessageEvent<*> -> {
                    val messageEvent = e as MessageEvent<MessageContent>
                    val replyToken = messageEvent.replyToken
                    val content = messageEvent.message

                    when (content) {
                        is TextMessageContent -> {
                            val message = content.text
                            val client = LineMessagingServiceBuilder.create(CHANNEL_ACCESS_TOKEN).build()

                            val replyMessages = ArrayList<Message>()
                            replyMessages.add(TextMessage(message))

                            try {
                                val response = client.replyMessage(ReplyMessage(replyToken, replyMessages)).execute()
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
                    }
                    when (content) {
                        is ImageMessageContent -> {
                        }
                    }
                    when (content) {
                        is LocationMessageContent -> {
                        }
                    }
                    when (content) {
                        is AudioMessageContent -> {
                        }
                    }
                    when (content) {
                        is VideoMessageContent -> {
                        }
                    }
                    when (content) {
                        is StickerMessageContent -> {
                        }
                    }
                    when (content) {
                        is FileMessageContent -> {
                        }
                    }
                }
                is UnfollowEvent -> {
                }
                is FollowEvent -> {
                }
                is JoinEvent -> {
                }
                is LeaveEvent -> {
                }
                is PostbackEvent -> {
                }
                is BeaconEvent -> {
                }
                else -> {
                }
            }
        }
    }

    override fun handleRequest(ddbEvent: DynamodbEvent, context: Context): ApiGatewayResponse {
        ddbEvent.records.forEach { event ->
            try {
                val callbackRequest = buildCallbackRequest(event)
                reply(callbackRequest)
            } catch (e: Exception) {
                LOG.error(e)
            }
        }
        return ApiGatewayResponse.builder().setStatusCode(200).build()
    }

    @Throws(IOException::class)
    private fun buildCallbackRequest(record: DynamodbEvent.DynamodbStreamRecord): CallbackRequest {
        val image = record.dynamodb.newImage
        val message = image["message"]?.getS()

        return buildObjectMapper().readValue<CallbackRequest>(message, CallbackRequest::class.java)
    }

    companion object {
        private val LOG = Logger.getLogger(Handler::class.java)

        private val CHANNEL_ACCESS_TOKEN = System.getenv("CHANNEL_ACCESS_TOKEN")

        private fun buildObjectMapper(): ObjectMapper {
            val objectMapper = ObjectMapper()
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

            objectMapper.registerModule(JavaTimeModule())
                    .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            return objectMapper
        }
    }

}
