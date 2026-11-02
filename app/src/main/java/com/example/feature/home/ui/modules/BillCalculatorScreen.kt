package com.example.feature.home.ui.modules

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.ui.theme.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillCalculatorRoute(
    onNavigateBack: () -> Unit,
    onNavigateToSolarLens: () -> Unit = {},
    viewModel: BillCalculatorViewModel = viewModel()
) {
    val screenState by viewModel.screenState.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val inputError by viewModel.inputError.collectAsState()
    val billingCycle by viewModel.billingCycle.collectAsState()
    val billResult by viewModel.billResult.collectAsState()
    val calculationStep by viewModel.calculationStep.collectAsState()
    val savedCalculations by viewModel.savedCalculations.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        BillCalculatorHistoryStorage.init(context)
    }

    var showClearConfirmDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = screenState != CalculatorScreenState.ENTRY) {
        when (screenState) {
            CalculatorScreenState.CALCULATING -> viewModel.cancelCalculation()
            CalculatorScreenState.RESULT -> viewModel.navigateToEntry()
            CalculatorScreenState.HISTORY -> viewModel.navigateToEntry()
            CalculatorScreenState.ENTRY -> onNavigateBack()
        }
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Text(
                    text = "Clear Calculation History?",
                    fontWeight = FontWeight.Bold,
                    color = StitchOnSurface
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete all saved calculations? This action cannot be undone.",
                    color = StitchOnSurfaceVariant,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearHistory()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StitchError,
                        contentColor = Color.White
                    )
                ) {
                    Text("Clear All", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel", color = StitchOnSurfaceVariant)
                }
            }
        )
    }

    val topBarTitle = when (screenState) {
        CalculatorScreenState.ENTRY -> "Bill Calculator"
        CalculatorScreenState.CALCULATING -> "Bill Calculator"
        CalculatorScreenState.RESULT -> "Bill Estimate"
        CalculatorScreenState.HISTORY -> "Calculation History"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topBarTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = StitchOnSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        when (screenState) {
                            CalculatorScreenState.CALCULATING -> viewModel.cancelCalculation()
                            CalculatorScreenState.RESULT -> viewModel.navigateToEntry()
                            CalculatorScreenState.HISTORY -> viewModel.navigateToEntry()
                            CalculatorScreenState.ENTRY -> onNavigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = StitchOnSurface
                        )
                    }
                },
                actions = {
                    when (screenState) {
                        CalculatorScreenState.ENTRY -> {
                            IconButton(
                                onClick = { viewModel.navigateToHistory() },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "Calculation History",
                                    tint = StitchOnSurface
                                )
                            }
                        }
                        CalculatorScreenState.RESULT -> {
                            IconButton(
                                onClick = { viewModel.navigateToHistory() },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "Calculation History",
                                    tint = StitchOnSurface
                                )
                            }
                            IconButton(
                                onClick = { viewModel.recalculate() },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "New Calculation",
                                    tint = StitchOnSurface
                                )
                            }
                        }
                        CalculatorScreenState.HISTORY -> {
                            if (savedCalculations.isNotEmpty()) {
                                TextButton(onClick = { showClearConfirmDialog = true }) {
                                    Text(
                                        text = "Clear All",
                                        color = StitchError,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        CalculatorScreenState.CALCULATING -> {}
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StitchBackground,
                    titleContentColor = StitchOnSurface,
                    navigationIconContentColor = StitchOnSurface
                )
            )
        },
        containerColor = StitchBackground
    ) { innerPadding ->
        AnimatedContent(
            targetState = screenState,
            transitionSpec = {
                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
            },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            label = "bill_calculator_screens"
        ) { state ->
            when (state) {
                CalculatorScreenState.ENTRY -> {
                    BillCalculatorEntryView(
                        inputText = inputText,
                        inputError = inputError,
                        billingCycle = billingCycle,
                        onInputChanged = { viewModel.onInputChanged(it) },
                        onClearInput = { viewModel.clearInput() },
                        onSelectPreset = { viewModel.selectPreset(it) },
                        onSelectCycle = { viewModel.setBillingCycle(it) },
                        onCalculate = { viewModel.calculate() },
                        onNavigateToHistory = { viewModel.navigateToHistory() }
                    )
                }
                CalculatorScreenState.CALCULATING -> {
                    BillCalculatorCalculatingView(
                        unitsText = inputText.ifBlank { "0" },
                        step = calculationStep,
                        onCancel = { viewModel.cancelCalculation() }
                    )
                }
                CalculatorScreenState.RESULT -> {
                    billResult?.let { result ->
                        LaunchedEffect(result) {
                            com.example.core.review.InAppReviewHelper.recordPositiveMilestone(context)
                            (context as? android.app.Activity)?.let { activity ->
                                com.example.core.review.InAppReviewHelper.maybeLaunchReviewFlow(activity)
                            }
                        }
                        BillCalculatorResultView(
                            result = result,
                            billingCycle = billingCycle,
                            onRecalculate = { viewModel.recalculate() },
                            onNavigateToSolarLens = onNavigateToSolarLens,
                            onNavigateToHistory = { viewModel.navigateToHistory() }
                        )
                    } ?: run {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No calculation available", color = StitchOnSurfaceVariant)
                        }
                    }
                }
                CalculatorScreenState.HISTORY -> {
                    BillCalculatorHistoryView(
                        historyList = savedCalculations,
                        onSelectCalculation = { viewModel.openSavedResult(it) },
                        onDeleteItem = { viewModel.deleteHistoryItem(it) },
                        onCalculateNew = { viewModel.navigateToEntry() }
                    )
                }
            }
        }
    }
}

