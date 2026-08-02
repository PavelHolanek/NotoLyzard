package com.example.notolyzard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notolyzard.feature.scalesandchords.ScalesAndChordsVisualizationScreen
import com.example.notolyzard.feature.scaleviewer.ScaleViewerScreen

/**
 * The app's single navigation graph.
 *
 * Screens never receive the [NavHostController]; they expose navigation events as lambdas
 * and this graph decides where those lead. That keeps screens previewable and testable
 * without a navigation host.
 */
@Composable
fun NotoLyzardNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = ScalesAndChordsVisualizationRoute,
        modifier = modifier,
    ) {
        composable<ScalesAndChordsVisualizationRoute> {
            ScalesAndChordsVisualizationScreen()
        }

        composable<ScaleViewerRoute> {
            ScaleViewerScreen()
        }

        // TODO: register further destinations here.
    }
}
