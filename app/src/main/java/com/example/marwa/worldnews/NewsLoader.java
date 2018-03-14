package com.example.marwa.worldnews;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

/**
 * Created by Marwa on 1/21/2018.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    /**
     * Query URL
     */
    private String url;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public NewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    /**
     * Force the data to load.
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<News> loadInBackground() {
        // If there is no URL, return null.
        if (url == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of news stories.
        return QueryUtils.fetchNewsData(url);
    }
}
