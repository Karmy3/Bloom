package com.example.bloom.model.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Entity(tableName = "discoveries")
@Serializable
data class Discovery(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val fact: String,
    val imageUrl: String,
    val localImagePath: String,
    val timestamp: Long,
    val userId: String
)