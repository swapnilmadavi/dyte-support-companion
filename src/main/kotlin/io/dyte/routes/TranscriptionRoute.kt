package io.dyte.routes

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.dyte.models.Transcription
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@OptIn(BetaOpenAI::class)
fun Route.transcriptionRoute(openAI: OpenAI) {
    route("transcription") {
        post {
            val transcription = call.receive<Transcription>()
            val completion = extractKeywordsFromTranscription(openAI, transcription)
            call.respondText(completion.toString(), status = HttpStatusCode.OK)
        }
    }
}

@OptIn(BetaOpenAI::class)
internal suspend fun extractKeywordsFromTranscription(openAI: OpenAI, transcription: Transcription): ChatCompletion {
    val chatCompletionRequest = ChatCompletionRequest(
        model = ModelId("gpt-3.5-turbo"),
        messages = listOf(
            ChatMessage(
                role = ChatRole.User,
                content = transcription.transcription
            )
        )
    )
    val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
    println("DyteHack: openAI extractKeywordsFromTranscription response =>")
    println(completion)
    return completion
}
