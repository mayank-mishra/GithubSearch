package com.git.search.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.git.search.MyApplication;
import com.git.search.Objects.ContributerResponseObject;
import com.git.search.Objects.RepoObject;
import com.git.search.Objects.ResponseObject;
import com.git.search.R;
import com.git.search.adapters.ContributerGridAdapter;
import com.git.search.network.GsonGetRequest;
import com.git.search.network.Urls;
import com.git.search.util.AppUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RepoDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, ContributerGridAdapter.OnGridItemClickListener{

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;
    final Uri imageUri = Uri.parse("http://i.imgur.com/VIlcLfg.jpg");

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private AppBarLayout appbar;
    private CollapsingToolbarLayout collapsing;
    private ImageView coverImage;
    private FrameLayout framelayoutTitle;
    private LinearLayout linearlayoutTitle;
    private Toolbar toolbar;
    private TextView textviewTitle,txt_full_name,txt_name,txt_link,txt_desc;
    private CircleImageView avatar;

    private GridView grid_view;
    private ContributerGridAdapter mAdapter ;
    private List<ContributerResponseObject> repoList;
    private FrameLayout layout_progress;
    Map<String, String> defaultHeader = new HashMap<String, String>();
    RepoObject repoDetail;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-03-03 11:32:38 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        repoDetail=(RepoObject)getIntent().getSerializableExtra("Repo");
        appbar = (AppBarLayout)findViewById( R.id.appbar );
        collapsing = (CollapsingToolbarLayout)findViewById( R.id.collapsing );
        coverImage = (ImageView)findViewById( R.id.imageview_placeholder );
        framelayoutTitle = (FrameLayout)findViewById( R.id.framelayout_title );
        linearlayoutTitle = (LinearLayout)findViewById( R.id.linearlayout_title );
        toolbar = (Toolbar)findViewById( R.id.toolbar );
        textviewTitle = (TextView)findViewById( R.id.textview_title );
        txt_name = (TextView)findViewById( R.id.txt_repo_name );
        txt_full_name = (TextView)findViewById( R.id.txt_repo_full_name );
        avatar = (CircleImageView) findViewById(R.id.avtar_logo);

        grid_view = (GridView) findViewById(R.id.grid_view);
        layout_progress=(FrameLayout) findViewById(R.id.layout_progress);
        txt_link = (TextView) findViewById(R.id.txt_link);
        txt_desc= (TextView) findViewById(R.id.txt_desc);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Fresco.initialize(this);
            setContentView(R.layout.activity_repo_detail);
            findViews();

            toolbar.setTitle("");
            appbar.addOnOffsetChangedListener(this);

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            // toolbar fancy stuff
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            startAlphaAnimation(textviewTitle, 0, View.INVISIBLE);
            whiteNotificationBar(grid_view);
            defaultHeader.put("user-agent", System.getProperty("http.agent"));
            repoList = new ArrayList<>();
            mAdapter=new ContributerGridAdapter(this,repoList,this);
            grid_view.setAdapter(mAdapter);
            textviewTitle.setText(repoDetail.name);
            txt_full_name.setText(repoDetail.full_name);
            txt_name.setText(repoDetail.name);
            txt_link.setText(repoDetail.url);
            txt_desc.setText(repoDetail.description);

            Glide.with(RepoDetailActivity.this)
                    .load("https://opensource.com/sites/default/files/styles/image-full-size/public/lead-images/github-universe.jpg?itok=lwRZddXA")
                    .apply(RequestOptions.circleCropTransform())
                    .into(coverImage);

            Glide.with(RepoDetailActivity.this)
                    .load(repoDetail.owner.avatar_url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(avatar);

            fetchRequest();

        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            AppUtils.showError(this, exceptionAsString);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    private void fetchRequest() {
        try{
        visibleProgress();
        StringRequest request = new StringRequest(repoDetail.contributors_url ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideProgress();
                        //AppUtils.showError(RepoDetailActivity.this, response);
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Type listType = new TypeToken<List<ContributerResponseObject>>() {}.getType();
                        List<ContributerResponseObject> contributerResponseObject = new Gson().fromJson(response.toString(),listType);
                        repoList.clear();
                        repoList.addAll(contributerResponseObject);

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();
                        AppUtils. setGridViewHeightBasedOnChildren(grid_view,4);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                hideProgress();
                AppUtils.showError(RepoDetailActivity.this, error.getMessage());
            }


        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return defaultHeader;
            }


        };
        MyApplication.getInstance().addToRequestQueue(request);
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

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.BLACK);
        }
    }

    @Override
    public void onGridItemClick(ContributerResponseObject contributerResponseObject) {
        try {
            Toast.makeText(getApplicationContext(), "Selected: " + contributerResponseObject.login + ", " + contributerResponseObject.repos_url, Toast.LENGTH_LONG).show();
            Intent i = new Intent(RepoDetailActivity.this, ContributerDetailActivity.class);
            i.putExtra("KEY", contributerResponseObject);
            startActivity(i);
        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            AppUtils.showError(this, exceptionAsString);
        }
    }
}

