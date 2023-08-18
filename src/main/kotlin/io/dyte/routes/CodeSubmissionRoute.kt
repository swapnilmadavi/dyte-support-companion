package io.dyte.routes

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.client.OpenAI
import io.dyte.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.websocket.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@OptIn(BetaOpenAI::class)
fun Route.codeRoute(openAI: OpenAI) {
    route("/code") {
        post("submit") {
            val codeSnippet = call.receiveText()
            appendCodeSnippetToBuffer(codeSnippet)
            call.respondText("", status = HttpStatusCode.Accepted)
        }

        get("review") {
            val codeSnippet = codeBuffer.toString()
            codeBuffer.clear()
            val chatCompletion = reviewCodeSnippet(openAI, codeSnippet)
            val response = buildJsonObject {
                put("code", codeSnippet)
                put("review", chatCompletion.choices.firstOrNull()?.message?.content)
            }
            call.respond(HttpStatusCode.OK, response)
        }
    }
}
