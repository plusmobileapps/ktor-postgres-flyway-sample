package com.plusmobileapps

import com.plusmobileapps.data.DogDto
import com.plusmobileapps.routes.DOGS_ENDPOINT
import com.plusmobileapps.routes.DogsResponse
import com.plusmobileapps.routes.dogRoutes
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import kotlin.test.assertEquals

class DogRouteTest {

    @Test
    fun `GET dogs returns list of dogs`() {
        testApplication {
            val client = createClient {
                this.install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                    json()
                }
            }
            val database = TestDatabase()
            val dogs = database.prepopulateDogs()
            install(ContentNegotiation) {
                json()
            }
            routing {
                dogRoutes(database)
            }
            val response: HttpResponse = client.get(DOGS_ENDPOINT)
            assertEquals(response.body(), DogsResponse(dogs.map { DogDto.fromEntity(it) }))
        }
    }
}