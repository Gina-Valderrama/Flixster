package me.gina_valderrama.flixster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {
    //base url for images
    String imageBaseURL;
    //the poster size to get when fetching images, part of url
    String posterSize;

    public Config(JSONObject object) throws JSONException {
        JSONObject images = object.getJSONObject("images");

        //get the image base url
        imageBaseURL = images.getString("secure_base_url");
        //get poster size
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
        //use the option at index 3 or fallback to w342
        posterSize = posterSizeOptions.optString(3, "w342");

    }

    //helper method for creating urls
    public String getImageURL(String size, String path){
        return String.format("%s%s%s", imageBaseURL, size, path); //concatenation to form url
    }

    public String getImageBaseURL() {
        return imageBaseURL;
    }

    public String getPosterSize() {
        return posterSize;
    }
}
