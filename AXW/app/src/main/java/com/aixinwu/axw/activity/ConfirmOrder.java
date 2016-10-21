package com.aixinwu.axw.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.aixinwu.axw.Adapter.ConfirmOrderAdapter;
import com.aixinwu.axw.R;
import com.aixinwu.axw.database.ProductReadDbHelper;
import com.aixinwu.axw.database.ProductReaderContract;
import com.aixinwu.axw.model.Product;
import com.aixinwu.axw.model.ShoppingCartEntity;
import com.aixinwu.axw.tools.GlobalParameterApplication;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ConfirmOrder extends Activity {

    private ArrayList<ShoppingCartEntity> mDatas = new ArrayList<>();
    public ArrayList<Integer> CheckedProductId = new ArrayList<>();
    private ConfirmOrderAdapter mAdapter;
    private ListView commodityList;
    public ArrayList<JSONObject> OrderedProduct = new ArrayList<>();
    private int mTotalMoney = 0;
    private int size = 0;
    private int orderid = -1;
    private TextView order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        initData();
        commodityList = (ListView)findViewById(R.id.commodityList);
        mAdapter = new ConfirmOrderAdapter(this,mDatas);
        commodityList.setAdapter(mAdapter);
        ((TextView)findViewById(R.id.numberOfCommodity)).setText("共" + size + "件商品");
        ((TextView)findViewById(R.id.totalMoney)).setText("合计：" + mTotalMoney);
        order = (TextView)findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete from database
                for (int i = 0; i < CheckedProductId.size(); ++i) {
                    deleteFromDatabase(CheckedProductId.get(i));
                }
                //====
                //send order request to the server
                oThread.start();
            }
        });
    }

    //在数据库中删除物品id 为id的操作
    private void deleteFromDatabase (int id) {
        ProductReadDbHelper mDbHelper = new ProductReadDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(ProductReaderContract.ProductEntry.TABLE_NAME,
                ProductReaderContract.ProductEntry.COLUMN_NAME_ENTRY_ID+"=?",
                new String[]{id+""}
        );
        Log.i("Check!", id + " Ok");
    }

    public void initData(){
        Intent intent = this.getIntent();
        size = (Integer)intent.getSerializableExtra("size");
        mTotalMoney = (Integer)intent.getSerializableExtra("mTotalMoney");
        for (int i = 0; i < size; ++i){
            mDatas.add((ShoppingCartEntity)intent.getSerializableExtra("OrderedData"+i));
            CheckedProductId.add((Integer)intent.getSerializableExtra("CheckedProductId"+i));
           // JSONObject abc = (JSONObject)intent.getSerializableExtra("OrderedProduct"+i);
           // OrderedProduct.add(abc);
        }

        createOrderList();
        Log.i("YUDING", "YES");
    }


    //创建JSONArray 用于order
    private void createOrderList () {
        int quant = 0;
        for (int i = 0; i < CheckedProductId.size(); i++) {
            JSONObject orderproduct = new JSONObject();
            String index = CheckedProductId.get(i).toString();
            ProductReadDbHelper mDbHelper = new ProductReadDbHelper(getApplicationContext());
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            Cursor cursor = db.query(ProductReaderContract.ProductEntry.TABLE_NAME,
                    new String[]{ProductReaderContract.ProductEntry.COLUMN_NAME_NUMBER},
                    ProductReaderContract.ProductEntry.COLUMN_NAME_ENTRY_ID + "=?",
                    new String[] {index},
                    null,
                    null,
                    null
            );
            //String SELECT_PRODUCT_QUANT = "Select number From entry Where product_id=?";
            //Cursor cursor = db.rawQuery(SELECT_PRODUCT_QUANT, new String[]{index});
            if (cursor.moveToNext()) {
                quant = cursor.getInt(cursor.getColumnIndex("number"));
            }
            cursor.close();
            orderproduct.put("product_id", CheckedProductId.get(i));
            //orderproduct.put("product_id", CheckedProductId.get(i));
            orderproduct.put("isbook", 0);
            orderproduct.put("quantity", quant);
            Log.i("Orderproduct", orderproduct.toString());
            OrderedProduct.add(orderproduct);
            Log.i("Orderedlist:", OrderedProduct.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_confirm_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //链接服务器
    private void order(){
        String MyToken= GlobalParameterApplication.getToken();
        String surl = GlobalParameterApplication.getSurl();
        int userid = GlobalParameterApplication.getUserID();
        JSONObject orderrequest = new JSONObject();

        orderrequest.put("token", MyToken);
        orderrequest.put("order_info", OrderedProduct);
        orderrequest.put("consignee_id", userid);


        //data.put("token", MyToken);


        try {
            URL url = new URL(surl + "/item_aixinwu_item_make_order");
            try {
                Log.i("Order","getconnection");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                Log.i("orderorder", orderrequest.toJSONString());
                conn.getOutputStream().write(orderrequest.toJSONString().getBytes());

                java.lang.String ostr = IOUtils.toString(conn.getInputStream());
                org.json.JSONObject outjson = null;
                try{
                    outjson = new org.json.JSONObject(ostr);
                    //orderid = outjson.getInt("order_id") + "";
                    orderid = outjson.getJSONObject("status").getInt("code");
                    Log.i("Order successful", outjson.toString()+"id: "+orderid);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }


    public Thread oThread = new Thread() {
        @Override
        public void run() {
            super.run();
            order();
                        Message msg = new Message();
            msg.what = 1994;
            oHandler.sendMessage(msg);
        }
    };

    public Handler oHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 1994:
                    /*Intent intent = new Intent(ConfirmOrder.this, DealFinished.class);
                    intent.putExtra("overallcost", mTotalMoney + "");
                    intent.putExtra("orderid", orderid);
                    startActivity(intent);*/
                    String dialogContent = "";
                    if (orderid == 0)
                        dialogContent = "商品购买成功";
                    else
                        dialogContent = "商品购买失败";
                    new  AlertDialog.Builder(ConfirmOrder.this)
                            .setTitle("消息")
                            .setMessage(dialogContent)
                            .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int ii) {
                                    new Thread(){
                                        @Override
                                        public void run(){
                                            super.run();

                                            Intent intent = new Intent(getApplication(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                    }.start();
                                }
                            })
                            .show();
            }
        }
    };

}
