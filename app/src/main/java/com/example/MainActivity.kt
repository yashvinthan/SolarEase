package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.navigation.SolarEaseNavGraph
import com.example.ui.theme.SolarEaseTheme

class MainActivity : ComponentActivity() {
  private val destinationState = androidx.compose.runtime.mutableStateOf<Any?>(null)

  private fun resolveDestination(destStr: String?): Any? {
    return when (destStr) {
      "SOLAR_VENDOR" -> com.example.navigation.Screen.EbServiceNumber
      "SOLAR_ESTIMATE" -> com.example.navigation.Screen.EbServiceNumber
      "CUSTOMER_CATEGORY" -> com.example.navigation.Screen.CustomerCategory
      "SYSTEM_TYPE" -> com.example.navigation.Screen.SystemType
      "MONTHLY_CONSUMPTION" -> com.example.navigation.Screen.MonthlyConsumption
      "MOUNT_TYPE" -> com.example.navigation.Screen.MountType
      "AVAILABLE_AREA" -> com.example.navigation.Screen.AvailableArea
      "RECOMMENDATION" -> com.example.navigation.Screen.Recommendation
      "PANEL_SELECTION" -> com.example.navigation.Screen.PanelSelection
      "NEARBY_VENDORS" -> com.example.navigation.Screen.PanelSelection
      "VENDOR_PROFILE" -> com.example.navigation.Screen.PanelSelection
      "ESTIMATE_REQUEST" -> com.example.navigation.Screen.PanelSelection
      "REQUEST_CONFIRMATION" -> com.example.navigation.Screen.Home
      "HOME" -> com.example.navigation.Screen.Home
      "RECOMMENDS" -> com.example.navigation.Screen.Recommends
      else -> null
    }
  }

  private fun resolveIntentDestination(intent: android.content.Intent?): Any? {
    if (intent == null) return null
    val extraDest = intent.getStringExtra("START_DESTINATION")
    val fromExtra = resolveDestination(extraDest)
    if (fromExtra != null) return fromExtra

    val uri = intent.data ?: return null
    val path = uri.path?.lowercase() ?: ""
    val host = uri.host?.lowercase() ?: ""

    return when {
      path.contains("calc") || host.contains("calc") -> com.example.navigation.Screen.BillCalculator
      path.contains("meter") || host.contains("meter") -> com.example.navigation.Screen.MeterScanner
      path.contains("solar") || host.contains("solar") -> com.example.navigation.Screen.EbServiceNumber
      else -> com.example.navigation.Screen.Home
    }
  }

  override fun onNewIntent(intent: android.content.Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    val dest = resolveIntentDestination(intent)
    if (dest != null) {
      destinationState.value = dest
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    destinationState.value = resolveIntentDestination(intent)
    com.example.core.session.UserSession.init(this)
    com.example.feature.home.ui.modules.TnebBillCache.init(this)
    com.example.feature.home.ui.modules.MeterReadingCache.init(this)
    com.example.feature.home.ui.modules.BillCalculatorHistoryStorage.init(this)
    com.example.feature.lens.data.LensRoomStorage.init(this)
    com.example.feature.recommends.data.RewardStorage.init(this)
    enableEdgeToEdge()
    setContent {
      SolarEaseTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
            ) {
                val isUserSignedIn = com.example.core.session.UserSession.hasValidSession()
                val defaultDest = if (isUserSignedIn) com.example.navigation.Screen.Home else com.example.navigation.Screen.AuthGraph
                val targetDest = destinationState.value ?: defaultDest
                androidx.compose.runtime.key(targetDest) {
                    val navController = rememberNavController()
                    SolarEaseNavGraph(navController = navController, startDestination = targetDest)
                }
            }
        }
      }
    }
  }
}

