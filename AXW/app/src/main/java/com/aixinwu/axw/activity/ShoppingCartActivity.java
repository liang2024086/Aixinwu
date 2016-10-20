package com.aixinwu.axw.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aixinwu.axw.R;
import com.aixinwu.axw.database.ProductReadDbHelper;
import com.aixinwu.axw.database.ProductReaderContract;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.aixinwu.axw.model.ShoppingCartEntity;
import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

import static java.lang.System.in;

public class ShoppingCartActivity extends AppCompatActivity {

    private static final int MSG_WHAT = 0x223;

    private ListView mListView;


    //content of shoppingcart
    public ArrayList<Integer> CheckedProductId = new ArrayList<>();
    //public HashSet<Integer> CheckedProductId = new HashSet<>();
    private Button BtnDelChecked;
    private Button BtnDelAll;
    public ArrayList<JSONObject> OrderedProduct = new ArrayList<>();

    /**
     * 结算
     */
    private Button mBtnChecking;


    private TextView mTVTotal;

    private CheckBox mCheckBox;

    private String orderid;
    /**
     * 合计
     */
    private int mTotalMoney = 0;
    private int mTotalChecked = 0;



    private ArrayList<ShoppingCartEntity> mDatas = new ArrayList<>();

    private ProductReadDbHelper mDbHelper = new ProductReadDbHelper(this);

    private ShoppingCartAdapter mAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case MSG_WHAT:
                    mAdapter = new ShoppingCartAdapter(getApplication(), mDatas);
                    mListView.setAdapter(mAdapter);
                    addListeners();
                    break;
            }
        }
    };

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        initViews();
        //initDatas();
    }

    @Override
    protected void onStart() {
        initDatas();
        super.onStart();
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

    //清空购物车的数据库操作
    private void deleteAllFromDatabase () {
        ProductReadDbHelper mDbHelper = new ProductReadDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + ProductReaderContract.ProductEntry.TABLE_NAME);
        Log.i("Delete all", " Ok");
    }

    //结算listerner
    private void addListeners() {

        // 全选
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mTotalChecked = 0;
                    mTotalMoney = 0;
                    for (int i = 0; i < mDatas.size(); ++i){
                        CheckedProductId.add(Integer.parseInt(mDatas.get(i).getId()));
                        mTotalChecked ++;
                        mTotalMoney += mDatas.get(i).getPrice() * mDatas.get(i).getNumber();
                    }
                }
                else{
                    mTotalChecked = 0;
                    mTotalMoney = 0;
                    CheckedProductId.clear();
                }
                mAdapter.notifyDataSetChanged();
            }
        });
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
                    orderid = outjson.getInt("order_id") + "";
                    Log.i("Order successful", outjson.toString());
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
                    Intent intent = new Intent(ShoppingCartActivity.this, DealFinished.class);
                    intent.putExtra("overallcost", mTotalMoney + "");
                    intent.putExtra("orderid", orderid);
                    startActivity(intent);
            }
        }
    };

    private void initViews() {

        mListView = (ListView) findViewById(R.id.lv_shopping_cart_activity);
        mBtnChecking = (Button) findViewById(R.id.btn_activity_shopping_cart_clearing);
        mTVTotal = (TextView) findViewById(R.id.tv_activity_shopping_cart_total);
        mCheckBox = (CheckBox) findViewById(R.id.cb_activity_shopping_cart);
        BtnDelChecked = (Button) findViewById(R.id.btn_delete_selected_product);
        BtnDelAll = (Button) findViewById(R.id.btn_delete_all_product);
        mBtnChecking = (Button) findViewById(R.id.btn_activity_shopping_cart_clearing);

        // 最终结算
        mBtnChecking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create checked list
                createOrderList();

                //delete from database
                for (int i = 0; i < CheckedProductId.size(); ++i) {
                    deleteFromDatabase(CheckedProductId.get(i));
                }
                //====
                //send order request to the server

                oThread.start();


            }
        });

        // 删除所选商品
        BtnDelChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //delete from database

                for (int i = 0; i < CheckedProductId.size(); ++i) {
                    deleteFromDatabase(CheckedProductId.get(i));
                }
                //====
                finish();
                Intent intent = new Intent(ShoppingCartActivity.this, ShoppingCartActivity.class);
                startActivity(intent);
            }
        });

        // 清空购物车
        BtnDelAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //delete from database

                deleteAllFromDatabase();
                //====
                finish();
                Intent intent = new Intent(ShoppingCartActivity.this, ShoppingCartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initDatas() {
//初始化购物车数据
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        final Cursor cursor = db.query(ProductReaderContract.ProductEntry.TABLE_NAME, null, null, null, null, null, null);

        mDatas.clear();

        new Thread() {
            @Override
            public void run() {
                if (cursor != null) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        String id = cursor.getString(cursor.getColumnIndex
                                (ProductReaderContract.ProductEntry
                                        .COLUMN_NAME_ENTRY_ID));

                        String name = cursor.getString(cursor.getColumnIndex
                                (ProductReaderContract.ProductEntry
                                        .COLUMN_NAME_NAME));

                        String category = cursor.getString(cursor.getColumnIndex
                                (ProductReaderContract.ProductEntry
                                        .COLUMN_NAME_CATEGORY));

                        int price = Integer.parseInt(cursor.getString(cursor.getColumnIndex
                                (ProductReaderContract.ProductEntry.COLUMN_NAME_PRICE)));

                        int number = Integer.parseInt(cursor.getString(cursor.getColumnIndex
                                (ProductReaderContract.ProductEntry.COLUMN_NAME_NUMBER)));

                        String imgurl = cursor.getString(cursor.getColumnIndex(
                                ProductReaderContract.ProductEntry.COLUMN_NAME_IMG));

                        ShoppingCartEntity entity = new ShoppingCartEntity(id, name, category,
                                price, number, imgurl);


                        mDatas.add(entity);
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                    if (db != null && db.isOpen()) {
                        db.close();
                    }
                    Message message = Message.obtain();
                    message.what = MSG_WHAT;
                    mHandler.sendMessage(message);
                }
            }
        }.start();

        mBtnChecking.setText("去结算(0)");
        mTVTotal.setText("合计：0 爱心币");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_cart, menu);
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


    //购物车列表adapter
    private class ShoppingCartAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private ArrayList<ShoppingCartEntity> mDatas = new ArrayList<>();
        private ViewHolder holder;
        public Bitmap bitmap;

        public  HashMap<Integer,Boolean> mMaps = new HashMap<>();

        public  HashMap<Integer, Boolean> getMap() {
            return mMaps;
        }

        public void setmMaps(HashMap<Integer, Boolean> mMaps){
            this.mMaps = mMaps;
        }


        public ShoppingCartAdapter(Context context, ArrayList<ShoppingCartEntity> mDatas) {
            mInflater = LayoutInflater.from(context);
            this.mDatas = mDatas;
            for (int i = 0; i < mDatas.size() ; i++) {
                mMaps.put(i, false);
            }
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_shopping_cart, parent, false);
                holder.cb = (CheckBox) convertView.findViewById(R.id.cb_item_shopping_cart);
                holder.name = (TextView) convertView.findViewById(R.id.tv_item_shopping_cart_name);
                holder.category = (TextView) convertView.findViewById(R.id
                        .tv_item_shopping_cart_category);
                holder.price = (TextView) convertView.findViewById(R.id
                        .tv_item_shopping_cart_price);
                holder.number = (TextView) convertView.findViewById(R.id
                        .tv_item_shopping_cart_number);
                holder.img = (ImageView) convertView.findViewById(R.id
                        .img_item_shopping_cart_number);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ShoppingCartEntity entity = (ShoppingCartEntity) getItem(position);
            holder.category.setText(entity.getCategory());
            holder.name.setText(entity.getName());
            holder.price.setText(entity.getPrice() + "");
            holder.number.setText("x" + entity.getNumber());
//============================================================
            ImageLoader.getInstance().displayImage(entity.getImgUrl(), holder.img);


            //============================================================

            //holder.img.setImageResource(R.drawable.aixinwu);           //缩略图片显示

            if (CheckedProductId.contains(Integer.parseInt(entity.getId()))){
                holder.cb.setChecked(true);
            }
            else{
                holder.cb.setChecked(false);
            }


            //holder.cb.setChecked(getMap().get(position));

            holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int itemid = Integer.parseInt(entity.getId());

                    if (isChecked) {
                        if (!CheckedProductId.contains(itemid)) {
                            CheckedProductId.add(itemid);
                            mTotalMoney += entity.getNumber() * entity.getPrice();
                            mTotalChecked++;
                        }
                    } else {
                        if (CheckedProductId.contains(itemid)) {
                            mTotalMoney -= entity.getNumber() * entity.getPrice();
                            mTotalChecked--;
                            CheckedProductId.remove(CheckedProductId.indexOf(itemid));
                        }

                    }
                    mBtnChecking.setText("去结算(" + mTotalChecked + ")");
                    mTVTotal.setText("合计：" + mTotalMoney + " 爱心币");
                }
            });

            return convertView;
        }

        class ViewHolder {
            CheckBox cb;
            ImageView img;
            TextView name, category, price, number;
        }
    }




}
