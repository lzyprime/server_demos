package io.lzyprime.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.lzyprime.model.AuthRepository
import io.lzyprime.model.FileRepository
import io.lzyprime.model.UserRepository
import io.lzyprime.model.data.Failed
import io.lzyprime.model.data.FileType
import io.lzyprime.model.data.TokenPrincipal
import io.lzyprime.model.db.Gender
import io.lzyprime.plugins.Security

fun Routing.userRoute() = route("/user") {
    login()
    authenticate(Security.authSessionName) {
        checkLogin()
        updateUserInfo()
        updateAvatar()
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
    call.respondResult(UserRepository.getUserInfo(selfUid))
}

@kotlinx.serialization.Serializable
data class UpdateUserInfoReq(
    val nickname: String,
    val gender: Gender,
)

private fun Route.updateUserInfo() = put<UpdateUserInfoReq> { req ->
    call.respondResult(
        UserRepository.updateUserInfo(selfUid) {
            it.nickname = req.nickname
            it.gender = req.gender
        }
    )
}

private fun Route.updateAvatar() = put<ByteArray>("/avatar") { fileByteArray ->
    FileRepository.putFile(FileType.Picture, fileByteArray).onSuccess { fid ->
        call.respondResult(
            UserRepository.updateUserInfo(selfUid) {
                it.avatar = fid
            }
        )
    }.onFailure { t ->
        call.respondFailed(t)
    }
}
