package io.lzyprime.model.pref

import io.ktor.server.sessions.*
import io.ktor.util.*
import io.lzyprime.plugins.Security

interface TokenManager {
    fun uidToToken(uid: Int): String
    fun tokenToUid(token: String): Int?

    fun clearToken(token: String)

    companion object : TokenManager by TokenManagerImpl
}

private object TokenManagerImpl : TokenManager {
    private const val TokenMaxAgeInMillis = 7 * 24 * 3600 * 1000L //token 过期时间, 秒
    private val secretSignKey = hex(System.currentTimeMillis().toString(16))
    private val transformer by lazy {
        SessionTransportTransformerEncrypt(hex(Security.encryptKey), secretSignKey)
    }
    private val cache = mutableMapOf<String, String>()
    private val timestamp = mutableMapOf<String, Long>()

    override fun tokenToUid(token: String): Int? {
        return if (System.currentTimeMillis() - timestamp.getOrDefault(token, 0) <= TokenMaxAgeInMillis) {
            cache[token]?.let(transformer::transformRead)?.toIntOrNull()?.also {
                timestamp[token] = System.currentTimeMillis()
            }
        } else {
            timestamp.remove(token)
            cache.remove(token)
            null
        }
    }

    override fun uidToToken(uid: Int): String {
        val code = transformer.transformWrite(uid.toString())
        return code.split("/").first().also {
            cache[it] = code
            timestamp[it] = System.currentTimeMillis()
        }
    }

    override fun clearToken(token: String) {
        cache.remove(token)
        timestamp.remove(token)
    }
}