package io.lzyprime.model.data

import io.lzyprime.model.db.Gender
import io.lzyprime.model.db.UserFilm
import io.lzyprime.utils.fileUrl

@kotlinx.serialization.Serializable
data class UserInfo(
    val uid: Int,
    val name: String,
    val nickname: String,
    val gender: Gender,
    val avatar: String,
) {
    constructor(user: UserFilm) : this(
        user.uid,
        user.name,
        user.nickname,
        user.gender,
        fileUrl(FileType.Picture, user.avatar),
    )
}