package com.plusmobileapps

import com.plusmobileapps.data.Dog
import com.plusmobileapps.data.DogDto
import com.plusmobileapps.routes.DOGS_ENDPOINT
import com.plusmobileapps.routes.DogsResponse
import com.plusmobileapps.routes.dogRoutes
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.UUID

class DogRouteTest {

    private lateinit var database: TestDatabase

    @Before
    fun setUp() {
        database = TestDatabase()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `GET dogs returns list of dogs`() {
        myTestApplication {
            val client = createJsonClient()
            val dogs = database.prepopulateDogs()

            routing {
                dogRoutes(database)
            }
            val response: HttpResponse = client.get(DOGS_ENDPOINT)
            response.status shouldBe HttpStatusCode.OK
            response.body<DogsResponse>() shouldBe DogsResponse(dogs.map { DogDto.fromEntity(it) })
        }
    }

    @Test
    fun `POST dog creates a new dog in the database`() {
        myTestApplication {
            val client = createJsonClient()
            val dog = DogDto(
                uuid = "",
                name = "Finley",
                imageUrl = "https://images.dog.ceo/breeds/labradoodle/lola.jpg",
                breed = "Australian Labradoodle",
            )

            routing {
                dogRoutes(database)
            }
            val response: HttpResponse = client.post(DOGS_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(dog)
            }
            val createdDog = response.body<DogDto>()
            response.status shouldBe HttpStatusCode.OK

            with(createdDog) {
                name shouldBe dog.name
                imageUrl shouldBe dog.imageUrl
                breed shouldBe dog.breed
            }

            val dogInDb = database.dbQuery {
                val dogs = Dog.all().toList()
                dogs.size shouldBe 1
                dogs.first()
            }

            dogInDb.id.value shouldBe UUID.fromString(createdDog.uuid)
        }
    }
}