package com.example.notolyzard.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.notolyzard.ui.theme.LocalNotePalette

@Composable
fun NotoLyzardApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LocalNotePalette.current.background,
        topBar = {
            TopLevelNavigationBar(
                currentDestination = backStackEntry?.destination,
                onDestinationSelected = navController::navigateToTopLevel,
            )
        },
    ) { innerPadding ->
        NotoLyzardNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun TopLevelNavigationBar(
    currentDestination: NavDestination?,
    onDestinationSelected: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedIndex = TopLevelDestination.entries
        .indexOfFirst { it.isCurrent(currentDestination) }
        // -1 while the NavHost is still composing, or when a destination outside the bar is
        // open; the tab row needs a real index to place its indicator.
        .coerceAtLeast(0)

    Surface(color = MaterialTheme.colorScheme.surface, modifier = modifier) {
        PrimaryTabRow(
            selectedTabIndex = selectedIndex,
            containerColor = Color.Transparent,
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
        ) {
            TopLevelDestination.entries.forEachIndexed { index, destination ->
                Tab(
                    selected = index == selectedIndex,
                    onClick = { onDestinationSelected(destination) },
                    text = { Text(text = destination.label) },
                )
            }
        }
    }
}

private fun TopLevelDestination.isCurrent(destination: NavDestination?): Boolean =
    destination?.hierarchy?.any { it.hasRoute(route::class) } == true

private fun NavHostController.navigateToTopLevel(destination: TopLevelDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
