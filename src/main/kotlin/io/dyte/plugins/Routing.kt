package io.dyte.plugins

import com.aallam.openai.client.OpenAI
import io.dyte.routes.codeRoute
import io.dyte.routes.transcriptionRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(openAI: OpenAI) {
    routing {
        codeRoute(openAI)
        transcriptionRoute(openAI)
    }
}
