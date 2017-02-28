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
import com.aixinwu.axw.Adapter.VolunteerAdapter;
import com.aixinwu.axw.R;
import com.aixinwu.axw.activity.HelloWorld;
import com.aixinwu.axw.activity.ProductDetailActivity;
import com.aixinwu.axw.activity.ProductListActivity;
import com.aixinwu.axw.activity.VolActivityList;
import com.aixinwu.axw.activity.VolunteerApply;
import com.aixinwu.axw.activity.VolActivityList;
import com.aixinwu.axw.model.Product;
import com.aixinwu.axw.model.VolunteerActivity;
import com.aixinwu.axw.tools.Bean;
import com.aixinwu.axw.tools.GlobalParameterApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;
import android.widget.TextView;
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
    private List<VolunteerActivity> volList = new ArrayList<VolunteerActivity>();

    //public List<Product> dbData = new ArrayList<Product>();


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

        TextView productlist = (TextView) getActivity().findViewById(R.id.exchange_more);
        productlist.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProductListActivity.class);
                intent.putExtra("type", "exchange");
                getActivity().startActivity(intent);
            }
        });


        TextView leaselist = (TextView) getActivity().findViewById(R.id.lend_more_more);
        leaselist.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProductListActivity.class);
                String s = "rent";
                intent.putExtra("type", s);
                getActivity().startActivity(intent);
            }
        });

        TextView vollist = (TextView) getActivity().findViewById(R.id.vol_more_more);
        vollist.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), VolActivityList.class);

                getActivity().startActivity(intent);
            }
        });

        /*
        TextView publiclist = (TextView) getActivity().findViewById(R.id.public_more);
        publiclist.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProductListActivity.class);
                getActivity().startActivity(intent);
            }
        });
*/
        super.onStart();
    }




    public Thread mThread = new Thread(){
        @Override
        public void run(){
            super.run();
            //getDbData();
            productList = new ArrayList<Product> (getDbData("exchange"));
            leaseList =  new ArrayList<Product> (getDbData("rent"));
            volList =  new ArrayList<VolunteerActivity> (getVolunteer());
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
                    VolunteerAdapter adapter3 = new VolunteerAdapter(
                            getActivity(),
                            R.layout.volunteer_item,
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
                            bundle.putSerializable("productId", product.getId());
                            intent.putExtras(bundle);
                            getActivity().startActivity(intent);
                        }
                    });
                    GridView gridView2 = (GridView) getActivity().findViewById(R.id.grid2);
                    gridView2.setAdapter(adapter2);
                    gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Product product = leaseList.get(i);
                            Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("productId", product.getId());
                            intent.putExtras(bundle);
                            getActivity().startActivity(intent);
                        }
                    });
                    GridView gridView3 = (GridView) getActivity().findViewById(R.id.grid3);
                    gridView3.setAdapter(adapter3);
                    gridView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            VolunteerActivity product = volList.get(i);
                            Intent intent = new Intent(getActivity(), VolunteerApply.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("volActivityId", product);
                            intent.putExtras(bundle);
                            getActivity().startActivity(intent);
                        }
                    });
            }
        }
    };

    static public List<Product> getDbData(String type){
        List<Product> dbData = new ArrayList<Product>();
        String MyToken= GlobalParameterApplication.getToken();
        String surl = GlobalParameterApplication.getSurl();
        JSONObject itemsrequest = new JSONObject();
        String typestr = "";
        int start = 0;
        itemsrequest.put("startAt", start);
        itemsrequest.put("length", 100);
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
                    Log.i("LoveCoin","getconnection");
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
                       // Log.i("Inall", result.length() + "");
                        for (int i = 0; i < result.length(); i++)
                            //if (result.getJSONObject(i).getInt("status") == 0)
                            {
                                String jsonall = result.getJSONObject(i).toString();
                                Log.i("JSONALL", jsonall);
                                String [] imageurl = result.getJSONObject(i).getString("image").split(",");//==========================
                                String logname = result.getJSONObject(i).getString("name");
                                String value = result.getJSONObject(i).getString("price") + "";
                                String iid = result.getJSONObject(i).getInt("id") + "";
                                String descurl = result.getJSONObject(i).getString("desp_url");
                                String descdetail = result.getJSONObject(i).getString("desc");
                                String shortdesc = result.getJSONObject(i).getString("short_desc");
                                String despUrl   = result.getJSONObject(i).getString("desp_url");
                                int stock = result.getJSONObject(i).getInt("stock");
                                /*Log.i("Image Url", imageurl[0]);
                                Log.i("aixinwuitemid", iid);
                                Log.i("value", value);
                                Log.i("name", logname);
                                Log.i("stock", stock + "");*/
                                //Log.i("xxxx", logname + value + iid);
                                /*Log.i("Image Url", imageurl[0]);
                                Log.i("Desc", descurl + "null");*/
                                if ( imageurl[0].equals("") ) {
                                    //If no images in database, show a default image.
                                    //BitmapFactory.Options cc = new BitmapFactory.Options();
                                    //cc.inSampleSize = 20;
                                    dbData.add(new Product(result.getJSONObject(i).getInt("id"),
                                            result.getJSONObject(i).getString("name"),
                                            result.getJSONObject(i).getDouble("price"),
                                            stock,
                                            GlobalParameterApplication.imgSurl+"121000239217360a3d2.jpg",
                                            descdetail,
                                            shortdesc,
                                            despUrl
                                            ));
                                    /*Log.i("Status ", "001");*/
                                } else
                                    dbData.add(new Product(result.getJSONObject(i).getInt("id"),
                                            result.getJSONObject(i).getString("name"),
                                            result.getJSONObject(i).getDouble("price"),
                                            stock,
                                            GlobalParameterApplication.axwUrl+imageurl[0],
                                            descdetail,
                                            shortdesc,
                                            despUrl
                                    ));
                                //Log.i("Status ", "021");
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

        return dbData;
    }

    static public List<VolunteerActivity> getVolunteer(){
        List<VolunteerActivity> dbData = new ArrayList<VolunteerActivity>();
        String MyToken= GlobalParameterApplication.getToken();
        String surl = GlobalParameterApplication.getSurl();
        JSONObject itemsrequest = new JSONObject();
        itemsrequest.put("token",MyToken);

        try {
            URL url = new URL(surl + "/aixinwu_volunteer_act_get");
            try {
                Log.i("LoveCoin","getconnection");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                conn.getOutputStream().write(itemsrequest.toJSONString().getBytes());
                java.lang.String ostr = IOUtils.toString(conn.getInputStream());

                try {
                    JSONArray outjson = null;
                    outjson = new org.json.JSONArray(ostr);
                    System.out.println(ostr);
                    for (int i1 = 0; i1 < outjson.length(); ++i1){
                        int need = outjson.getJSONObject(i1).getInt("num_needed");
                        int signed = outjson.getJSONObject(i1).getInt("num_signed");
                        dbData.add(new VolunteerActivity(outjson.getJSONObject(i1).getInt("id"),
                                outjson.getJSONObject(i1).getString("image"),
                                //"http://img.taodiantong.cn/v55183/infoimg/2013-07/130720115322ky.jpg",
                                outjson.getJSONObject(i1).getString("name"),
                                outjson.getJSONObject(i1).getDouble("pay_cash"),
                                outjson.getJSONObject(i1).getString("work_date"),
                                need,
                                signed,
                                outjson.getJSONObject(i1).getDouble("workload"),
                                outjson.getJSONObject(i1).getString("site"),
                                outjson.getJSONObject(i1).getInt("joined"),
                                outjson.getJSONObject(i1).getString("about"),
                                outjson.getJSONObject(i1).getString("content")
                        ));
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

        return dbData;
    }


}

