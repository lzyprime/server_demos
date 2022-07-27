package io.lzyprime.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.lzyprime.routes.fileRoute
import io.lzyprime.routes.userRoute

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        userRoute()
        fileRoute()
    }
}
