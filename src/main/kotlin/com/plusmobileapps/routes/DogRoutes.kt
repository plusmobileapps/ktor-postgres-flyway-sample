package com.plusmobileapps.routes

import com.plusmobileapps.data.Database
import com.plusmobileapps.data.Dog
import com.plusmobileapps.data.DogDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import java.util.UUID

const val DOGS_ENDPOINT = "/dogs"

fun Route.dogRoutes(database: Database) {
    getDogs(database)
    addDog(database)
    deleteDog(database)
}

private fun Route.getDogs(database: Database) {
    get(DOGS_ENDPOINT) {
        val dogs = database.dbQuery {
            Dog.all().toList().map { DogDto.fromEntity(it) }
        }
        call.respond(HttpStatusCode.OK, mapOf("dogs" to dogs))
    }
}

private fun Route.addDog(database: Database) {
    post(DOGS_ENDPOINT) {
        try {
            val dog = call.receive<DogDto>()
            val newDog = database.dbQuery {
                Dog.new {
                    name = dog.name
                    imageUrl = dog.imageUrl
                }
            }
            call.respond(HttpStatusCode.OK, DogDto.fromEntity(newDog))
        } catch (e: Exception) {
            println(e.message)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}

private fun Route.deleteDog(database: Database) {
    delete(DOGS_ENDPOINT) {
        val dogId = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        try {
            database.dbQuery {
                Dog.findById(UUID.fromString(dogId))?.delete()
            }
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError)
        }

    }
}