package com.yoke.gainful.network

import io.ktor.client.HttpClient

internal expect fun createPlatformHttpClient(): HttpClient
