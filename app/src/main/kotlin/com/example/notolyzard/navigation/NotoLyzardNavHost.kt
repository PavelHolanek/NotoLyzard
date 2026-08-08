package com.example.notolyzard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notolyzard.feature.scalesandchords.ScalesAndChordsVisualizationScreen
import com.example.notolyzard.feature.intervalguessinggame.IntervalGuessingGameScreen

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

        composable<IntervalGuessingGameRoute> {
            IntervalGuessingGameScreen()
        }
    }
}
