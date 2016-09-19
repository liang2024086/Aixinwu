package com.aixinwu.axw.fragment;

//import org.json.JSONObject;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.aixinwu.axw.Adapter.ProductAdapter;
import com.aixinwu.axw.R;
import com.aixinwu.axw.activity.HelloWorld;
import com.aixinwu.axw.activity.ProductDetailActivity;
import com.aixinwu.axw.activity.ProductListActivity;
import com.aixinwu.axw.model.Product;
import com.aixinwu.axw.tools.Bean;
import com.aixinwu.axw.tools.GlobalParameterApplication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

/**
 * Created by liangyuding on 2016/4/6.
 */
public class LoveCoin extends Fragment {

    private List<Product> productList = new ArrayList<Product>();
    private List<Product> leaseList = new ArrayList<Product>();
    private List<Product> volList = new ArrayList<Product>();

    public List<Product> dbData = new ArrayList<Product>();


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.love_coin,null);


        mThread.start();


        return view;
    }





    @Override
    public void onStart () {

        ImageView productlist = (ImageView) getActivity().findViewById(R.id.imageButton_1);
        productlist.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProductListActivity.class);
                getActivity().startActivity(intent);
            }
        });


        super.onStart();
    }


    private void initProduct () {




        //dbData;

        /*
        Product product1 = new Product("A new Compiler Principle book", 100, R.mipmap.product1);
        productList.add(product1);
        Product product2 = new Product("A new Hadoop book", 100, R.mipmap.product2);
        productList.add(product2);
        Product product3 = new Product("A new Coding book", 100, R.mipmap.product3);
        productList.add(product3);
        Product product4 = new Product("A new C++ primer book", 100, R.mipmap.product4);
        productList.add(product4);
        Product leaseproduct1 = new Product("A new Compiler Principle book", 100, R.mipmap.product1);
        leaseList.add(leaseproduct1);
        Product leaseproduct2 = new Product("A new Hadoop book", 100, R.mipmap.product2);
        leaseList.add(leaseproduct2);
        Product leaseproduct3 = new Product("A new Coding book", 100, R.mipmap.product3);
        leaseList.add(leaseproduct3);
        Product leaseproduct4 = new Product("A new C++ primer book", 100, R.mipmap.product4);
        leaseList.add(leaseproduct4);
        Product volproduct1 = new Product("A new Compiler Principle book", 100, R.mipmap.product1);
        volList.add(volproduct1);
        Product volproduct2 = new Product("A new Hadoop book", 100, R.mipmap.product2);
        volList.add(volproduct2);
        Product volproduct3 = new Product("A new Coding book", 100, R.mipmap.product3);
        volList.add(volproduct3);
        Product volproduct4 = new Product("A new C++ primer book", 100, R.mipmap.product4);
        volList.add(volproduct4);
    */

    }


    public Thread mThread = new Thread(){
        @Override
        public void run(){
            super.run();
            getDbData();
            productList = new ArrayList<Product> (dbData);
            leaseList =  new ArrayList<Product> (dbData);
            volList =  new ArrayList<Product> (dbData);
            Message msg = new Message();
            msg.what=1321;
            nHandler.sendMessage(msg);
        }

    };

    public Handler nHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1321:
                    ProductAdapter adapter1 = new ProductAdapter(
                            getActivity(),
                            R.layout.product_item,
                            productList
                    );
                    ProductAdapter adapter2 = new ProductAdapter(
                            getActivity(),
                            R.layout.product_item,
                            leaseList
                    );
                    ProductAdapter adapter3 = new ProductAdapter(
                            getActivity(),
                            R.layout.product_item,
                            volList
                    );
                    //Log.i("LoveCoin", "init end" + productList.get(0).getImage_url() );
                    GridView gridView1 = (GridView) getActivity().findViewById(R.id.grid1);
                    gridView1.setAdapter (adapter1);
                    gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Product product = productList.get(i);
                            Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                            //intent.putExtra("param1", product.getProduct_name());
                            //getActivity().startActivity(intent);

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("product", product);
                            intent.putExtras(bundle);
                            getActivity().startActivity(intent);
                        }
                    });
                    GridView gridView2 = (GridView) getActivity().findViewById(R.id.grid2);
                    gridView2.setAdapter(adapter2);
                    GridView gridView3 = (GridView) getActivity().findViewById(R.id.grid3);
                    gridView3.setAdapter(adapter3);
            }
        }
    };

    private void getDbData(){
        String MyToken= GlobalParameterApplication.getToken();
        String surl = GlobalParameterApplication.getSurl();
        JSONObject data = new JSONObject();

        int start = 0;
        data.put("startAt", start);
        data.put("length", 12);
        //data.put("token", MyToken);

        Log.i("LoveCoin", "get");

        try {
                URL url = new URL(surl + "/item_aixinwu_item_get_list");
                try {
                    Log.i("LoveCoin","getconnection");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    conn.getOutputStream().write(data.toJSONString().getBytes());
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
                                String [] imageurl = result.getJSONObject(i).getString("image").split(",");//==========================
                                String logname = result.getJSONObject(i).getString("name");
                                String value = result.getJSONObject(i).getInt("price") + "";
                                String iid = result.getJSONObject(i).getInt("id") + "";
                                String descurl = result.getJSONObject(i).getString("desp_url");
                                String descdetail = result.getJSONObject(i).getString("desc");
                                String shortdesc = result.getJSONObject(i).getString("short_desc");
                                String despUrl   = result.getJSONObject(i).getString("desp_url");
                                Log.i("Image Url", imageurl[0]);
                                Log.i("aixinwuitemid", iid);
                                Log.i("value", value);
                                Log.i("name", logname);
                                //Log.i("xxxx", logname + value + iid);
                                Log.i("Image Url", imageurl[0]);
                                Log.i("Desc", descurl + "null");
                                if ( imageurl[0].equals("") ) {
                                    //If no images in database, show a default image.
                                    //BitmapFactory.Options cc = new BitmapFactory.Options();
                                    //cc.inSampleSize = 20;
                                    dbData.add(new Product(result.getJSONObject(i).getInt("id"),
                                            result.getJSONObject(i).getString("name"),
                                            result.getJSONObject(i).getInt("price"),
                                            "http://202.120.47.213:12345/img/121000239217360a3d2.jpg",
                                            descdetail,
                                            shortdesc,
                                            despUrl
                                            ));
                                    Log.i("Status ", "001");
                                } else
                                    dbData.add(new Product(result.getJSONObject(i).getInt("id"),
                                            result.getJSONObject(i).getString("name"),
                                            result.getJSONObject(i).getInt("price"),
                                            "http://202.120.47.213:12345/"+imageurl[0],
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

