package io.lzyprime.plugins

import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.lzyprime.model.AuthRepository
import io.lzyprime.model.data.Failed
import io.lzyprime.model.data.TokenPrincipal
import io.lzyprime.model.data.UidPrincipal
import io.lzyprime.routes.respondFailed

object Security {
    const val authSessionName = "auth-session"
    const val tokenName = "auth_code"
    lateinit var encryptKey: String
    lateinit var baseUrl: String
}

fun Application.configureSecurity() {
    environment.config.propertyOrNull("ktor.security.encryptKey")?.let {
        Security.encryptKey = it.getString()
    }
    Security.baseUrl = "${environment.config.host}:${environment.config.port}"
    install(Sessions) {
        cookie<TokenPrincipal>(Security.tokenName)
    }
    authentication {
        session<TokenPrincipal>(Security.authSessionName) {
            validate { session ->
                AuthRepository.checkToken(session.token).map { UidPrincipal(it) }.getOrNull()
            }

            challenge {
                it?.token?.let { token -> AuthRepository.clearToken(token) }
                call.sessions.clear(Security.tokenName)
                call.respondFailed(Failed.TokenExpired)
            }
        }
    }
}