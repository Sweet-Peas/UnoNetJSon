package se.sweetpeas.unonetjson;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;

public class MainActivity extends ActionBarActivity {

    private final String URI = "https://api.github.com";
    private final String TAG = "REST";

    RestAdapter restAdapter = null;
    GitHub github = null;

    private ArrayList<String> listItems=new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    private ListView list;

    static class Contributor {
        String login;
        int contributions;
    }

    interface GitHub {
        @GET("/repos/{owner}/{repo}/contributors")
        List<Contributor> getContributors(
                @Path("owner") String owner,
                @Path("repo") String repo
        );

        @PUT("/repos/{owner}/{repo}/contributors")
        List<Contributor> hansa(
                @Path("owner") String owner,
                @Path("repo") String repo
        );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        list = (ListView) findViewById(R.id.listView);
        list.setAdapter(adapter);

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(URI)
                .build();

        github = restAdapter.create(GitHub.class);

        new getGitUsers().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class getGitUsers extends AsyncTask<Void, Void, Integer> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected Integer doInBackground(Void... a) {
            List<Contributor> contributors = github.getContributors("square", "retrofit");

            for (Contributor contributor : contributors) {
                Log.d(TAG, contributor.login + " (" + contributor.contributions + ")");
                listItems.add(contributor.login + " (" + contributor.contributions + ")");
            }
            return 0;
        }

        protected void onPostExecute(Integer inp) {
            adapter.notifyDataSetChanged();
        }
    }
}
