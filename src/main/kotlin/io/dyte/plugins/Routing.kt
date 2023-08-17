package io.dyte.plugins

import io.dyte.routes.codeSubmissionRoute
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        codeSubmissionRoute()
    }
}
