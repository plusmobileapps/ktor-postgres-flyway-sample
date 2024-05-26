package com.plusmobileapps.plugins

import com.plusmobileapps.data.Database
import com.plusmobileapps.routes.dogRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting(database: Database) {
    routing {
        dogRoutes(database)
    }
}
