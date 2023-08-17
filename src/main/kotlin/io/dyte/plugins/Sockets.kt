package io.dyte.plugins

import io.dyte.askOpenAiToFindIssuesInCode
import io.dyte.openAIClient
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("code/issues") {
            send("You are connected!")
            for(frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                println("DyteHack: $receivedText")
                send("You said: $receivedText")
                if (receivedText == "review") {
                    openAIClient?.let {ai ->
//                        launch {
                            askOpenAiToFindIssuesInCode(ai)
//                        }
                    }
                }
            }
        }
    }
}