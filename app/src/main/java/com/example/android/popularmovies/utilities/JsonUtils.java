package com.example.android.popularmovies.utilities;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.HttpURLConnection;

public final class JsonUtils {

    public static MovieData[] getMovieDataFromJson(Context context, String rawJson) throws JSONException{
        JSONObject movieJson = new JSONObject(rawJson);

        // Check for errors
        final String OWM_MESSAGE_CODE = "cod";
        if (movieJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        // Extract the JSON into an array
        JSONArray movieArray = movieJson.getJSONArray("results");
        MovieData[] movieData = new MovieData[movieArray.length()];
        // Parse each object and create a new MovieData entry keeping the relevant fields
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movie = movieArray.getJSONObject(i);
            movieData[i] = new MovieData(
                    movie.getString("poster_path"),
                    movie.getString("overview"),
                    movie.getString("release_date"),
                    movie.getString("original_title"),
                    BigDecimal.valueOf(movie.getDouble("vote_average")).floatValue());
        }

        return movieData;
    }

}
