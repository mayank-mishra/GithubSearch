package com.git.search.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.git.search.Classes.FilterModel;
import com.git.search.MyApplication;
import com.git.search.Objects.RepoObject;
import com.git.search.Objects.ResponseObject;
import com.git.search.R;
import com.git.search.adapters.RepoAdapter;
import com.git.search.fragments.FilterFabFragment;
import com.git.search.network.GsonGetRequest;
import com.git.search.network.Urls;
import com.git.search.util.AppUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by mayank on 18/01/2018.
 */

public class MainActivity extends AppCompatActivity implements RepoAdapter.RepoAdapterListener, AAH_FabulousFragment.Callbacks {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<RepoObject> repoList;
    private RepoAdapter mAdapter;
    private SearchView searchView;
    private FrameLayout layout_progress;
    private LinearLayoutManager layoutManager;
    private boolean isLoading=false;
    private int pageCount=1;
    private String searchName="android";
    private FloatingActionButton fab;
    private FilterFabFragment dialogFrag;
    Map<String, String> defaultHeader = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        layout_progress=findViewById(R.id.layout_progress);

            fab = (FloatingActionButton) findViewById(R.id.fab);

            dialogFrag = FilterFabFragment.newInstance();
            dialogFrag.setParentFab(fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
                }
            });

        setSupportActionBar(toolbar);
        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);

        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        defaultHeader.put("user-agent", System.getProperty("http.agent"));
        repoList = new ArrayList<>();
        mAdapter = new RepoAdapter(this, repoList, this);

        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 60));
        recyclerView.setAdapter(mAdapter);
        //recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        searchGitRepositories(Urls.DEFAULT_QUERY_STRING);
        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            AppUtils.showError(this, exceptionAsString);
        }
    }

    private void visibleProgress() {
        layout_progress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        layout_progress.setVisibility(View.GONE);
    }

    private void searchGitRepositories(String queryString){
        try{
        visibleProgress();
        GsonGetRequest request = new GsonGetRequest(Urls.URL_REPO+queryString,ResponseObject.class,defaultHeader,
                new Response.Listener<Object >() {
                    @Override
                    public void onResponse(Object  response) {
                        hideProgress();
                       // AppUtils.showError(MainActivity.this,((ResponseObject)response).items.toString());
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the data! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        // adding contacts to contacts list
                        repoList.clear();
                        repoList.addAll(((ResponseObject)response).items);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                hideProgress();
                String errorMsg="";
                if(!AppUtils.NullChecker(error.getMessage()).isEmpty()){
                    errorMsg="Something Went Wrong,Please try again later";
                }else{
                    errorMsg=AppUtils.NullChecker(error.getMessage());
                }
                AppUtils.showError(MainActivity.this,errorMsg);
            }


        });

        // Add gson request to volley request queue.
        MyApplication.getInstance().addToRequestQueue(request);
        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            AppUtils.showError(this, exceptionAsString);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        final String SEARCH_QUERY_STRING="&sort=stars&order=desc&page=1&per_page=10";
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                //mAdapter.getFilter().filter(query);
                searchName=query;
                if(!query.isEmpty())
                    searchGitRepositories("q="+query+SEARCH_QUERY_STRING);
                else
                    searchGitRepositories(Urls.DEFAULT_QUERY_STRING);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                searchName=query;
                if(!query.isEmpty())
                searchGitRepositories("q="+query+SEARCH_QUERY_STRING);
                else
                    searchGitRepositories(Urls.DEFAULT_QUERY_STRING);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }else if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onrepoSelected(RepoObject contact) {
        Toast.makeText(getApplicationContext(), "Selected: " + contact.name + ", " + contact.full_name, Toast.LENGTH_LONG).show();
        Intent i=new Intent(MainActivity.this,RepoDetailActivity.class);
        i.putExtra("Repo",contact);
        startActivity(i);
    }


    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            pageCount++;
            if (!isLoading) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        /*&& totalItemCount >= PAGE_SIZE*/) {

                    String query="q="+searchName+"&page="+pageCount+"&per_page=10";
                    searchGitRepositories(query);
                }
            }
        }
    };

    @Override
    public void onResult(Object result) {
        try{
            StringBuilder sb = new StringBuilder(100);
        if(searchName.isEmpty())
            searchName="android";
        String commanQuery="&page=1&per_page=10";

            sb.append("q="+searchName);
        FilterModel filter= ((FilterModel)result);
        //because of some url issue i am removing that
            if(!filter.fromDate.isEmpty() && !filter.toDate.isEmpty()) {
                sb.append(" created:" + filter.fromDate + ".." + filter.toDate);
            }if(!filter.is.isEmpty()) {
                sb.append("   is:" + filter.is);
            }if(!filter.sortBy.isEmpty()) {
                sb.append("&sort=" + filter.sortBy);
            }if(!filter.orderBy.isEmpty()) {
                sb.append("&order=" + filter.orderBy);
            }
            sb.append(commanQuery);
        searchGitRepositories(sb.toString());
        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            AppUtils.showError(this, exceptionAsString);
        }
        //
    }
}
