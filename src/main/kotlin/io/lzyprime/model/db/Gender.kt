package io.lzyprime.model.db

enum class Gender {
    Unknown,
    Male,
    Female,
    Secret;
    companion object {
        operator fun invoke(ordinal: Int): Gender = values().getOrNull(ordinal) ?: Unknown
    }
}