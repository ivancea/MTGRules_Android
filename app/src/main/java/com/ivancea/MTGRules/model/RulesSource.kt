package com.ivancea.MTGRules.model

import java.net.URI
import java.nio.charset.Charset
import java.time.LocalDate

data class RulesSource(
    val uri: URI,
    val date: LocalDate,
    val encoding: Charset
)
