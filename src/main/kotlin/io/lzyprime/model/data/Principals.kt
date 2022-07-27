package io.lzyprime.model.data

import io.ktor.server.auth.*

@JvmInline
value class UidPrincipal(val uid: Int) : Principal

@JvmInline
value class TokenPrincipal(val token: String) : Principal