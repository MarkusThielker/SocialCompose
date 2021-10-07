package com.example.social_compose.server

object ServerConfig {

    // server setup
    const val host = "localhost"
    const val port = 8080

    // authentication
    const val issuer = "https://server.markus-thielker.de"
    const val secret = "secret"
    const val validity = 900000 // ms -> 15 minutes
}
