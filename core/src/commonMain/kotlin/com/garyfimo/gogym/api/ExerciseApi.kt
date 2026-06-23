package com.garyfimo.gogym.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import com.garyfimo.gogym.model.Exercise
import com.garyfimo.gogym.config.AppConfig

class ExerciseApi(
    private val client: HttpClient,
    private val appConfig: AppConfig
) {
    suspend fun getExercises(): List<Exercise> {
        return client.get("${appConfig.baseUrl}/api/exercises").body()
    }

    suspend fun getExercise(id: String): Exercise {
        return client.get("${appConfig.baseUrl}/api/exercises/$id").body()
    }
}
