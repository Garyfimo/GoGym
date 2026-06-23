package com.garyfimo.gogym.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import com.garyfimo.gogym.model.Exercise
import com.garyfimo.gogym.Platform

class ExerciseApi(
    private val client: HttpClient,
    private val platform: Platform
) {
    suspend fun getExercises(): List<Exercise> {
        return client.get("${platform.apiBaseUrl}/api/exercises").body()
    }
}
