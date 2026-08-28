package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eq_presets")
data class EqPresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isCustom: Boolean = true,
    val category: String = "Aangepast",
    val bandGains: String, // Comma separated 10 float values in dB (-12 to +12)
    val bassBoost: Int = 0, // 0 - 1000
    val virtualizer: Int = 0, // 0 - 1000
    val loudness: Int = 0, // 0 - 1000
    val clarity: Float = 0f, // 0 - 10
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "hearing_profiles")
data class HearingProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileName: String,
    val leftGains: String, // Comma separated hearing threshold compensation
    val rightGains: String,
    val scorePercent: Int = 100,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_tracks")
data class SavedTrackEntity(
    @PrimaryKey val videoId: String,
    val title: String,
    val artist: String,
    val genre: String,
    val recommendedPreset: String,
    val timestamp: Long = System.currentTimeMillis()
)
