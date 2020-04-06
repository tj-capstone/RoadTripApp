package com.example.roadtripapp;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
//import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;

import org.junit.Rule;

import org.junit.runner.RunWith;



@RunWith(AndroidJUnit4.class)
public class UITest {
    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.READ_CONTACTS);

    @Test
    public void TestContact() {

        Intent resultData = new Intent();
        resultData.setData(getContactUriByName("Brianna"));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        intending(toPackage("com.google.android.contacts")).respondWith(result);
        onView(withText("Enter your Name")).check(matches(isDisplayed()));
        //onView(withText("Enter your Name")).perform(typeText("Dummy Name"), closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.btnContact)).perform(click());


        onView(withId(R.id.tvNumber)).check(matches(not(withText("No Contact Selected"))));

    }

    public Uri getContactUriByName(String contactName) {
        Cursor cursor = mActivityRule.getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                if (name.equals(contactName)) {
                    return Uri.withAppendedPath(ContactsContract.Data.CONTENT_URI, id);
                }
            }
        }
        return null;
    }


    @Test
    public void TestMap() {


        onView(withText("Enter your Name")).check(matches(isDisplayed()));
        //onView(withText("Enter your Name")).perform(typeText("Dummy Name"), closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.btnLocation)).perform(click());
        //onView(withContentDescription("Google Map")).perform(click());

        onView(withId(R.id.pickLocation)).perform(click());
        onView(withId(R.id.tvLocation)).check(matches(not(withText("No Location Selected"))));
    }


    @Test
    public void TestGetLocation() {
        //Test to make sure it doesnt work with nothing entered
        onView(withText("Enter your Name")).check(matches(isDisplayed()));
        //onView(withText("Enter your Name")).perform(typeText("Dummy Name"), closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.btnSend)).perform(click());
        //onView(withContentDescription("Google Map")).perform(click());
        onView(withId(R.id.tvLocation)).check(matches((withText("NO location set"))));


        onView(withId(R.id.btnLocation)).perform(click());
        onView(withId(R.id.pickLocation)).perform(click());
        Intent resultData = new Intent();
        resultData.setData(getContactUriByName("Brianna"));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(toPackage("com.google.android.contacts")).respondWith(result);
        onView(withId(R.id.btnContact)).perform(click());
        onView(withId(R.id.btnSend)).perform(click());
        onView(withId(R.id.tvLatCurr)).check(matches(not(withText("Latitude"))));




    }
}


