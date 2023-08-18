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

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val openAiApiKey = System.getenv("OPENAI_API_KEY")
    val token = requireNotNull(openAiApiKey) { "OPENAI_API_KEY environment variable must be set." }
    val openAI = configureOpenAi(token)
    configureSerialization()
    configureRouting(openAI)
    configureSockets(openAI)
}

private fun configureOpenAi(token: String): OpenAI {
    return OpenAI(token = token, logging = LoggingConfig(LogLevel.All))
}


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
internal suspend fun reviewCodeSnippet(openAI: OpenAI, codeSnippet: String): ChatCompletion {
    val chatCompletionRequest = ChatCompletionRequest(
        model = ModelId("gpt-3.5-turbo"),
        messages = listOf(
            ChatMessage(
                role = ChatRole.System,
                content = "You will be provided with a code snippet, and your task is to find and fix bugs in it."
            ),
            ChatMessage(
                role = ChatRole.User,
                content = codeSnippet
            )
        )
    )
    val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
    println("DyteHack: openAI reviewCodeSnippet response =>")
    println(completion)
    return completion
}
