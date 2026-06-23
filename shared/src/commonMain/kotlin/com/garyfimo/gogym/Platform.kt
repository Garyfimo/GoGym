package com.garyfimo.gogym

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform