package io.lzyprime.model.db

import io.ktor.server.sessions.*
import io.ktor.util.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object UserFilms : IntIdTable() {
    val name = varchar("name", 20).uniqueIndex()
    val checkCode = varchar("check_code", 50).default("")
    val avatar = varchar("avatar", 500).default("")
    val nickname = varchar("nickname", 50).default("")
    val gender = enumeration<Gender>("gender").default(Gender.Unknown)
    val createTime = long("create_time").clientDefault { System.currentTimeMillis() }
}

class UserFilm(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserFilm>(UserFilms) {
        fun findByName(name: String) = find { UserFilms.name eq name }.firstOrNull()
    }

    val uid get() = id.value
    var name by UserFilms.name
    var avatar by UserFilms.avatar
    var nickname by UserFilms.nickname
    var gender by UserFilms.gender
    private val createTime by UserFilms.createTime

    private var checkCode by UserFilms.checkCode

    private val trans
        get() = SessionTransportTransformerMessageAuthentication(
            hex(listOf(uid, createTime).joinToString(separator = "3821", prefix = "3821", postfix = "3821")),
        )

    fun setPassword(v: String) {
        checkCode = trans.transformWrite(v).substringAfterLast('/', "")
    }

    fun checkPassword(v: String) =
        trans.transformRead("$v/$checkCode") == v
}