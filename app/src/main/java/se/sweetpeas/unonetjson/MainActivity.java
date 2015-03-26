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

    private final String URI = "http://192.168.0.11";
    private final String TAG = "REST";

    RestAdapter restAdapter = null;
    uHTTP uHttp = null;

    private ArrayList<String> listItems=new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    private ListView list;

    static class IoPin {
        int id;
        int pin;
        int value;
        int pwm;
    }

    interface uHTTP {
        @GET("/digital")
        List<IoPin> getAllIoPins();

        @GET("/digital/{id}")
        List<IoPin> getIoPin(
                @Path("id") String id
        );

        @GET("/analog")
        List<IoPin> getAllAnalogPins();

        @GET("/analog/{id}")
        List<IoPin> getAnalogPin(
                @Path("id") String id
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

        uHttp = restAdapter.create(uHTTP.class);

        new getAllIoPins().execute();
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

    //
    // Get the state of all IO pins
    //
    private class getAllIoPins extends AsyncTask<Void, Void, Integer> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected Integer doInBackground(Void... a) {
            List<IoPin> ioPins = uHttp.getAllIoPins();

            for (IoPin ioPin : ioPins) {
                Log.d(TAG, ioPin.id + " (" + ioPin.pin + ") = " + ioPin.value);
                listItems.add(ioPin.id + " (" + ioPin.pin + ") = " + ioPin.value + ", pwm = " + ioPin.pwm);
            }
            return 0;
        }

        protected void onPostExecute(Integer inp) {
            adapter.notifyDataSetChanged();
        }
    }
}
