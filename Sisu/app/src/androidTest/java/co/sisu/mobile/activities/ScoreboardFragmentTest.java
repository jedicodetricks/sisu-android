package co.sisu.mobile.activities;


import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.gson.Gson;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.sisu.mobile.R;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.oldFragments.ClientListFragment;
import co.sisu.mobile.oldFragments.ScoreboardFragment;
import co.sisu.mobile.models.AgentGoalsObject;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncAgentJsonObject;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ScoreboardFragmentTest {

    @Rule
    public ActivityTestRule<ParentActivity> mActivityTestRule = new ActivityTestRule<>(ParentActivity.class, true, false);

    private String agentJson = "{\n" +
            "                   \"agent\": {\n" +
            "                     \"agent_id\": 3667, \n" +
            "                     \"desired_income\": 0.0, \n" +
            "                     \"email\": \"Brady.Groharing@live.com\", \n" +
            "                     \"first_name\": \"Brady\", \n" +
            "                     \"last_name\": \"Groharing\", \n" +
            "                     \"mobile_phone\": \"1112223333\", \n" +
            "                     \"profile\": \"ca6edf8d-2762-4771-8f84-1b3adedcaced.png\", \n" +
            "                     \"vision_statement\": \"My mom said I had to.\"\n" +
            "                   }, \n" +
            "                   \"server_time\": \"2018-10-25 03:07:21\", \n" +
            "                   \"status\": \"OK\", \n" +
            "                   \"status_code\": 0\n" +
            "                 }";

    String contacts = "";
    int appt = 0;
    int closed = 0;
    int contract = 0;
    String signed = "";
    String listings = "";

    Instrumentation.ActivityMonitor clientMonitor = getInstrumentation().addMonitor(ClientListFragment.class.getName(), null, false);

    @Test
    public void signInTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        sleep(3500);
        Gson gson = new Gson();
        AsyncAgentJsonObject agent = gson.fromJson(agentJson, AsyncAgentJsonObject.class);
        AsyncAgentJsonObject agentObject = (AsyncAgentJsonObject) agent;
        AgentModel currentAgent = agentObject.getAgent();

        Intent intent = new Intent();
        intent.putExtra("Agent", currentAgent);
        mActivityTestRule.launchActivity(intent);

        try {
            mActivityTestRule.getActivity()
                    .getSupportFragmentManager().beginTransaction().replace(R.id.your_placeholder, ScoreboardFragment.class.newInstance(), "test").commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //TODO: I need to change this to a more reliable "wait for data to load"
        sleep(8000);

        ParentActivity parentActivity = mActivityTestRule.getActivity();
        DataController dataController = parentActivity.getDataController();
        AgentGoalsObject[] goals = dataController.getAgent().getAgentGoalsObject();
        setupGoals(goals);


        onView(withId(R.id.contactsGoalNumber)).check(matches(withText(contacts)));
        onView(withId(R.id.appointmentsGoalNumber)).check(matches(withText(String.valueOf(appt))));
        onView(withId(R.id.bbsignedGoalNumber)).check(matches(withText(signed)));
        onView(withId(R.id.listingsTakenGoalNumber)).check(matches(withText(listings)));
        onView(withId(R.id.underContactGoalNumber)).check(matches(withText(String.valueOf(contract))));
        onView(withId(R.id.closedGoalNumber)).check(matches(withText(String.valueOf(closed))));

        ViewInteraction contactsButton = onView(
                allOf(withId(R.id.contactsProgress), isDisplayed()));
        contactsButton.perform(click());

        sleep(1000);

        onView(withId(R.id.total)).check(matches(isDisplayed()));
        onView(withId(R.id.total)).check(matches(isDisplayed()));

        pressBack();
        sleep(2000);

        ViewInteraction appointmentsButton = onView(
                allOf(withId(R.id.appointmentsProgress), isDisplayed()));
        appointmentsButton.perform(click());

        sleep(1000);

        onView(withId(R.id.total)).check(matches(isDisplayed()));

        pressBack();
        sleep(2000);

        ViewInteraction buyersButton = onView(
                allOf(withId(R.id.bbSignedProgress), isDisplayed()));
        buyersButton.perform(click());

        sleep(1000);

        onView(withId(R.id.total)).check(matches(isDisplayed()));

        pressBack();
        sleep(2000);

        ViewInteraction listingsButton = onView(
                allOf(withId(R.id.listingsTakenProgress), isDisplayed()));
        listingsButton.perform(click());

        sleep(1000);

        onView(withId(R.id.total)).check(matches(isDisplayed()));

        pressBack();
        sleep(2000);

        ViewInteraction contractButton = onView(
                allOf(withId(R.id.underContractProgress), isDisplayed()));
        contractButton.perform(click());

        sleep(1000);

        onView(withId(R.id.total)).check(matches(isDisplayed()));

        pressBack();
        sleep(2000);

        ViewInteraction closedButton = onView(
                allOf(withId(R.id.closedProgress), isDisplayed()));
        closedButton.perform(click());

        sleep(1000);

        onView(withId(R.id.total)).check(matches(isDisplayed()));

        pressBack();

        sleep(3000);
    }

    private void setupGoals(AgentGoalsObject[] goals) {
        for (AgentGoalsObject ago : goals) {
            if (ago.getGoal_id().equals("SCLSD") || ago.getGoal_id().equals("BCLSD")) {
                closed += Integer.parseInt(ago.getValue());
            }
            else if (ago.getGoal_id().equals("SUNDC") || ago.getGoal_id().equals("BUNDC")) {
                contract += Integer.parseInt(ago.getValue());
            }
            else if (ago.getGoal_id().equals("SAPPT") || ago.getGoal_id().equals("BAPPT")) {
                appt += Integer.parseInt(ago.getValue());
            }
            else if(ago.getGoal_id().equals("SSGND")) {
                listings = ago.getValue().equals("0") ? "1" : ago.getValue();
            }
            else if(ago.getGoal_id().equals("BSGND")) {
                signed = ago.getValue().equals("0") ? "1" : ago.getValue();
            }
            else if(ago.getGoal_id().equals("CONTA")){
                contacts = ago.getValue().equals("0") ? "1" : ago.getValue();
            }
        }
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
