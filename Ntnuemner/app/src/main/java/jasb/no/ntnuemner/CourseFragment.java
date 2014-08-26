package jasb.no.ntnuemner;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 8/18/14.
 */
public class CourseFragment extends Fragment {


    private final String LOG_TAG = CourseFragment.class.getSimpleName();
    private ArrayAdapter<String> mCourseAdapter;

    private TextView courseCode;
    private TextView courseNorwegianName;
    private TextView courseEnglishName;
    private TextView courseCredits;
    private TextView courseAnbFork;

    public CourseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        String[] data = {};
        List<String> course = new ArrayList<String>(Arrays.asList(data));
        mCourseAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_course,
                R.id.list_item_course_textview,
                course
        );
        View rootView = inflater.inflate(R.layout.fragment_course, container, false);
/*        courseCode = (TextView) rootView.findViewById(R.id.course_code);
        courseNorwegianName = (TextView) rootView.findViewById(R.id.course_norwegian_name);
        courseEnglishName = (TextView) rootView.findViewById(R.id.course_english_name);
        courseCredits = (TextView) rootView.findViewById(R.id.course_credits);
        courseAnbFork = (TextView) rootView.findViewById(R.id.course_anbfork);*/

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_course);
        listView.setAdapter(mCourseAdapter);

        new FetchCourseDataTask().execute();
        return rootView;
    }

    public class FetchCourseDataTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... strings) {
            String courseStr = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                final String COURSE_BASE_URL = "http://www.ime.ntnu.no/api/course";

                Uri.Builder builtUri = Uri.parse(COURSE_BASE_URL).buildUpon().appendEncodedPath("en/tdt4100");
                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Uri is: " + url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                courseStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in
                // attempting to parse it
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG,
                                "Error closing stream", e);
                    }
                }
            }
            try {
                return getCourseDataFromJson(courseStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        private String[] getCourseDataFromJson(String courseStr) throws JSONException {
            final String COURSE = "course";
            final String COURSE_CODE = "code";
            final String COURSE_NORWEGIAN_NAME = "norwegianName";
            final String COURSE_ENGLISH_NAME = "englishName";
            final String COURSE_CREDIT = "credit";

            final String INFO = "infoType";
            final String INFO_NAME = "name";
            final String INFO_TEXT = "text";
            final int INFO_ANBFORK = 0;
            final int INFO_FORKRAV = 1;
            final int INFO_CONTENT = 2;
            final int INFO_METHOD = 4;
            final int INFO_GOAL = 5;

            final String ASSESSMENT = "assessment";

            final String COURSE_PEOPLE = "educationalRole";
            final String COURSE_CREDIT_REDUCTION = "creditReduction";

            String code;
            String norwegianName;
            String englishName;
            double credit;
            String anbFork;
            String forKrav = "none";
            String content;
            String method;
            String goal;

            JSONObject courseJson = new JSONObject(courseStr).getJSONObject(COURSE);
            code = courseJson.getString(COURSE_CODE);
            norwegianName = courseJson.getString(COURSE_NORWEGIAN_NAME);
            englishName = courseJson.getString(COURSE_ENGLISH_NAME);
            credit = courseJson.getDouble(COURSE_CREDIT);

            JSONArray infoJson = courseJson.getJSONArray(INFO);
            anbFork = infoJson.getJSONObject(INFO_ANBFORK).getString(INFO_TEXT);
            if (!infoJson.getJSONObject(INFO_FORKRAV).isNull(INFO_TEXT)) {
                forKrav = infoJson.getJSONObject(INFO_FORKRAV).getString(INFO_TEXT);
            }
            content = infoJson.getJSONObject(INFO_CONTENT).getString(INFO_TEXT);
            method = infoJson.getJSONObject(INFO_METHOD).getString(INFO_TEXT);
            goal = infoJson.getJSONObject(INFO_GOAL).getString(INFO_TEXT);

            String result = "course code: " + code + "\nNames: " + norwegianName + " / "
                    + englishName + "\nCredit: " + credit + "Anbefalte forkunnskaper: " + anbFork + "\nForkunnskapskrav: "
                    + forKrav + "\nFaglig innhold: " + content + "\nLæringsform: " + method +
                    "\nLæringsmål: " + goal;
            Log.v(LOG_TAG, result);

            String[] r = new String[] {
                    "course code: " + code,
                    "Norwegian name: " + norwegianName,
                    "English name: " + englishName,
                    "Credit: " + credit,
                    "Anbefalte forkunnskaper: " + anbFork,
                    "Forkunnskapskrav: " + forKrav,
                    "Faglig innhold: " + content,
                    "Læringsform: " + method,
                    "Læringsmål: " + goal
            };

            return r;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mCourseAdapter.clear();
                for(String dayForecastStr : result) {
                    mCourseAdapter.add(dayForecastStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}
