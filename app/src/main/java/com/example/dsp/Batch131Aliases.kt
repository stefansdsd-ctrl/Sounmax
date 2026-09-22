package com.example.dsp

object Batch131Aliases {
    val MAP = mapOf(
        "huisarts" to setOf("wachtkamerhuisarts", "hospital"),
        "wachtkamer" to setOf("wachtkamerhuisarts"),
        "zwembad" to setOf("zwembadhal"),
        "badhuis" to setOf("zwembadhal"),
        "terras" to setOf("terrasavond", "cafe"),
        "buiten zitten" to setOf("terrasavond"),
        "ikea" to setOf("ikeahal", "shop"),
        "meubelhal" to setOf("ikeahal"),
        "schoolplein" to setOf("schoolplein", "kids"),
        "pauzewacht" to setOf("schoolplein"),
        "lift" to setOf("liftschacht"),
        "elevator" to setOf("liftschacht"),
        "parkeergarage" to setOf("parkeergarage", "car"),
        "kelder parkeren" to setOf("parkeergarage"),
        "kapper" to setOf("kapperstoel"),
        "kapsalon" to setOf("kapperstoel")
    )
}
