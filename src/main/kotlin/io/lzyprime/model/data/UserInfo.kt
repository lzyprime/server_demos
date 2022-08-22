package io.lzyprime.model.data

import io.lzyprime.model.db.UserFilm

@kotlinx.serialization.Serializable
data class UserInfo(
    val uid: Int,
    val name: String,
    val nickname: String,
    val gender: Int,
    val avatar: String,
) {
    constructor(user: UserFilm) : this(
        user.uid,
        user.name,
        user.nickname,
        user.gender.ordinal,
        user.avatar,
    )
}