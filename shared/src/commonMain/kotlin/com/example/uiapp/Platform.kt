package com.example.uiapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform