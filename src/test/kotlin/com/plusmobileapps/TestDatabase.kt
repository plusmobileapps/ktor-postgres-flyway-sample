@file:OptIn(ExperimentalCoroutinesApi::class)

package com.plusmobileapps

import com.plusmobileapps.data.Database
import com.plusmobileapps.data.Dog
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.withContext
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource
import kotlin.coroutines.CoroutineContext

class TestDatabase(
    private val context: CoroutineContext = UnconfinedTestDispatcher(),
) : Database {

    private val datasource = h2DataSource()
    private val flyway = Flyway.configure()
        .dataSource(datasource)
        .cleanDisabled(false)
        .load()

    init {
        flyway.migrate()
        org.jetbrains.exposed.sql.Database.connect(datasource)
    }

    fun close() {
        flyway.clean()
        datasource.close()
    }

    override suspend fun <T> dbQuery(block: () -> T): T = withContext(context) {
        transaction { block() }
    }

    suspend fun prepopulateDogs(): List<Dog> = dbQuery {
        val dog1 = Dog.new {
            name = "Finley"
            imageUrl = "https://images.dog.ceo/breeds/labradoodle/lola.jpg"
            breed = "Australian Labradoodle"
        }
        val dog2 = Dog.new {
            name = "Lola"
            imageUrl = "https://images.dog.ceo/breeds/labrador/n02099712_9374.jpg"
            breed = "Labrador Retriever"
        }
        listOf(dog1, dog2)
    }

    private fun h2DataSource() = HikariDataSource(
        HikariConfig().apply {
            driverClassName = "org.h2.Driver"
            jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
            maximumPoolSize = 3
            isAutoCommit = true
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
    )
}