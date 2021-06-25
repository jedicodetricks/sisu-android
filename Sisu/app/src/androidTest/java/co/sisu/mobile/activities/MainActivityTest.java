package co.sisu.mobile.activities;


import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.sisu.mobile.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

//    Instrumentation.ActivityMonitor parentMonitor = getInstrumentation().addMonitor(ParentActivity.class.getName(), null, false);
//    Instrumentation.ActivityMonitor forgotPasswordMonitor = getInstrumentation().addMonitor(ForgotPasswordActivity.class.getName(), null, false);

    private String testEmail;
    private String failEmail;
    private String testPassword;
//    private View decorView;

    @Before
    public void setup() {
        testEmail = "brady.groharing@live.com";
        failEmail = "failure@live.com";
        testPassword = "asdf123";
//        activityScenarioRule.getScenario().onActivity(activity -> decorView = activity.getWindow().getDecorView());
    }

    @After
    public void after() {
        // This is needed to end the test I guess?
//        @Override
//        onPause()
    }


    @Test
    public void signInFailedTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        sleep(2000);

        onView(withId(R.id.emailInput)).perform(typeText(failEmail), closeSoftKeyboard());
        onView(withId(R.id.emailInput)).check(matches(withText(failEmail)));

        onView(withId(R.id.passwordInput)).perform(typeText(testPassword), closeSoftKeyboard());
        sleep(200);

        onView(withId(R.id.signInButton)).perform(click());

//        onView(withText("Incorrect username or password"))
//                .inRoot(withDecorView(not(decorView)))// Here we use decorView
//                .check(matches(isDisplayed()));
    }

    @Test
    public void signInSuccessTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        sleep(2000);

        onView(withId(R.id.emailInput)).perform(typeText(testEmail), closeSoftKeyboard());
        onView(withId(R.id.emailInput)).check(matches(withText(testEmail)));

        onView(withId(R.id.passwordInput)).perform(typeText(testPassword), closeSoftKeyboard());
        sleep(200);

        onView(withId(R.id.signInButton)).perform(click());

        sleep(4500);
        onView(withId(R.id.your_placeholder)).check(matches(isDisplayed()));
//        Activity secondActivity = getInstrumentation()
//                .waitForMonitorWithTimeout(parentMonitor, 5000);
//        assertNotNull(secondActivity);

    }

    @Test
    public void forgotPasswordTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        sleep(3500);

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.forgotPassword),
                        isDisplayed()));
        appCompatButton.perform(click());

//        Activity secondActivity = getInstrumentation()
//                .waitForMonitorWithTimeout(forgotPasswordMonitor, 5000);
//        assertNotNull(secondActivity);

    }

    private void sleep(int length){
        try {
            Thread.sleep(length);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
