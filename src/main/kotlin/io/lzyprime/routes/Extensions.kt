package io.lzyprime.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import io.lzyprime.model.data.Failed
import io.lzyprime.model.data.UidPrincipal

val PipelineContext<Unit, ApplicationCall>.selfUid: Int inline get() = call.principal<UidPrincipal>()?.uid ?: 0

suspend inline fun ApplicationCall.respondFailed(failed: Throwable) {
    val statusCode = if (failed is Failed) {
        failed.statusCode
    } else {
        Failed.Unknown.statusCode
    }
    respond(statusCode, statusCode.description)
}

suspend inline fun <reified T : Any> ApplicationCall.respondResult(result: Result<T>) {
    result.onSuccess {
        respond(it)
    }.onFailure {
        respondFailed(it)
    }
}