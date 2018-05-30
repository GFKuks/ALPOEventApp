package com.alpoeventapp.qualityapp.views;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.SearchView;

import com.alpoeventapp.qualityapp.R;

/**
 * Klase, kas atbilst meklēšanas rīkam.
 */
public class SearchActivity extends AppCompatActivity{

    private static final String TAG = "SearchActivity";
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: starts");
        getMenuInflater().inflate(R.menu.menu_search, menu); //inflates menu_search, menu_browse is parameter passed from android to this method

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE); //provides access to system search service
        mSearchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();  //get reference to searchView widget
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());    //this gets searchable info by passing component name
        mSearchView.setSearchableInfo(searchableInfo);  //searchableInfo is then set into SearchView

        mSearchView.setIconified(false);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: called");
                Intent intent = new Intent(SearchActivity.this, BrowseEventsListActivity.class);
                intent.putExtra("query", query);

                mSearchView.clearFocus();   //ensures we return to MainActivity if using external keyboard

                startActivity(intent);
                finish();
                return true; //change to true to indicate that we're dealing with the event
            }

            @Override
            public boolean onQueryTextChange(String newText) {  //called back any time text is changed, one char at a time
                return false;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();   //on closing search activity, this activity closes, returning to MainActivity
                return false;
            }
        });
        //all these steps don't search
        Log.d(TAG, "onCreateOptionsMenu: returned " + true);//they just get the search items from the user
        return true;    //using a MenuInflater instead of LayoutInflater
    }
}
