package com.example.dsp

object Batch126Aliases {
    val MAP = mapOf(
        "tram" to setOf("tramconducteur", "tramhaltewacht"),
        "conducteur" to setOf("tramconducteur"),
        "tankstation" to setOf("tankstation"),
        "pomp" to setOf("tankstation"),
        "benzine" to setOf("tankstation"),
        "wasstraat" to setOf("wasstraat"),
        "autowas" to setOf("wasstraat"),
        "postkantoor" to setOf("postkantoor"),
        "brieven" to setOf("postkantoor", "brievenbusrij"),
        "sportschool" to setOf("sportschoolgang"),
        "gym gang" to setOf("sportschoolgang"),
        "avondbus" to setOf("bushalteavond"),
        "bushalte" to setOf("bushalteavond"),
        "kelder" to setOf("kelderberging"),
        "berging" to setOf("kelderberging"),
        "bezorging" to setOf("thuisbezorging"),
        "thuisbezorgd" to setOf("thuisbezorging"),
        "deurbel wacht" to setOf("thuisbezorging", "thuisintercom")
    )
}
