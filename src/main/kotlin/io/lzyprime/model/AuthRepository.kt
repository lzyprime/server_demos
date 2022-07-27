package io.lzyprime.model

import io.lzyprime.model.data.Failed
import io.lzyprime.model.data.UserInfo
import io.lzyprime.model.db.DB
import io.lzyprime.model.db.UserFilm
import io.lzyprime.model.pref.TokenManager
import org.jetbrains.exposed.sql.transactions.transaction

interface AuthRepository {
    fun login(name: String, password: String): Result<Pair<UserInfo, String>>
    fun checkToken(token: String): Result<Int>
    fun clearToken(token: String)

    companion object : AuthRepository {
        override fun login(name: String, password: String): Result<Pair<UserInfo, String>> =
            Failed.UserOrPasswordError.onNullOrFailed {
                if (name.isEmpty() || password.isEmpty()) {
                    null
                } else {
                    transaction(DB.db) {
                        UserFilm.findByName(name) ?: UserFilm.new {
                            this.name = name
                        }.also {
                            it.setPassword(password)
                        }
                    }.takeIf { it.checkPassword(password) }?.let { user ->
                        UserInfo(user) to TokenManager.uidToToken(user.uid)
                    }
                }
            }

        override fun checkToken(token: String): Result<Int> =
            Failed.TokenExpired.onNullOrFailed {
                TokenManager.tokenToUid(token)
            }

        override fun clearToken(token: String) {
            TokenManager.clearToken(token)
        }
    }
}