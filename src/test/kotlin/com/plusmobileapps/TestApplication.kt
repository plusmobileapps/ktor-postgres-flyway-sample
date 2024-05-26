package com.plusmobileapps

import io.ktor.client.HttpClient
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication

fun myTestApplication(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
    installContentNegotiation()
    block()
}

fun ApplicationTestBuilder.createJsonClient(): HttpClient = createClient {
    this.install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
        json()
    }
}

fun ApplicationTestBuilder.installContentNegotiation() {
    install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
        json()
    }
}

