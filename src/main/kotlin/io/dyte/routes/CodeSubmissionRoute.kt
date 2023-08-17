package io.dyte.routes

import io.dyte.appendCodeSnippetToBuffer
import io.dyte.codeBuffer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.codeSubmissionRoute() {
    route("code/submit") {
        post {
            val codeSnippet = call.receiveText()
            val response = StringBuilder()
            response.appendLine("Existing code")
            response.appendLine(codeBuffer.toString())
            response.appendLine()
            response.appendLine("Received code snippet =>")
            response.appendLine(codeSnippet)
            response.appendLine()
            appendCodeSnippetToBuffer(codeSnippet)
            response.appendLine("Merged code =>")
            response.appendLine(codeBuffer.toString())
            call.respondText(response.toString(), status = HttpStatusCode.Accepted)
        }
    }
}