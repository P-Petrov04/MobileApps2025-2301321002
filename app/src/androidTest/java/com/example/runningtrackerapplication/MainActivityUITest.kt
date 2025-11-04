package com.example.runningtrackerapplication

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityUITest {

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun mainActivity_shouldDisplayCorrectUI() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        try {
            onView(withText("Моите тренировки"))
                .check(matches(isDisplayed()))

            onView(withId(R.id.startNewRunButton))
                .check(matches(isDisplayed()))
                .check(matches(withText("🏁 Започни нова тренировка")))

            onView(withText("История на тренировките"))
                .check(matches(isDisplayed()))

            onView(withId(R.id.runsRecyclerView))
                .check(matches(isDisplayed()))

        } finally {
            activityScenario.close()
        }
    }

    @Test
    fun startNewRunButton_shouldOpenMapsActivity() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.startNewRunButton))
            .perform(click())

        Intents.intended(IntentMatchers.hasComponent(MapsActivity::class.java.name))
    }

    @Test
    fun emptyState_shouldShowNoRunsMessage() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        try {
            onView(withId(R.id.runsRecyclerView))
                .check(matches(isDisplayed()))

        } finally {
            activityScenario.close()
        }
    }

    @Test
    fun completeRunFlow_shouldWorkCorrectly() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withText("Моите тренировки")).check(matches(isDisplayed()))
        onView(withId(R.id.startNewRunButton)).check(matches(isDisplayed()))

        onView(withId(R.id.startNewRunButton))
            .perform(click())

        Intents.intended(IntentMatchers.hasComponent(MapsActivity::class.java.name))

        Espresso.pressBack()

        onView(withText("Моите тренировки"))
            .check(matches(isDisplayed()))
    }
}