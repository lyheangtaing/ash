package com.lyheang.ash

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform