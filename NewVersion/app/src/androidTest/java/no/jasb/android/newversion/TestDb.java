package no.jasb.android.newversion;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import no.jasb.android.newversion.data.CourseDbHelper;

/**
 * Created by alex on 10/4/14.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(CourseDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new CourseDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        CourseDbHelper dbHelper = new CourseDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues courseValues = createCourseValues();
        long courseRowId;
        courseRowId = db.insert(CourseDbHelper.TABLE_NAME, null, courseValues);

        assertTrue(courseRowId != -1);
        Log.d(LOG_TAG, "New course row id: " + courseRowId);

        Cursor courseCursor = db.query(
                CourseDbHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        validateCursor(courseCursor, courseValues);

        dbHelper.close();
    }

    static ContentValues createCourseValues() {
        ContentValues courseValues = new ContentValues();
        courseValues.put(CourseDbHelper.COURSE_CODE, "TDT4100");
        courseValues.put(CourseDbHelper.COURSE_NORWEGIAN_NAME, "Objektorientert programmering");
        courseValues.put(CourseDbHelper.COURSE_ENGLISH_NAME, "Object oriented programming");

        return courseValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }
}
