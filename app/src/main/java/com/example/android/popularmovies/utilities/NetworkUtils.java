package com.example.android.popularmovies.utilities;


import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String API_KEY = "28402f763ba69ea531e50e04bcead1b8";
    private static final String MOVIEDB_BASE_URL = "https://api.themoviedb.org";
    private static final String PATH_ID = "3";
    private static final String PATH_MOVIE = "movie";
    private static final String PATH_VIDEOS = "videos";
    private static final String PATH_REVIEWS = "reviews";
    public static final String PATH_POPULAR = "popular";
    public static final String PATH_RATING = "top_rated";
    private final static String PARAM_KEY = "api_key";

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185/";

    public static URL buildUrl(String path) {

        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .path(PATH_ID)
                .appendPath(PATH_MOVIE)
                .appendPath(path)
                .appendQueryParameter(PARAM_KEY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrlVideos(int id) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .path(PATH_ID)
                .appendPath(PATH_MOVIE)
                .appendPath(String.valueOf(id))
                .appendPath(PATH_VIDEOS)
                .appendQueryParameter(PARAM_KEY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrlReviews(int id) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .path(PATH_ID)
                .appendPath(PATH_MOVIE)
                .appendPath(String.valueOf(id))
                .appendPath(PATH_REVIEWS)
                .appendQueryParameter(PARAM_KEY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String buildPosterUrl(MovieData movie) {
        return IMAGE_BASE_URL + IMAGE_SIZE + movie.poster_path;
    }
}
