package io.lzyprime.model.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

sealed class FileFilms : IntIdTable() {
    val name = varchar("filename", 33).uniqueIndex()
    val bytes = binary("file_bytes")

    object Picture : FileFilms()
}

sealed class FileFilm(id: EntityID<Int>, films: FileFilms) : IntEntity(id) {
    val fid get() = id.value
    var name by films.name
    var bytes by films.bytes

    class Picture(id: EntityID<Int>) : FileFilm(id, FileFilms.Picture) {
        companion object : Entity<Picture>(FileFilms.Picture)
    }

    sealed class Entity<T : FileFilm>(private val films: FileFilms) : IntEntityClass<T>(films) {
        fun findByName(filename: String) = find { films.name eq filename }.firstOrNull()
    }
}