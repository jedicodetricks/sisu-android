package co.sisu.mobile.activities;


import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.sisu.mobile.R;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    Instrumentation.ActivityMonitor parentMonitor = getInstrumentation().addMonitor(ParentActivity.class.getName(), null, false);
    Instrumentation.ActivityMonitor forgotPasswordMonitor = getInstrumentation().addMonitor(ForgotPasswordActivity.class.getName(), null, false);

    @Test
    public void signInFailedTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        sleep(3500);

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.emailInput),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.emailSignInLayout),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("failureSignIn@gmail.com"), closeSoftKeyboard());

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.passwordInput),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.passwordSignInLayout),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText2.perform(replaceText("asdf123"), closeSoftKeyboard());

        sleep(200);

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.signInButton), withText("Sign In"),
                        isDisplayed()));
        appCompatButton.perform(click());


        onView(withText("Incorrect username or password"))
                .inRoot(withDecorView(not(mActivityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));

    }

    @Test
    public void signInTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        sleep(3500);

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.emailInput),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.emailSignInLayout),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("brady.groharing@live.com"), closeSoftKeyboard());

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.passwordInput),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.passwordSignInLayout),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText2.perform(replaceText("asdf123"), closeSoftKeyboard());

        sleep(200);

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.signInButton), withText("Sign In"),
                        isDisplayed()));
        appCompatButton.perform(click());

        sleep(3000);

        Activity secondActivity = getInstrumentation()
                .waitForMonitorWithTimeout(parentMonitor, 5000);
        assertNotNull(secondActivity);

//        intended(hasComponent(ParentActivity.class.getName()));
//        intended(hasComponent(new ComponentName(getTargetContext(), ParentActivity.class)));

//        ViewInteraction appCompatButton2 = onView(
//                allOf(withId(R.id.contactsProgressMark),
//                        isDisplayed()));
//        appCompatButton2.perform(click());
//
//        sleep(1000);
//
//        pressBack();

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

        Activity secondActivity = getInstrumentation()
                .waitForMonitorWithTimeout(forgotPasswordMonitor, 5000);
        assertNotNull(secondActivity);

    }

    private void sleep(int length){
        try {
            Thread.sleep(length);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
