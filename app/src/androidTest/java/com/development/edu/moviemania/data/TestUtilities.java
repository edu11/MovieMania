package com.development.edu.moviemania.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.development.edu.moviemania.data.MoviesContract.MovieEntry;
import com.development.edu.moviemania.data.MoviesContract.ReviewEntry;
import com.development.edu.moviemania.data.MoviesContract.TrailerEntry;
import com.development.edu.moviemania.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by edu on 31/08/2015.
 */
public class TestUtilities extends AndroidTestCase {

    static final String TEST_LOCATION = "99705";
    static final long TEST_DATE = 1419033600L;  // December 20th, 2014

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: Use this to create some default weather values for your database tests.
     */
    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_MOVIE_ID, 123456);
        movieValues.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, "Overview of the movie");
        movieValues.put(MovieEntry.COLUMN_MOVIE_POSTER, "Url to poster");
        movieValues.put(MovieEntry.COLUMN_MOVIE_RATING, 7.5);
        movieValues.put(MovieEntry.COLUMN_MOVIE_RELEASE, "2015");
        movieValues.put(MovieEntry.COLUMN_MOVIE_TITLE, "Awesome title");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_RUNTIME, 120);


        return movieValues;
    }

    static ContentValues createReviewValues(long movieRowId) {
        ContentValues reviewValues = new ContentValues();
        reviewValues.put(ReviewEntry.COLUMN_MOVIE_KEY, movieRowId);
        reviewValues.put(ReviewEntry.COLUMN_REVIEW_AUTHOR, "Chris");
        reviewValues.put(ReviewEntry.COLUMN_REVIEW_CONTENT, "blablabla blabla blabla");

        return reviewValues;
    }

    static ContentValues createTrailerValues(long movieRowId) {
        ContentValues trailerValues = new ContentValues();
        trailerValues.put(TrailerEntry.COLUMN_MOVIE_KEY, movieRowId);
        trailerValues.put(TrailerEntry.COLUMN_TRAILER_NAME, "Trailer 1");
        trailerValues.put(TrailerEntry.COLUMN_TRAILER_URL, "www.youtube.com/watch?v=asdasd");

        return trailerValues;
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }


}