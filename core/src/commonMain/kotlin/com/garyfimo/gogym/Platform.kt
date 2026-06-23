package com.garyfimo.gogym

interface Platform {
    val name: String
    val apiBaseUrl: String
}

expect fun getPlatform(): Platform