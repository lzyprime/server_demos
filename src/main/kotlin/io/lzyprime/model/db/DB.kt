package io.lzyprime.model.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

object DB {
    private const val DBName = "data"
    private const val FileDBName = "file"

    val db by lazy {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        Database.connect("jdbc:sqlite:$DBName.db", "org.sqlite.JDBC")
    }
    val fileDB by lazy {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        Database.connect("jdbc:sqlite:$FileDBName.db", "org.sqlite.JDBC")
    }
}

fun main() {
    transaction(DB.db) {
//        SchemaUtils.drop()
        SchemaUtils.create(UserFilms)
    }
    transaction(DB.fileDB) {
        SchemaUtils.create(FileFilms.Picture)
    }
}