package io.lzyprime.model.data

import io.ktor.http.*

sealed class Failed(code: Int) : Exception() {
    open val statusCode: HttpStatusCode by lazy {
        HttpStatusCode(code, javaClass.simpleName)
    }

    inline fun <reified T> onNullOrFailed(block: () -> T?): Result<T> = try {
        Result.success(block()!!)
    } catch (_: Throwable) {
        Result.failure(this)
    }

    object Unknown : Failed(9999)
    object UserOrPasswordError : Failed(10000)
    object TokenExpired : Failed(10001)
    object UserNotExist : Failed(10002)

    object FilePutFailed : Failed(20001)
    object FileNotFound : Failed(20002)
    object UnknownFileType : Failed(20003)
    data class NormalFailed(override val statusCode: HttpStatusCode) : Failed(statusCode.value)
}