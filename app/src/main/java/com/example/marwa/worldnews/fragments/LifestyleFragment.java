package com.example.marwa.worldnews.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.marwa.worldnews.News;
import com.example.marwa.worldnews.NewsLoader;
import com.example.marwa.worldnews.R;
import com.example.marwa.worldnews.adapters.NewsAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.example.marwa.worldnews.fragments.link.NEWS_LOADER_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class LifestyleFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>> {


    /**
     * Adapter for the list of news stories.
     */
    NewsAdapter adapter;
    NetworkInfo networkInfo;
    /**
     * TextView that is displayed when the list is empty.
     */
    private TextView emptyStateTextView;
    /**
     * a ProgressBar variable to show and hide the progress bar.
     */
    private ProgressBar loadingIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.news_list, container, false);

        // Find a reference to the {@link ListView} in the layout.
        ListView newsListView = (ListView) rootView.findViewById(R.id.newsList);

        // Find a reference to an empty TextView
        emptyStateTextView = (TextView) rootView.findViewById(R.id.empty_view);
        // Set the TextView on the ListView.
        newsListView.setEmptyView(emptyStateTextView);

        //Find the ProgressBar using findViewById.
        loadingIndicator = (ProgressBar) rootView.findViewById(R.id.loading_indicator);

        // Create a new adapter that takes an empty list of news stories as input.
        adapter = new NewsAdapter(getActivity(), new ArrayList<News>());

        // Set the adapter on the {@link ListView}.
        newsListView.setAdapter(adapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news story.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current News's story that was clicked on.
                News currentNewsStory = adapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor).
                Uri newsUri = null;
                if (currentNewsStory != null) {
                    newsUri = Uri.parse(currentNewsStory.getWebUrl());
                }

                // Create a new intent to view the News's story URI.
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity.
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter.
            loaderManager.initLoader(link.NEWS_LOADER_ID, null, this);
        } else {
            // Update empty state with no connection error message
            emptyStateTextView.setText(R.string.no_internet_connection);
        }

        return rootView;
    }

    // onCreateLoader instantiates and returns a new Loader for the given ID
    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        emptyStateTextView.setVisibility(View.INVISIBLE);
        // First, hide loading indicator.
        loadingIndicator.setVisibility(View.VISIBLE);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        // getString retrieves a String value from the preferences.
        // The second parameter is the default value for this preference.
        String country = sharedPrefs.getString(
                getString(R.string.country_key),
                getString(R.string.country_default));

        String date = sharedPrefs.getString(
                getString(R.string.date_key),
                getString(R.string.date_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(link.NEWS_REQUEST_URL);
        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value.
        uriBuilder.appendQueryParameter(link.PARAM_QUERY, country);
        uriBuilder.appendQueryParameter(link.PARAM_SECTION, link.LIFE_AND_STYLE);
        uriBuilder.appendQueryParameter(link.PARAM_SHOW_TAGS, link.AUTHOR);
        uriBuilder.appendQueryParameter(link.PARAM_SHOW_FIELDS, link.PIC_DIS);
        uriBuilder.appendQueryParameter(link.PARAM_PAGE_SIZE, link.SIZE);
        uriBuilder.appendQueryParameter(link.PARAM_ORDER_BY, date);
        uriBuilder.appendQueryParameter(link.PARAM_API_KEY, link.KEY);

        // Return the completed uri
        return new NewsLoader(getContext(), uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        //Hide the indicator after the data is appeared
        loadingIndicator.setVisibility(View.GONE);

        // Check if connection is still available, otherwise show appropriate message
        if (networkInfo != null && networkInfo.isConnected()) {
            // If there is a valid list of news stories, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                adapter.addAll(data);
            } else {
                emptyStateTextView.setVisibility(View.VISIBLE);
                emptyStateTextView.setText(getString(R.string.no_news));
            }

        } else {
            emptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        adapter.clear();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Restart the loader when we get back to the MainActivity
        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
    }

}
