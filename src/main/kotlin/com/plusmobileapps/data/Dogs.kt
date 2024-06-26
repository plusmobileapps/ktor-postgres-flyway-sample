package com.plusmobileapps.data

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

@Serializable
data class DogDto(
    val uuid: String,
    val name: String,
    val breed: String?,
    val imageUrl: String?,
) {
    companion object {
        fun fromEntity(dog: Dog): DogDto = DogDto(
            uuid = dog.id.value.toString(),
            name = dog.name,
            breed = dog.breed,
            imageUrl = dog.imageUrl,
        )
    }
}

object Dogs : UUIDTable() {
    val name = varchar("dog_name", 50).index()
    val imageUrl = text("image_url").nullable()
    val breed = text("breed").nullable()
}

class Dog(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<Dog>(Dogs)

    var name: String by Dogs.name
    var imageUrl: String? by Dogs.imageUrl
    var breed: String? by Dogs.breed
}