package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

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

public class ProductListActivity extends Activity {
    private ArrayList<Product> productList = new ArrayList<Product>();
    private XRecyclerView mRecyclerView;
    private int times = 0;
    private String type;
    private ProductListAdapter mAdapter;


    public class thread extends Thread {
        private String type;
        private int refreshtype;
        public thread(String type, int refreshtype) {
            this.type = type;
            this.refreshtype = refreshtype;
        }
        @Override
        public void run() {
            super.run();
            getDbData(type, times);
            Message msg = new Message();
            msg.what = refreshtype;
            handler.sendMessage(msg);
        }
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mAdapter = new ProductListAdapter(productList);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(new ProductListAdapter.MyOnItemClickListener() {
                        @Override
                        public void OnItemClickListener(View view, int i) {
                            Product product = productList.get(i-1);
                            Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                            //intent.putExtra("param1", product.getProduct_name());
                            //getActivity().startActivity(intent);

                            try {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("productId", product.getId());
                                intent.putExtras(bundle);
                                ProductListActivity.this.startActivity(intent);
                            } catch(Throwable e){
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                case 1:
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.refreshComplete();
                    break;
                case 2:
                    mRecyclerView.loadMoreComplete();
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getStringExtra("type");


        setContentView(R.layout.activity_product_list);
        mRecyclerView = (XRecyclerView) this.findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        TextView title = (TextView) this.findViewById(R.id.product_list_title);
        switch (type) {
            case "exchange":
                title.setText("置换专区");
                break;
            case "rent":
                title.setText("租赁专区");
                break;
            case "cash":
                title.setText("公益专区");
                break;
        }
        //=========================



        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                times = 0;
                productList.clear();

                Thread plthread1 = new thread(type, 1);

                plthread1.start();
            }
            @Override
            public void onLoadMore() {
                times++;

                Thread plthread2 = new thread(type, 2);

                plthread2.start();
            }
        });

        Thread plthread = new thread(type, 0);
        plthread.start();

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
        Log.i("Request", itemsrequest.toString());

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
                                    result.getJSONObject(i).getDouble("price"),
                                    stock,
                                    GlobalParameterApplication.imgSurl+"121000239217360a3d2.jpg",
                                    descdetail,
                                    shortdesc,
                                    despUrl
                            ));
                            Log.i("Status ", "001");
                        } else
                            productList.add(new Product(result.getJSONObject(i).getInt("id"),
                                    result.getJSONObject(i).getString("name"),
                                    result.getJSONObject(i).getDouble("price"),
                                    stock,
                                    GlobalParameterApplication.axwUrl + imageurl[0],
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
