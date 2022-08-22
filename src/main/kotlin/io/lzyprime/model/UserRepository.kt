package io.lzyprime.model

import io.lzyprime.model.data.Failed
import io.lzyprime.model.data.UserInfo
import io.lzyprime.model.db.DB
import io.lzyprime.model.db.UserFilm
import org.jetbrains.exposed.sql.transactions.transaction

interface UserRepository {
    fun getUserInfo(uid: Int): Result<UserInfo>
    fun updateUserInfo(uid: Int, block: (UserFilm) -> Unit): Result<UserInfo>

    companion object : UserRepository {
        override fun getUserInfo(uid: Int): Result<UserInfo> =
            when (val user = transaction(DB.db) {
                UserFilm.findById(uid)
            }) {
                null -> Result.failure(Failed.UserNotExist)
                else -> Result.success(UserInfo(user))
            }

        override fun updateUserInfo(uid: Int, block: (UserFilm) -> Unit): Result<UserInfo> =
            Failed.UserNotExist.onNullOrFailed {
                transaction(DB.db) {
                    UserFilm.findById(uid)?.also(block)
                }?.let { UserInfo(it) }
            }
    }
}
