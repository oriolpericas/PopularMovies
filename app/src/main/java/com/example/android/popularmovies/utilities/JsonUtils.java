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
        if (!checkForErrors(movieJson))
            return null;

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
                    movie.getInt("id"),
                    movie.getString("original_title"),
                    BigDecimal.valueOf(movie.getDouble("vote_average")).floatValue());
        }

        return movieData;
    }

    public static ReviewData[] getReviewDataFromJson(Context context, String rawJson) throws JSONException {
        JSONObject reviewJson = new JSONObject(rawJson);

        // Check for errors
        if (!checkForErrors(reviewJson))
            return null;

        // Extract the JSON into an array
        JSONArray reviewArray = reviewJson.getJSONArray("results");
        ReviewData[] reviewData = new ReviewData[reviewArray.length()];
        // Parse each object and create a new ReviewData entry
        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject review = reviewArray.getJSONObject(i);
            reviewData[i] = new ReviewData(
                    review.getString("id"),
                    review.getString("author"),
                    review.getString("content"),
                    review.getString("url"));
        }

        return reviewData;
    }

    public static TrailerData[] getTrailerDataFromJson(Context context, String rawJson) throws JSONException {
        JSONObject trailerJson = new JSONObject(rawJson);

        // Check for errors
        if (!checkForErrors(trailerJson))
            return null;

        // Extract the JSON into an array
        JSONArray trailerArray = trailerJson.getJSONArray("results");
        TrailerData[] trailerData = new TrailerData[trailerArray.length()];
        // Parse each object and create a new TrailerData entry
        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject trailer = trailerArray.getJSONObject(i);
            trailerData[i] = new TrailerData(
                    trailer.getString("id"),
                    trailer.getString("iso_639_1"),
                    trailer.getString("iso_3166_1"),
                    trailer.getString("key"),
                    trailer.getString("name"),
                    trailer.getString("site"),
                    trailer.getInt("size"),
                    trailer.getString("type"));
        }

        return trailerData;
    }

    private static boolean checkForErrors(JSONObject json) throws JSONException {
        final String OWM_MESSAGE_CODE = "cod";
        if (json.has(OWM_MESSAGE_CODE)) {
            int errorCode = json.getInt(OWM_MESSAGE_CODE);
            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    return true;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return false;
                default:
                    return false;
            }
        }
        return true;
    }
}
