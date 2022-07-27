package io.lzyprime.model.data

enum class FileType(val url: String) {
    Unknown("error_type"),
    Picture("picture");
    companion object {
        operator fun invoke(url: String?):FileType = values().find { it.url == url } ?: Unknown
    }
}