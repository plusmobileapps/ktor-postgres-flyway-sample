package com.plusmobileapps

import com.plusmobileapps.data.Database
import com.plusmobileapps.data.DatabaseImpl
import com.plusmobileapps.plugins.configureContentNegotiation
import com.plusmobileapps.plugins.configureRouting
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val database: Database = DatabaseImpl()
    configureContentNegotiation()
    configureRouting(database)
}
