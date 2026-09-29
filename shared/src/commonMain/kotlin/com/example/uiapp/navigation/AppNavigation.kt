package com.example.uiapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

class AppNavController(val initialRoute: String = "home") {
    var backstack by mutableStateOf(listOf(initialRoute))
        private set

    val currentRoute: String
        get() = backstack.lastOrNull() ?: initialRoute

    val canPop: Boolean
        get() = backstack.size > 1

    fun navigate(route: String) {
        if (route == initialRoute) {
            popToRoot()
            return
        }
        if (backstack.lastOrNull() != route) {
            backstack = backstack + route
        }
    }

    fun pop(): Boolean {
        if (backstack.size > 1) {
            backstack = backstack.dropLast(1)
            return true
        }
        return false
    }

    fun popTo(route: String): Boolean {
        val index = backstack.indexOfLast { it == route }
        if (index >= 0) {
            backstack = backstack.take(index + 1)
            return true
        }
        return false
    }

    fun replace(route: String) {
        if (backstack.isNotEmpty()) {
            backstack = backstack.dropLast(1) + route
        } else {
            backstack = listOf(route)
        }
    }

    fun popToRoot() {
        if (backstack.size > 1 || backstack.firstOrNull() != initialRoute) {
            backstack = listOf(initialRoute)
        }
    }

    fun resetTo(route: String) {
        backstack = listOf(route)
    }
}

@Composable
fun rememberAppNavController(initialRoute: String = "home"): AppNavController {
    return remember { AppNavController(initialRoute) }
}

val LocalNavController = staticCompositionLocalOf { AppNavController("home") }
