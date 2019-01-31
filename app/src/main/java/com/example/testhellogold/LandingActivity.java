package com.example.testhellogold;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LandingActivity extends AppCompatActivity {

    TextView userEmail;
    SwipeRefreshLayout pullToRefresh;
    OkHttpClient client;
    String fetchUrl;
    ArrayList<SpotPrice> spotPrices;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private Context mContext;


    public void saveArrayList(ArrayList<SpotPrice> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LandingActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<SpotPrice> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LandingActivity.this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<SpotPrice>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(spotPrices.size() > 0) {
            saveArrayList(spotPrices, "prices");
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(spotPrices.size() > 0) {
            saveArrayList(spotPrices, "prices");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getArrayList("prices") != null && getArrayList("prices").size() > 0) {
            spotPrices = getArrayList("prices");
        }
        else {
            spotPrices = new ArrayList<SpotPrice>();
        }

        userEmail = (TextView)findViewById(R.id.userAuthEmail);
        fetchUrl = App.getContext().getResources().getString(R.string.price_api);

        mRecyclerView = (RecyclerView) findViewById(R.id.price_list);

        client = new OkHttpClient();

        pullToRefresh = findViewById(R.id.swipeLayout);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchData();
            }
        });
        mContext = getApplicationContext();
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SpotPriceAdapter(spotPrices,mContext);

        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchData();
            }
        });

        if(Auth.user != null){
            userEmail.setText("You are login as "+Auth.user.getUserEmail());
            fetchData();
        }
    }

    void fetchData() {

        Request request = new Request.Builder()
                .url(fetchUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String result = response.body().string();
//                final String result = "{ result: 'ok', data: { buy: '170.1' , sell: '172' , spot_price: '175', timestamp: '555'   } }";

                LandingActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject price = new JSONObject(result);
                            if(price.getString("result").equals("ok")){
                                JSONObject priceData = new JSONObject(price.getString("data"));
                                SpotPrice spotPrice = new SpotPrice(Float.parseFloat(priceData.getString("buy")),
                                        Float.parseFloat(priceData.getString("sell")),
                                        Float.parseFloat(priceData.getString("spot_price")),
                                        priceData.getString("timestamp"));
                                spotPrices.add(0, spotPrice);
                                mAdapter = new SpotPriceAdapter(spotPrices,mContext);
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


        pullToRefresh.setRefreshing(false);
    }

}
