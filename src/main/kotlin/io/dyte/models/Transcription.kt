package io.dyte.models

import kotlinx.serialization.Serializable

@Serializable
data class Transcription(
    val transcription: String
)