package io.lzyprime.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.lzyprime.model.AuthRepository
import io.lzyprime.model.UserRepository
import io.lzyprime.model.data.Failed
import io.lzyprime.model.data.TokenPrincipal
import io.lzyprime.model.db.Gender
import io.lzyprime.plugins.Security

fun Routing.userRoute() = route("/user") {
    login()
    authenticate(Security.authSessionName) {
        checkLogin()
        updateUserInfo()
    }
}

@kotlinx.serialization.Serializable
data class LoginReq(val user: String, val password: String)

/**
 * login or create new user.
 *
 * @return UserInfo.
 * */
private fun Route.login() = post<LoginReq>("/login") { req ->
    AuthRepository.login(req.user, req.password).fold({ (user, token) ->
        call.sessions.set(Security.tokenName, TokenPrincipal(token))
        call.respond(user)
    }, {
        call.respondFailed(it)
    })
}

private fun Route.checkLogin() = get {
    UserRepository.getUserInfo(selfUid).onSuccess {
        call.respond(it)
    }.onFailure {
        call.sessions.clear(Security.tokenName)
        call.respondFailed(Failed.TokenExpired)
    }
}

@kotlinx.serialization.Serializable
data class UpdateUserInfoReq(
    val nickname: String?,
    val gender: Int?,
    val avatar: String?,
)

private fun Route.updateUserInfo() = put<UpdateUserInfoReq> { req ->
    call.respondResult(
        UserRepository.updateUserInfo(selfUid) {
            req.nickname?.let { v -> it.nickname = v }
            req.gender?.let { v -> it.gender = Gender(v) }
            req.avatar?.let { v -> it.avatar = v }
        }
    )
}
