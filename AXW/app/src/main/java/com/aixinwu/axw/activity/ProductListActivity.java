package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.aixinwu.axw.Adapter.ProductAdapter;
import com.aixinwu.axw.Adapter.ProductListAdapter;
import com.aixinwu.axw.R;
import com.aixinwu.axw.model.Product;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jcodecraeer.xrecyclerview.*;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

public class ProductListActivity extends AppCompatActivity {
    private ArrayList<Product> productList = new ArrayList<Product>();
    private XRecyclerView mRecyclerView;
    private int times = 0;
    private ArrayList<String> listData;
    private ProductListAdapter mAdapter;


    public class thread extends Thread {
        private String type;

        public thread(String type) {
            this.type = type;
        }

        @Override
        public void run() {
            super.run();
            getDbData("cash", times);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);


    }

    @Override
    public void onStart() {

        mRecyclerView = (XRecyclerView) this.findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        //==
        Thread plthread = new thread("cash");
        plthread.start();
        //==

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                                              public void run() {
                                                  times = 0;
                                                  productList.clear();
                                                  Thread plthread1 = new thread("cash");
                                                  plthread1.start();
                                                  try {
                                                      plthread1.join();
                                                  } catch (InterruptedException e) {
                                                      e.printStackTrace();
                                                  }
                                                  mAdapter.notifyDataSetChanged();
                                                  mRecyclerView.refreshComplete();
                                              }
                                          }, 1000);
                /*
                refreshTime++;
                times = 0;
                new Handler().postDelayed(new Runnable() {
                    public void run() {

                        listData.clear();
                        for (int i = 0; i < 20; i++) {
                            listData.add("item" + i + "after " + refreshTime + " times of refresh");
                        }
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.refreshComplete();
                    }

                }, 1000);            //refresh data here
                */

            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                                              public void run() {
                                                  times++;
                                                  Thread plthread2 = new thread("cash");
                                                  plthread2.start();
                                                  //==
                                                  try {
                                                      plthread2.join();
                                                  } catch (InterruptedException e) {
                                                      e.printStackTrace();
                                                  }
                                                  mRecyclerView.loadMoreComplete();
                                                  mAdapter.notifyDataSetChanged();
                                              }
                                          }, 1000);
                /*
                if (times < 2) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            mRecyclerView.loadMoreComplete();
                            for (int i = 0; i < 20; i++) {
                                listData.add("item" + (i + listData.size()));
                            }
                            mRecyclerView.loadMoreComplete();
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 1000);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            for (int i = 0; i < 9; i++) {
                                listData.add("item" + (i + listData.size()));
                            }
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.setIsnomore(true);
                            //mRecyclerView.setNoMore(true);
                        }
                    }, 1000);
                }
                */

            }
        });
/*
        listData = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            listData.add("item" + i);
        }
        */
        mAdapter = new ProductListAdapter(productList);
        mRecyclerView.setAdapter(mAdapter);
/*
        GridView gridView1 = (GridView) findViewById(R.id.grid_product);
        gridView1.setAdapter(adapter1);
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = productList.get(i);
                Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                intent.putExtra("param1", product.getProduct_name());
                startActivity(intent);
            }
        });
*/
        try {
            plthread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private void getDbData(String type, int times) {
        //List<Product> ProductList = new ArrayList<Product>();
        String MyToken = GlobalParameterApplication.getToken();
        String surl = GlobalParameterApplication.getSurl();
        JSONObject itemsrequest = new JSONObject();
        String typestr = "";
        int start = 9 * times;
        itemsrequest.put("startAt", start);
        itemsrequest.put("length", 9);
        switch (type) {
            case "exchange":
                typestr = "置换";
                break;
            case "rent":
                typestr = "租赁";
                break;
            case "cash":
                typestr = "现金";
                break;
        }

        itemsrequest.put("type", typestr);
        //data.put("token", MyToken);
        Log.i("LoveCoin", "get");

        try {
            URL url = new URL(surl + "/item_aixinwu_item_get_list");
            try {
                Log.i("LoveCoin", "getconnection");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                conn.getOutputStream().write(itemsrequest.toJSONString().getBytes());
                java.lang.String ostr = IOUtils.toString(conn.getInputStream());
                org.json.JSONObject outjson = null;

                try {
                    JSONArray result = null;
                    outjson = new org.json.JSONObject(ostr);
                    result = outjson.getJSONArray("items");
                    start += result.length();
                    Log.i("Inall", result.length() + "");
                    for (int i = 0; i < result.length(); i++)
                    //if (result.getJSONObject(i).getInt("status") == 0)
                    {
                        String jsonall = result.getJSONObject(i).toString();
                        Log.i("JSONALL", jsonall);

                        String[] imageurl = result.getJSONObject(i).getString("image").split(",");//==========================
                        /*
                        String logname = result.getJSONObject(i).getString("name");
                        String value = result.getJSONObject(i).getInt("price") + "";
                        String iid = result.getJSONObject(i).getInt("id") + "";
                        String descurl = result.getJSONObject(i).getString("desp_url");
                        String descdetail = result.getJSONObject(i).getString("desc");
                        String shortdesc = result.getJSONObject(i).getString("short_desc");
                        String despUrl   = result.getJSONObject(i).getString("desp_url");
                        int stock = result.getJSONObject(i).getInt("stock");
                        Log.i("Image Url", imageurl[0]);
                        Log.i("aixinwuitemid", iid);
                        Log.i("value", value);
                        Log.i("name", logname);
                        Log.i("stock", stock + "");
                        //Log.i("xxxx", logname + value + iid);
                        Log.i("Image Url", imageurl[0]);
                        Log.i("Desc", descurl + "null");
                        */
                        String descdetail = result.getJSONObject(i).getString("desc");
                        String shortdesc = result.getJSONObject(i).getString("short_desc");
                        String despUrl = result.getJSONObject(i).getString("desp_url");
                        int stock = result.getJSONObject(i).getInt("stock");
                        if (imageurl[0].equals("")) {
                            //If no images in database, show a default image.
                            //BitmapFactory.Options cc = new BitmapFactory.Options();
                            //cc.inSampleSize = 20;
                            productList.add(new Product(result.getJSONObject(i).getInt("id"),
                                    result.getJSONObject(i).getString("name"),
                                    result.getJSONObject(i).getInt("price"),
                                    stock,
                                    "http://202.120.47.213:12345/img/121000239217360a3d2.jpg",
                                    descdetail,
                                    shortdesc,
                                    despUrl
                            ));
                            Log.i("Status ", "001");
                        } else
                            productList.add(new Product(result.getJSONObject(i).getInt("id"),
                                    result.getJSONObject(i).getString("name"),
                                    result.getJSONObject(i).getInt("price"),
                                    stock,
                                    "http://202.120.47.213:12345/" + imageurl[0],
                                    descdetail,
                                    shortdesc,
                                    despUrl
                            ));
                        Log.i("Status ", "021");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
