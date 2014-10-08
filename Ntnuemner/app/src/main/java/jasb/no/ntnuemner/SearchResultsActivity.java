package jasb.no.ntnuemner;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import jasb.no.ntnuemner.data.CourseDbHelper;

/**
 * Created by alex on 8/26/14.
 */
public class SearchResultsActivity extends Activity {

    private final String LOG_TAG = SearchResultsActivity.class.getSimpleName();
    private ArrayAdapter<String> mCourseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "Created!");
        String[] searchResults = handleIntent(getIntent());
        mCourseAdapter = new ArrayAdapter<String>(
                this,
                R.layout.list_item_course,
                R.id.list_item_course_textview,
                searchResults
        );
        setContentView(R.layout.fragment_course);
        ListView listView = (ListView) findViewById(R.id.listview_course);
        listView.setAdapter(mCourseAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.v(LOG_TAG, "inside onItemClick");
                Toast.makeText(getApplicationContext(), "haha", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(getIntent());
    }

    private String[] handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.v(LOG_TAG, "Search query is: " + query);
            CourseDbHelper dbHelper = new CourseDbHelper(this);
            View rootView = getLayoutInflater().inflate(R.layout.fragment_course, null);
            String[] searchResult = new SearchTask(this).search(query);
            return searchResult;
        }
        return null;
    }



}
