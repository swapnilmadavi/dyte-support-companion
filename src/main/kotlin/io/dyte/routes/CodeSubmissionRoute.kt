package io.dyte.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.codeSubmissionRoute() {
    route("code/submit") {
        post {
            val codeSnippet = call.receiveText()
            println("DyteHack: $codeSnippet")
            call.respondText("Code snippet submitted", status = HttpStatusCode.Accepted)
        }
    }
}