package com.plusmobileapps.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource
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
        migrate(datasource)
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

    private fun migrate(dataSource: DataSource) {
        try {
            val flyway = Flyway.configure()
                .dataSource(dataSource)
                .load()
            flyway.migrate()
        } catch (e: FlywayException) {
            LOGGER.error("Failed to migrate database", e)
        }
    }
}

private val LOGGER = KtorSimpleLogger("Database")