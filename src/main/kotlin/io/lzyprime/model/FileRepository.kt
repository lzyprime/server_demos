package io.lzyprime.model

import io.lzyprime.model.data.Failed
import io.lzyprime.model.data.FileType
import io.lzyprime.model.db.DB
import io.lzyprime.model.db.FileFilm
import io.lzyprime.utils.md5
import org.jetbrains.exposed.sql.transactions.transaction

interface FileRepository {
    fun putFile(fileType: FileType, fileByte: ByteArray): Result<Int>
    fun getFileById(fileType: FileType, fid: Int): Result<ByteArray>

    companion object : FileRepository {

        override fun putFile(fileType: FileType, fileByte: ByteArray): Result<Int> =
            Failed.FilePutFailed.onNullOrFailed {
                getFileFilmEntity(fileType)?.let { film ->
                    val file = transaction(DB.fileDB) {
                        val filename = md5(fileByte)
                        film.findByName(filename) ?:
                        film.new {
                            name = filename
                            bytes = fileByte
                        }
                    }
                    file.fid
                }
            }

        override fun getFileById(fileType: FileType, fid: Int): Result<ByteArray> =
            Failed.FileNotFound.onNullOrFailed {
                getFileFilmEntity(fileType)?.let { film ->
                    transaction(DB.fileDB) {
                        film.findById(fid)
                    }?.bytes
                }
            }

        private fun getFileFilmEntity(fileType: FileType): FileFilm.Entity<*>? = when (fileType) {
            FileType.Picture -> FileFilm.Picture
            else -> null
        }
    }
}