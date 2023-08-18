package io.dyte.plugins

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.client.OpenAI
import io.dyte.codeBuffer
import io.dyte.reviewCodeSnippet
import io.dyte.wsSession
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration

@OptIn(BetaOpenAI::class)
fun Application.configureSockets(openAI: OpenAI) {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("code/issues") {
            wsSession = this
            send("You are connected!")
            for(frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                println("DyteHack: $receivedText")
                send("You said: $receivedText")
                if (receivedText == "review") {
                    val chatCompletion = reviewCodeSnippet(openAI, codeBuffer.toString())
                    // clear code buffer to form next snippet
                    codeBuffer.clear()
                }
            }
        }
    }
}