package com.example.uiapp.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * `AppNavController` adalah tulang punggung navigasi seluruh 71 lab. Test ini mengunci
 * kontrak backstack-nya, termasuk perilaku anti-duplikat pada [AppNavController.navigate].
 */
class AppNavigationTest {

    @Test
    fun startsAtInitialRouteWithoutBackstack() {
        val nav = AppNavController("home")
        assertEquals("home", nav.currentRoute)
        assertFalse(nav.canPop)
    }

    @Test
    fun navigatePushesRouteAndEnablesBack() {
        val nav = AppNavController("home")
        nav.navigate("charts")
        assertEquals("charts", nav.currentRoute)
        assertTrue(nav.canPop)
        nav.navigate("stats")
        assertEquals(listOf("home", "charts", "stats"), nav.backstack)
    }

    @Test
    fun navigateIgnoresConsecutiveDuplicateRoute() {
        val nav = AppNavController("home")
        nav.navigate("charts")
        nav.navigate("charts")
        assertEquals(2, nav.backstack.size)
        assertEquals("charts", nav.currentRoute)
    }

    @Test
    fun navigateToInitialRoutePopsAllTheWayBack() {
        val nav = AppNavController("home")
        nav.navigate("charts")
        nav.navigate("stats")
        nav.navigate("home")
        assertEquals(listOf("home"), nav.backstack)
        assertFalse(nav.canPop)
    }

    @Test
    fun popRemovesTopEntryAndStopsAtRoot() {
        val nav = AppNavController("home")
        nav.navigate("charts")
        assertTrue(nav.pop())
        assertEquals("home", nav.currentRoute)
        assertFalse(nav.pop())
        assertEquals(listOf("home"), nav.backstack)
    }

    @Test
    fun popToTruncatesBackstackToRequestedRoute() {
        val nav = AppNavController("home")
        nav.navigate("charts")
        nav.navigate("stats")
        nav.navigate("theme_lab")
        assertTrue(nav.popTo("stats"))
        assertEquals(listOf("home", "charts", "stats"), nav.backstack)
    }

    @Test
    fun popToMissingRouteKeepsBackstackIntact() {
        val nav = AppNavController("home")
        nav.navigate("charts")
        assertFalse(nav.popTo("tidak_ada"))
        assertEquals(listOf("home", "charts"), nav.backstack)
    }

    @Test
    fun replaceSwapsOnlyTopEntry() {
        val nav = AppNavController("home")
        nav.navigate("charts")
        nav.navigate("stats")
        nav.replace("theme_lab")
        assertEquals(listOf("home", "charts", "theme_lab"), nav.backstack)
    }

    @Test
    fun resetToReplacesWholeBackstack() {
        val nav = AppNavController("home")
        nav.navigate("charts")
        nav.resetTo("onboarding")
        assertEquals(listOf("onboarding"), nav.backstack)
        assertFalse(nav.canPop)
    }

    @Test
    fun popToRootReturnsToInitialRoute() {
        val nav = AppNavController("home")
        nav.navigate("charts")
        nav.navigate("stats")
        nav.popToRoot()
        assertEquals("home", nav.currentRoute)
        assertFalse(nav.canPop)
    }

    @Test
    fun customInitialRouteIsRespected() {
        val nav = AppNavController("onboarding")
        nav.navigate("auth")
        assertEquals("auth", nav.currentRoute)
        nav.navigate("onboarding")
        assertEquals(listOf("onboarding"), nav.backstack)
    }
}
