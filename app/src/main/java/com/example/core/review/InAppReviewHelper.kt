package com.example.core.review

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.play.core.review.ReviewManagerFactory

/**
 * Manages Google Play In-App Review requests based on positive sentiment milestones.
 * Adheres strictly to the ASO rating-prompt-strategy:
 * - Never prompts on cold start or after errors.
 * - Prompts only after meaningful value milestones (Bill Calculated, Solar Assessment Completed).
 * - Enforces minimum cooldown of 14 days between prompts.
 */
object InAppReviewHelper {
    private const val TAG = "InAppReviewHelper"
    private const val PREFS_NAME = "solarease_review_prefs"
    private const val KEY_LAST_PROMPT_TIME = "last_review_prompt_time"
    private const val KEY_POSITIVE_ACTIONS_COUNT = "positive_actions_count"
    private const val COOLDOWN_DAYS_MS = 14L * 24 * 60 * 60 * 1000

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Records a positive user milestone (e.g., successful TNEB bill calculation,
     * rooftop solar estimate generated, or meter scan logged).
     */
    fun recordPositiveMilestone(context: Context) {
        val prefs = getPrefs(context)
        val count = prefs.getInt(KEY_POSITIVE_ACTIONS_COUNT, 0) + 1
        prefs.edit().putInt(KEY_POSITIVE_ACTIONS_COUNT, count).apply()
    }

    /**
     * Checks eligibility and launches the native Google Play In-App Review flow if applicable.
     */
    fun maybeLaunchReviewFlow(activity: Activity) {
        val prefs = getPrefs(activity)
        val positiveCount = prefs.getInt(KEY_POSITIVE_ACTIONS_COUNT, 0)
        val lastPromptTime = prefs.getLong(KEY_LAST_PROMPT_TIME, 0L)
        val currentTime = System.currentTimeMillis()

        // Require at least 2 positive actions and 14-day cooldown
        if (positiveCount < 2) {
            Log.d(TAG, "Review flow skipped: positiveCount=$positiveCount < 2")
            return
        }
        if (currentTime - lastPromptTime < COOLDOWN_DAYS_MS) {
            Log.d(TAG, "Review flow skipped: Cooldown period active")
            return
        }

        try {
            val manager = ReviewManagerFactory.create(activity)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(activity, reviewInfo)
                    flow.addOnCompleteListener {
                        // Regardless of whether the user rated or dismissed, record timestamp
                        prefs.edit()
                            .putLong(KEY_LAST_PROMPT_TIME, currentTime)
                            .putInt(KEY_POSITIVE_ACTIONS_COUNT, 0)
                            .apply()
                        Log.d(TAG, "Review flow completed successfully")
                    }
                } else {
                    Log.w(TAG, "Review flow request failed: ${task.exception?.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initiating in-app review", e)
        }
    }
}

