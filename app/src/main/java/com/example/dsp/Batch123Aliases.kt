package com.example.dsp

object Batch123Aliases {
    val MAP = mapOf(
        "laadpaal" to setOf("laadpaalwacht"),
        "laden" to setOf("laadpaalwacht"),
        "ev" to setOf("laadpaalwacht"),
        "schoolpoort" to setOf("schoolpoort"),
        "schoolplein" to setOf("schoolpoort", "playground"),
        "ophalen" to setOf("schoolpoort", "pickupkids"),
        "fietsenstalling" to setOf("stationsstalling"),
        "stalling" to setOf("stationsstalling"),
        "ovfiets" to setOf("stationsstalling"),
        "taxi" to setOf("nachttaxi"),
        "uber" to setOf("nachttaxi"),
        "bolt" to setOf("nachttaxi"),
        "intercom" to setOf("thuisintercom"),
        "deurbel" to setOf("thuisintercom", "alert"),
        "ikeakids" to setOf("ikeakinderhoek"),
        "smaland" to setOf("ikeakinderhoek"),
        "ziekenhuislift" to setOf("ziekenhuislift"),
        "liftziekenhuis" to setOf("ziekenhuislift"),
        "parkeerautomaat" to setOf("parkeerautomaat"),
        "betaalautomaat" to setOf("parkeerautomaat")
    )
}
