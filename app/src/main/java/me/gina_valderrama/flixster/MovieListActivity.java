package me.gina_valderrama.flixster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import me.gina_valderrama.flixster.models.Config;
import me.gina_valderrama.flixster.models.Movie;

public class MovieListActivity extends AppCompatActivity {

    //constants

    //the base URL for the API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    //the parameter name for the API key
    public final static String API_KEY_PARAM = "api_key";
    //tag for logging from this activity
    public final static String TAG = "MovieListActivity";

    //instance fields

    AsyncHttpClient client;

    //list of currently playing movies
    ArrayList<Movie> movies;
    //the recycler view
    RecyclerView rvMovies;
    //the adapter wired to the recycler view
    MovieAdapter adapter;
    // image config
    Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        //initialize client
        client = new AsyncHttpClient();

        //initialize list of movies
        movies = new ArrayList<>();

        //initialize the adapter - movies array can't be reinitialized after this point
        adapter = new MovieAdapter(movies);

        //resolve the recycler view and connect a layout manager and the adapter
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        //get configuration on app creation
        getConfiguration();

    }


    //get the list of currently playing movies from the API
    private void getNowPlaying() {
        //create URL
        String url = API_BASE_URL + "/movie/now_playing";
        //set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); //api key, always required
        //execute a GET command, expect a JSON object response

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //load the results into movies list
                try {
                    JSONArray results = response.getJSONArray("results");
                    //iterate thru results and create movie objects
                    for (int i = 0; i < results.length(); i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        //notify adapter that a row was added
                        adapter.notifyItemInserted(movies.size() - 1);
                    }
                    Log.i(TAG, String.format("loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("failed to parse now playing", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("failed to get data from now playing endpoint", throwable, true);
            }
        });
    }

    //get configuration from the API
    private void getConfiguration() {
        //create URL
        String url = API_BASE_URL + "/configuration";
        //set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); //api key, always required
        //execute a GET command, expect a JSON object response

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    config = new Config(response);
                    Log.i(TAG, String.format("loaded configuration with imageBaseUrl %s, and posterSize",
                            config.getImageBaseURL(),
                            config.getPosterSize()));
                    //pass to adapter
                    adapter.setConfig(config);

                    //get now playing movie list
                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed parsing configuration", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting configuration", throwable, true);
            }
        });
    }

    //handle errors, log and alert
    private void logError(String message, Throwable error, boolean alertUser) {
        //always log error
        Log.e(TAG, message, error);
        //alert user to avoid silent errors
        if (alertUser) {
            //show long toast with error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        }
    };

}
