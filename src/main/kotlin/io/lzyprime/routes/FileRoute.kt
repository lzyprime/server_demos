package io.lzyprime.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.lzyprime.model.FileRepository
import io.lzyprime.model.data.FileType
import io.lzyprime.plugins.Security

fun Route.fileRoute() = route("/file") {
    getFile()
    authenticate(Security.authSessionName) {
        putFile()
    }
}

private fun Route.getFile() = get("/{fileType}/{fid}") {
    val fid = call.parameters["fid"]?.toIntOrNull() ?: -1
    FileRepository.getFileById(FileType(call.parameters["fileType"]), fid)
        .onSuccess { call.respondBytes(it) }
        .onFailure { call.respondFailed(it) }
}

private fun Route.putFile() = put<ByteArray>("{fileType}") { fileBytes ->
    call.respondResult(FileRepository.putFile(FileType(call.parameters["fileType"]), fileBytes))
}