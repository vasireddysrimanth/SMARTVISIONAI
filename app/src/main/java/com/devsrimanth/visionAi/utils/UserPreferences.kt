package com.devsrimanth.visionAi.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore wrapper for persisting user preferences.
 * Saves: confidence threshold + GPU toggle setting.
 * Survives app restarts — user doesn't need to reset every time.
 */

/** Extension property — creates DataStore instance once */
private val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(name = "smart_camera_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val CONFIDENCE_THRESHOLD = floatPreferencesKey("confidence_threshold")
        val USE_GPU = booleanPreferencesKey("use_gpu")
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
    }

    /** Read confidence threshold — default 0.5 (50%) */
    val confidenceThreshold: Flow<Float> = context.dataStore.data
        .map { prefs ->
            prefs[CONFIDENCE_THRESHOLD] ?: 0.5f
        }

    /** Read GPU toggle — default true */
    val useGpu: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[USE_GPU] ?: true
        }

    /** Save confidence threshold */
    suspend fun saveConfidenceThreshold(value: Float) {
        context.dataStore.edit { prefs ->
            prefs[CONFIDENCE_THRESHOLD] = value
        }
    }

    /** Save GPU preference */
    suspend fun saveUseGpu(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[USE_GPU] = value
        }
    }


    /** Add this flow */
    val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[HAS_SEEN_ONBOARDING] ?: false
        }

    /** Add this suspend function */
    suspend fun setOnboardingComplete() {
        context.dataStore.edit { prefs ->
            prefs[HAS_SEEN_ONBOARDING] = true
        }
    }
}