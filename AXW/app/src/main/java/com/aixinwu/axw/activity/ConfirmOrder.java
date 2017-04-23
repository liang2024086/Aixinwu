package com.aixinwu.axw.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aixinwu.axw.Adapter.ConfirmOrderAdapter;
import com.aixinwu.axw.Adapter.ReceiverAdapter;
import com.aixinwu.axw.R;
import com.aixinwu.axw.database.ProductReadDbHelper;
import com.aixinwu.axw.database.ProductReaderContract;
import com.aixinwu.axw.model.Product;
import com.aixinwu.axw.model.ShoppingCartEntity;
import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.aixinwu.axw.model.Consignee;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.w3c.dom.Text;

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
    private double mTotalMoney = 0;
    private int size = 0;
    private int orderid = -1;
    private TextView order;

    private TextView consigneeName;
    private TextView stuId;
    private TextView phone;

    private List<Consignee> consignees = new ArrayList<>();
    private Consignee commonConsigne;

    private RelativeLayout edit;
    private RelativeLayout submitRelative;

    private TextView submit;
    private TextView cancel;

    private EditText editName;
    private EditText editStuId;
    private EditText editPhone;

    private Handler dHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 532394:
                    Toast.makeText(ConfirmOrder.this,"请检查输入信息是否正确",Toast.LENGTH_SHORT).show();
                    break;
                case 234567:
                    consigneeName.setText(commonConsigne.getName());
                    stuId.setText(commonConsigne.getStuId());
                    phone.setText(commonConsigne.getPhoneNumber());
                    break;
                case 234242 :
                    Toast.makeText(ConfirmOrder.this,"修改成功",Toast.LENGTH_SHORT).show();
                    submitRelative.setVisibility(View.GONE);
                    edit.setVisibility(View.VISIBLE);
                    consigneeName.setVisibility(View.VISIBLE);
                    stuId.setVisibility(View.VISIBLE);
                    phone.setVisibility(View.VISIBLE);
                    editName.setVisibility(View.GONE);
                    editName.setText("");
                    editStuId.setText("");
                    editPhone.setText("");
                    editStuId.setVisibility(View.GONE);
                    editPhone.setVisibility(View.GONE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            GetAddress();
                            Message msg = new Message();
                            msg.what = 234567;
                            dHandler.sendMessage(msg);
                        }
                    }).start();

                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        editName = (EditText) findViewById(R.id.editName);
        editStuId = (EditText) findViewById(R.id.editStuId);
        editPhone = (EditText) findViewById(R.id.editPhone);

        consigneeName = (TextView) findViewById(R.id.name);
        stuId = (TextView) findViewById(R.id.stuId);
        phone = (TextView) findViewById(R.id.phone);

        submitRelative = (RelativeLayout) findViewById(R.id.submitRelative);

        cancel = (TextView) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRelative.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                consigneeName.setVisibility(View.VISIBLE);
                stuId.setVisibility(View.VISIBLE);
                phone.setVisibility(View.VISIBLE);
                editName.setVisibility(View.GONE);
                editName.setText("");
                editStuId.setText("");
                editPhone.setText("");
                editStuId.setVisibility(View.GONE);
                editPhone.setVisibility(View.GONE);
            }
        });

        submit = (TextView) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ConfirmOrder.this,"SUBMIT",Toast.LENGTH_SHORT).show();
                        Message msg = new Message();
                        int status = changeCosingnee();
                        if (status == 0){
                            msg.what=234242;
                            dHandler.sendMessage(msg);
                        }
                        else{
                            msg.what = 532394;
                            dHandler.sendMessage(msg);
                        }
                    }
                }).start();
            }
        });

        edit = (RelativeLayout) findViewById(R.id.editRelative);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRelative.setVisibility(View.VISIBLE);
                edit.setVisibility(View.GONE);
                consigneeName.setVisibility(View.GONE);
                stuId.setVisibility(View.GONE);
                phone.setVisibility(View.GONE);
                editName.setVisibility(View.VISIBLE);
                editStuId.setVisibility(View.VISIBLE);
                editPhone.setVisibility(View.VISIBLE);

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                GetAddress();
                Message msg = new Message();
                msg.what = 234567;
                dHandler.sendMessage(msg);
            }
        }).start();

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

                final ProgressDialog progressDialog = new ProgressDialog(ConfirmOrder.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("结算中...");
                progressDialog.setCancelable(false);
                progressDialog.show();

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
        mTotalMoney = (Double)intent.getSerializableExtra("mTotalMoney");
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
                    else if(orderid == 5)
                        dialogContent = "爱心币余额不足";
                    else if (orderid == 10)
                        dialogContent = "商品购买数量已达到限购上限，无法购买";
                    else
                        dialogContent = "商品购买失败";
                    new  AlertDialog.Builder(ConfirmOrder.this)
                            .setTitle("消息")
                            .setMessage(dialogContent)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int ii) {
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            super.run();

                                            /*Intent intent = new Intent(getApplication(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);*/

                                            if (ShoppingCartActivity.shoppingCartActivity != null)
                                                ShoppingCartActivity.shoppingCartActivity.finish();
                                            if (ProductDetailActivity.productDetailActivity != null)
                                                ProductDetailActivity.productDetailActivity.finish();
                                            finish();

                                        }
                                    }.start();
                                }
                            }).setCancelable(false)
                            .show();
            }
        }
    };

    public void GetAddress(){
        URL url = null;
        try {
            url = new URL(GlobalParameterApplication.getSurl() + "/usr_get_address");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/json");
            JSONObject data = new JSONObject();
            data.put("token",GlobalParameterApplication.getToken());
            conn.getOutputStream().write(data.toJSONString().getBytes());
            String ostr = IOUtils.toString(conn.getInputStream());
            System.out.println(ostr);
            org.json.JSONObject result=null;
            try {
                org.json.JSONObject outjson = new org.json.JSONObject(ostr);
                org.json.JSONArray outt=null;
                outt=outjson.getJSONArray("address");

                for (int i = 0; i < outt.length(); ++i){
                    consignees.add(new Consignee(
                            outt.getJSONObject(i).getString("consignee"),
                            outt.getJSONObject(i).getString("snum"),
                            outt.getJSONObject(i).getString("mobile"),
                            outt.getJSONObject(i).getInt("id"),
                            outt.getJSONObject(i).getInt("customer_id"),
                            outt.getJSONObject(i).getString("email"),
                            outt.getJSONObject(i).getInt("is_default")
                    ));

                    if (outt.getJSONObject(i).getInt("is_default")==1){
                        commonConsigne = new Consignee(
                                outt.getJSONObject(i).getString("consignee"),
                                outt.getJSONObject(i).getString("snum"),
                                outt.getJSONObject(i).getString("mobile"),
                                outt.getJSONObject(i).getInt("id"),
                                outt.getJSONObject(i).getInt("customer_id"),
                                outt.getJSONObject(i).getString("email"),
                                outt.getJSONObject(i).getInt("is_default")
                        );
                    }
                }
                System.out.println("GOOD\n"+outt.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int changeCosingnee(){
        URL url = null;
        int status = -1;
        try {
            url = new URL(GlobalParameterApplication.getSurl() + "/usr_set_address");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String newName = editName.getText().toString();
        String newStuId = stuId.getText().toString();
        String newPhone = editPhone.getText().toString();
        if (newName.length() >= 2 && newStuId.length() >= 10 && newPhone.length()==11){

            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type","application/json");
                JSONObject data = new JSONObject();
                data.put("token",GlobalParameterApplication.getToken());
                data.put("consignee",newName);
                data.put("snum",newStuId);
                data.put("mobile",newPhone);
                conn.getOutputStream().write(data.toJSONString().getBytes());
                String ostr = IOUtils.toString(conn.getInputStream());
                System.out.println(ostr);
                org.json.JSONObject result=null;
                try {
                    org.json.JSONObject outjson = new org.json.JSONObject(ostr);
                    org.json.JSONArray outt=null;

                    status = outjson.getJSONObject("status").getInt("code");

                    System.out.println("GOOD\n"+outjson.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return status;
    }

}
