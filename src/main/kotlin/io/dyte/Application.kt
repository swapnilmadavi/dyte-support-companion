package io.dyte

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import io.dyte.plugins.*
import io.ktor.server.application.*
import kotlinx.coroutines.launch

fun main(args: Array<String>) {
    val apiKey = "<ENTER_KEY_HERE>" //System.getenv("OPENAI_API_KEY")
    val token = requireNotNull(apiKey) { "OPENAI_API_KEY environment variable must be set." }
    openAIClient = OpenAI(token = token, logging = LoggingConfig(LogLevel.All))
    println("DyteHack: OpenAI initialized")
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureSockets()
}

internal var openAIClient: OpenAI? = null

val codeBuffer: StringBuilder = StringBuilder()
fun appendCodeSnippetToBuffer(codeSnippet: String) {
    if (codeBuffer.isEmpty()) {
        codeBuffer.append(codeSnippet)
        return
    }

    val overlappingText = StringBuilder()
    val maxOverlapLength = minOf(codeBuffer.length, codeSnippet.length)
    for (i in 1..maxOverlapLength) {
        if (codeBuffer.takeLast(i) == codeSnippet.take(i)) {
            overlappingText.clear()
            overlappingText.append(codeSnippet.take(i))
        }
    }

    if (overlappingText.isNotEmpty()) {
        codeBuffer.append(codeSnippet.substring(overlappingText.length))
    }
}

@OptIn(BetaOpenAI::class)
internal suspend fun askOpenAiToFindIssuesInCode(openAI: OpenAI) {
    println("DyteHack: askOpenAiToFindIssuesInCode")
    val chatCompletionRequest = ChatCompletionRequest(
        model = ModelId("gpt-3.5-turbo"),
        messages = listOf(
            ChatMessage(
                role = ChatRole.System,
                content = "You will be provided with a code snippets, and your task is to find and fix bugs in it."
            ),
            ChatMessage(
                role = ChatRole.User,
                content = codeBuffer.toString()
            )
        )
    )
    val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
    println("DyteHack: openAI response =>")
    println(completion)
}
