package com.example.dsp

object Batch128Aliases {
    val MAP = mapOf(
        "max anc" to setOf("maxancnu"),
        "anc max" to setOf("maxancnu"),
        "stilte" to setOf("maxancnu", "oorpauze"),
        "gesprek" to setOf("gespreknu", "call"),
        "transparantie" to setOf("gespreknu", "call"),
        "praten" to setOf("gespreknu"),
        "wind" to setOf("windfietsplus", "bike"),
        "fiets wind" to setOf("windfietsplus", "bike"),
        "concert zacht" to setOf("stilconcert"),
        "zaal" to setOf("stilconcert", "concertzaal")
    )
}
