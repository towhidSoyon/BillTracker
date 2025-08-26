package com.towhid.billtracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.towhid.billtracker.presentation.subscription.edit.EditScreen
import com.towhid.billtracker.presentation.subscription.list.ListScreen

object Routes {
    const val LIST = "list"
    const val EDIT = "edit"
}

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            ListScreen(onAdd = { nav.navigate("${Routes.EDIT}?id=-1") }, onEdit = { id -> nav.navigate("${Routes.EDIT}?id=$id") })
        }
        composable(
            route = "${Routes.EDIT}?id={id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
        ) { back ->
            val id = back.arguments?.getLong("id") ?: -1L
            EditScreen(id = id, onDone = { nav.popBackStack() })
        }
    }
}
