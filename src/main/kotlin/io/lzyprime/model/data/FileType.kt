package io.lzyprime.model.data

import io.lzyprime.plugins.Security

enum class FileType(val url: String) {
    Unknown("error_type"),
    Picture("picture");


    fun fileUrl(fid: Int): String =
        if (this == Unknown || fid == -1) "" else "http://${Security.baseUrl}/file/$url/$fid"

    companion object {
        operator fun invoke(url: String?): FileType = values().find { it.url == url } ?: Unknown
    }
}