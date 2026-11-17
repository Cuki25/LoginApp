package com.example.loginapp.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.loginapp.ui.firstaid.FirstAidListScreen
import com.example.loginapp.ui.firstaid.FirstAidModuleScreen
import com.example.loginapp.ui.home.HomeScreen
import com.example.loginapp.ui.hospitals.HospitalsScreen
import com.example.loginapp.ui.lookup.LookupScreen
import com.example.loginapp.ui.profile.ProfileScreen
import com.example.loginapp.ui.speeddial.SpeedDialScreen
import com.example.loginapp.ui.symptoms.CheckSymptomsScreen
import com.example.loginapp.ui.theme.MedKitLine
import com.example.loginapp.ui.theme.MedKitPrimary
import com.example.loginapp.ui.theme.MedKitSurface

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon:  ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem("home",     "Home",     Icons.Filled.Home),
    BottomNavItem("firstaid", "First Aid", Icons.Filled.MedicalServices),
    BottomNavItem("hospitals","Hospitals", Icons.Filled.LocalHospital),
    BottomNavItem("dial",     "Dial",      Icons.Filled.Phone),
    BottomNavItem("lookup",   "Look-up",   Icons.Filled.Search),
    BottomNavItem("profile",  "Profile",   Icons.Filled.Person)
)

// Routes not shown in the bottom bar
private const val ROUTE_SYMPTOMS = "symptoms"
private const val ROUTE_MODULE   = "module/{moduleId}"
private fun moduleRoute(id: String) = "module/$id"

/**
 * Main app scaffold shown after login — contains the bottom nav bar
 * and all inner-app screens.
 */
@Composable
fun MainAppScaffold(
    email:    String,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val currentBack   by navController.currentBackStackEntryAsState()
    val currentRoute  = currentBack?.destination?.route

    // Show bottom bar only on top-level tab routes
    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MedKitSurface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick  = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState    = true
                                    }
                                }
                            },
                            icon  = {
                                Icon(item.icon, contentDescription = item.label)
                            },
                            label = { Text(item.label, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = MedKitPrimary,
                                selectedTextColor   = MedKitPrimary,
                                indicatorColor      = MedKitPrimary.copy(alpha = 0.12f),
                                unselectedIconColor = MedKitLine,
                                unselectedTextColor = MedKitLine
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController    = navController,
            startDestination = "home",
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    onCheckSymptoms = { navController.navigate(ROUTE_SYMPTOMS) },
                    onFirstAid      = { navController.navigate("firstaid") },
                    onHospitals     = { navController.navigate("hospitals") },
                    onDial          = { navController.navigate("dial") }
                )
            }
            composable("firstaid") {
                FirstAidListScreen(
                    onModuleClick = { id -> navController.navigate(moduleRoute(id)) }
                )
            }
            composable("hospitals") { HospitalsScreen() }
            composable("dial")      { SpeedDialScreen() }
            composable("lookup")    { LookupScreen() }
            composable("profile") {
                ProfileScreen(email = email, onLogout = onLogout)
            }
            composable(ROUTE_SYMPTOMS) {
                CheckSymptomsScreen(
                    onBack         = { navController.popBackStack() },
                    onModuleResult = { id ->
                        navController.navigate(moduleRoute(id)) {
                            popUpTo(ROUTE_SYMPTOMS) { inclusive = true }
                        }
                    }
                )
            }
            composable(
                route     = ROUTE_MODULE,
                arguments = listOf(navArgument("moduleId") { type = NavType.StringType })
            ) { back ->
                val moduleId = back.arguments?.getString("moduleId") ?: "bleeding"
                FirstAidModuleScreen(
                    moduleId   = moduleId,
                    onBack     = { navController.popBackStack() },
                    onDial     = { navController.navigate("dial") },
                    onHospitals = { navController.navigate("hospitals") }
                )
            }
        }
    }
}
