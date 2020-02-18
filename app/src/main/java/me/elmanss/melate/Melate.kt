package me.elmanss.melate

import android.app.Application
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

/**
 * ADDCEL on 2019-12-31.
 */
class Melate : Application() {

    companion object {
        private lateinit var instance: Melate

        fun get(): Melate {
            return instance
        }
    }

    lateinit var database: Database

    override fun onCreate() {
        super.onCreate()
        val driver: SqlDriver = AndroidSqliteDriver(Database.Schema, this, "favoritos.db")
        Database.Schema.create(driver)
        database = Database(driver)

        instance = this
    }
}