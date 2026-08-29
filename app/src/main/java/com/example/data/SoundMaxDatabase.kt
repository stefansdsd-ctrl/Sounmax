package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface EqPresetDao {
    @Query("SELECT * FROM eq_presets ORDER BY isFavorite DESC, lastUsedAt DESC, id ASC")
    fun getAllPresets(): Flow<List<EqPresetEntity>>

    @Query("SELECT * FROM eq_presets WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavorites(): Flow<List<EqPresetEntity>>

    @Query("SELECT * FROM eq_presets WHERE lastUsedAt > 0 ORDER BY lastUsedAt DESC LIMIT 8")
    fun getRecent(): Flow<List<EqPresetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: EqPresetEntity): Long

    @Delete
    suspend fun deletePreset(preset: EqPresetEntity)

    @Query("DELETE FROM eq_presets WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE eq_presets SET isFavorite = :fav WHERE id = :id")
    suspend fun setFavorite(id: Long, fav: Boolean)

    @Query("UPDATE eq_presets SET lastUsedAt = :ts WHERE id = :id")
    suspend fun touchUsed(id: Long, ts: Long)
}

@Dao
interface HearingProfileDao {
    @Query("SELECT * FROM hearing_profiles ORDER BY createdAt DESC")
    fun getAllProfiles(): Flow<List<HearingProfileEntity>>

    @Query("SELECT * FROM hearing_profiles ORDER BY createdAt DESC LIMIT 1")
    fun getLatestProfile(): Flow<HearingProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: HearingProfileEntity): Long

    @Delete
    suspend fun deleteProfile(profile: HearingProfileEntity)
}

@Dao
interface SavedTrackDao {
    @Query("SELECT * FROM saved_tracks ORDER BY timestamp DESC")
    fun getAllSavedTracks(): Flow<List<SavedTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: SavedTrackEntity)

    @Delete
    suspend fun deleteTrack(track: SavedTrackEntity)
}

@Database(
    entities = [EqPresetEntity::class, HearingProfileEntity::class, SavedTrackEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SoundMaxDatabase : RoomDatabase() {
    abstract fun eqPresetDao(): EqPresetDao
    abstract fun hearingProfileDao(): HearingProfileDao
    abstract fun savedTrackDao(): SavedTrackDao

    companion object {
        @Volatile
        private var INSTANCE: SoundMaxDatabase? = null

        fun getDatabase(context: Context): SoundMaxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoundMaxDatabase::class.java,
                    "soundmax_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
