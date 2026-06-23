package com.garyfimo.gogym.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import com.garyfimo.gogym.Greeting
import com.garyfimo.gogym.getPlatform
import com.garyfimo.gogym.api.ExerciseApi
import com.garyfimo.gogym.config.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val commonModule = module {
    single { Greeting() }
    single { getPlatform() }
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    coerceInputValues = true
                })
            }
        }
    }
    // ExerciseApi now injects HttpClient and AppConfig
    single { ExerciseApi(get(), get()) }
}

fun initKoin(appConfig: AppConfig, appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        module {
            single { appConfig }
        },
        commonModule
    )
}

// iOS/Preview entry point helper with default mock config
fun initKoin() = initKoin(
    AppConfig(
        baseUrl = "http://localhost:8080",
        environment = "MOCK"
    )
) {}

// Custom config overload for platform calls
fun initKoin(appConfig: AppConfig) = initKoin(appConfig) {}
