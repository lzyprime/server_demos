package io.lzyprime.utils

import io.ktor.util.*
import io.lzyprime.model.data.FileType
import io.lzyprime.plugins.Security
import java.security.MessageDigest

fun md5(byteArray: ByteArray): String =
    hex(MessageDigest.getInstance("MD5").digest(byteArray))

fun fileUrl(fileType: FileType, fid: Int): String =
    if (fileType == FileType.Unknown || fid == -1) "" else "http://${Security.baseUrl}/file/${fileType.url}/$fid"