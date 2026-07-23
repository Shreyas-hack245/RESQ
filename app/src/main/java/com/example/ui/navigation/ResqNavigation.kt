package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.ui.components.FakeCallDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ResqViewModel

sealed class ResqRoute(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : ResqRoute("home", "SOS", Icons.Default.Warning)
    object Medical : ResqRoute("medical", "Medical ID", Icons.Default.MedicalServices)
    object Tracking : ResqRoute("tracking", "Geofence", Icons.Default.GpsFixed)
    object Vault : ResqRoute("vault", "Vault", Icons.Default.Security)
    object Volunteers : ResqRoute("volunteers", "Volunteers", Icons.Default.VolunteerActivism)
    object History : ResqRoute("history", "History", Icons.Default.History)
    object Admin : ResqRoute("admin", "Admin", Icons.Default.AdminPanelSettings)
    object Settings : ResqRoute("settings", "Privacy", Icons.Default.Lock)
    object SosActive : ResqRoute("sos_active", "Broadcast", Icons.Default.Emergency)
    object Sensors : ResqRoute("sensors", "AI Engine", Icons.Default.Psychology)
}

@Composable
fun ResqAppNavigation(
    viewModel: ResqViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ResqRoute.Home.route

    val userMessage by viewModel.userMessage.collectAsState()
    val fakeCallState by viewModel.fakeCallState.collectAsState()
    val activeIncidents by viewModel.activeIncidents.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    val navItems = listOf(
        ResqRoute.Home,
        ResqRoute.Medical,
        ResqRoute.Tracking,
        ResqRoute.Vault,
        ResqRoute.Volunteers,
        ResqRoute.History,
        ResqRoute.Settings
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (currentRoute != ResqRoute.SosActive.route) {
                NavigationBar(
                    containerColor = ResqCardBg,
                    contentColor = ResqTextPrimary,
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    navItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (item == ResqRoute.Home && activeIncidents.isNotEmpty()) {
                                            Badge(containerColor = ResqRed)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = if (selected) ResqBlue else ResqTextSecondary
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) ResqBlue else ResqTextSecondary
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = ResqBlueBg,
                                selectedIconColor = ResqBlue,
                                unselectedIconColor = ResqTextSecondary,
                                selectedTextColor = ResqBlue,
                                unselectedTextColor = ResqTextSecondary
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ResqRoute.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ResqRoute.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToActiveSos = { navController.navigate(ResqRoute.SosActive.route) },
                    onNavigateToProfile = { navController.navigate(ResqRoute.Medical.route) },
                    onNavigateToTracking = { navController.navigate(ResqRoute.Tracking.route) },
                    onNavigateToVault = { navController.navigate(ResqRoute.Vault.route) },
                    onNavigateToVolunteers = { navController.navigate(ResqRoute.Volunteers.route) },
                    onNavigateToSensors = { navController.navigate(ResqRoute.Sensors.route) },
                    onNavigateToAdmin = { navController.navigate(ResqRoute.Admin.route) }
                )
            }

            composable(ResqRoute.SosActive.route) {
                SosActiveScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(ResqRoute.Medical.route) {
                MedicalProfileScreen(viewModel = viewModel)
            }

            composable(ResqRoute.Tracking.route) {
                TrackingSafeZonesScreen(viewModel = viewModel)
            }

            composable(ResqRoute.Vault.route) {
                EvidenceVaultScreen(viewModel = viewModel)
            }

            composable(ResqRoute.Volunteers.route) {
                VolunteersScreen(viewModel = viewModel)
            }

            composable(ResqRoute.History.route) {
                IncidentHistoryScreen(viewModel = viewModel)
            }

            composable(ResqRoute.Sensors.route) {
                AiDetectionSensorsScreen(viewModel = viewModel)
            }

            composable(ResqRoute.Admin.route) {
                AdminDashboardScreen(viewModel = viewModel)
            }

            composable(ResqRoute.Settings.route) {
                SettingsPrivacyScreen(viewModel = viewModel)
            }
        }

        // Fake incoming call global overlay simulator
        FakeCallDialog(
            isRinging = fakeCallState.isRinging,
            callerName = fakeCallState.callerName,
            callerNumber = fakeCallState.callerNumber,
            isCallConnected = fakeCallState.isCallConnected,
            secondsConnected = fakeCallState.secondsConnected,
            onAnswer = { viewModel.answerFakeCall() },
            onDecline = { viewModel.endFakeCall() }
        )
    }
}
