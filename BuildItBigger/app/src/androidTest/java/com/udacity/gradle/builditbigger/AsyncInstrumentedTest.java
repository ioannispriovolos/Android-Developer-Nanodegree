package com.udacity.gradle.builditbigger;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

// https://developer.android.com/training/testing/espresso/setup
@RunWith(AndroidJUnit4.class)
public class AsyncInstrumentedTest {

    String msg;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void asyncTaskTest() {


        ViewInteraction appCompatButton = onView(allOf(withText("Tell a Joke"), withParent(allOf(withId(R.id.fragment), withParent(withId(android.R.id.content)))), isDisplayed()));
        appCompatButton.perform(click());


        ViewInteraction textView = onView(allOf(withId(R.id.tv_joke), isDisplayed()));

        textView.check(matches(withText("You know you're ugly when you get handed the camera every time they make a group photo. - Ioannis")));

    }
}
