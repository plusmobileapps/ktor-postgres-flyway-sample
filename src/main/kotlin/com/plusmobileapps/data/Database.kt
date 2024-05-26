package com.plusmobileapps.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.coroutines.CoroutineContext

interface Database {
    suspend fun <T> dbQuery(block: () -> T): T
}

class DatabaseImpl(
    private val ioContext: CoroutineContext = Dispatchers.IO,
    private val jdbcUrl: String = System.getenv("JDBC_DATABASE_URL"),
) : com.plusmobileapps.data.Database {

    init {
        val datasource = hikariDataSource()
        Database.connect(datasource)
    }

    override suspend fun <T> dbQuery(block: () -> T): T = withContext(ioContext) {
        transaction { block() }
    }

    private fun hikariDataSource(): HikariDataSource = HikariDataSource(
        HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = this@DatabaseImpl.jdbcUrl
            maximumPoolSize = 3
            isAutoCommit = true
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
    )
}