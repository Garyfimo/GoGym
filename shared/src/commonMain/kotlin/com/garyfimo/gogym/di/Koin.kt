package com.garyfimo.gogym.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import com.garyfimo.gogym.Greeting
import com.garyfimo.gogym.getPlatform
import com.garyfimo.gogym.api.ExerciseApi
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
    single { ExerciseApi(get(), get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(commonModule)
}

// iOS entry point helper
fun initKoin() = initKoin {}
