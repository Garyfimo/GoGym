package com.garyfimo.gogym.workout.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import com.garyfimo.gogym.config.AppConfig
import com.garyfimo.gogym.workout.model.WorkoutSession
import kotlinx.serialization.Serializable

@Serializable
data class SaveWorkoutResponse(
    val status: String,
    val message: String
)

class WorkoutApi(
    private val client: HttpClient,
    private val appConfig: AppConfig
) {
    suspend fun saveWorkout(workoutSession: WorkoutSession): SaveWorkoutResponse {
        return client.post("${appConfig.baseUrl}/api/workouts") {
            contentType(ContentType.Application.Json)
            setBody(workoutSession)
        }.body()
    }
}
