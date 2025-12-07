package com.example.bloom.model.model.entities

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SupabaseDiscovery(
    @SerialName("user_id") val userId: String,
    val name: String,
    val summary: String,
    @SerialName("image_url") val imageUrl: String,
    val timestamp: Long,
    @SerialName("search_text") val searchText: String
)