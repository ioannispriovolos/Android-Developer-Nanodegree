package com.ioannispriovolos.android.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityScreenTest {

    private static final String NUTELLA_CAKE = "Nutella Pie";

    private static final String  BROWNIES = "Brownies";

    private static final String TITLE = "Recipes";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void cakeTitleCheck(){
        onView((withId(R.id.cake_recipe_list_title))).check(matches(withText(TITLE)));
    }

    @Test
    public void clickRecyclerViewItem_Nutella_OpensCakeDetailActivity(){

        onView(withId(R.id.cake_recycle__list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.tv_detail_title)).check(matches(withText(NUTELLA_CAKE)));

    }

    @Test
    public void clickRecyclerViewItem_Brownies_OpensCakeDetailActivity(){

        onView(withId(R.id.cake_recycle__list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.tv_detail_title)).check(matches(withText(BROWNIES)));
    }
}