package com.example.bloom.ia

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIInterpretationEngine @Inject constructor() {

    private val model = GenerativeModel(
        modelName = "gemini-pro-vision",
        apiKey = "AIzaSyC8MhcDFA4y7jF_alQRy4C41NMXpoIW75M"
    )

    private val structuredPrompt = "" +
            "Identify this object. Only if it is a plant or insect, write a fun, two-sentence fact about it. If it is NOT a plant or insect (e.g., a hand, a shoe, a building), set the Name to 'NOT_RELEVANT' and the Fact to 'Image does not contain a plant or insect.' Structure the output STRICTLY like this: Nom: [Name]\\nFait: [Fact]"

    suspend fun identifyPlant(bitmap: Bitmap): Pair<String, String> {
        return try {
            val response = model.generateContent(
                content {
                    image(bitmap)
                    text(structuredPrompt)
                }
            )

            //Parse la réponse structurée
            val lines = response.text?.lines() ?: emptyList()
            val nameLine = lines.find { it.startsWith("NAME:") }
            val factLine = lines.find { it.startsWith("FACT:") }

            val name = nameLine?.substringAfter("NAME:")?.trim() ?: "Unknown"
            val fact = factLine?.substringAfter("FACT:")?.trim() ?: "No facts found."

            Pair(name, fact)
        } catch (e: Exception) {
            println("AI Error: ${e.message}")
            Pair("AI_ERROR", "The analysis failed: ${e.message}")
        }
    }
}