package com.aixinwu.axw.activity;

import android.app.Activity;
import android.content.ContentValues;
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
import android.widget.Toast;

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
import org.w3c.dom.Text;

import static java.lang.System.in;

public class ShoppingCartActivity extends Activity {

    private static final int MSG_WHAT = 0x223;
    private static final int MSG_NUM = 233;
    private static final int MSG_TOTAL = 456;
    private ListView mListView;

    public static ShoppingCartActivity shoppingCartActivity = null;
    //content of shoppingcart
    public ArrayList<Integer> CheckedProductId = new ArrayList<>();
    public ArrayList<ShoppingCartEntity> orderedDatas = new ArrayList<>();
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

    /**
     * 合计
     */
    private double mTotalMoney = 0;
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
                case MSG_NUM:
                    for(int i = 0; i < mDatas.size(); ++i){
                        ShoppingCartEntity spc = mDatas.get(i);
                        if(spc.getId().equals(msg.getData().getString("id"))) {
                            if (msg.getData().getInt("op") == 1)
                                spc.setNumber(spc.getNumber() + 1);
                            else if (msg.getData().getInt("op") == 2)
                                spc.setNumber(spc.getNumber() - 1);
                            mDatas.set(i, spc);
                            break;
                        }
                    }

                    mAdapter.notifyDataSetChanged();
                    break;
                case MSG_TOTAL:
                    mBtnChecking.setText("去结算(" + mTotalChecked + ")");
                    mTVTotal.setText("合计：" + mTotalMoney + "爱心币");
                    break;
            }
        }
    };

    private Thread totalThread = new Thread() {
        @Override
        public void run() {
            super.run();
            updateTotal();
        }
    };


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        shoppingCartActivity = this;

        initViews();
        initDatas();
    }

    @Override
    protected void onStart() {
       // initDatas();
        super.onStart();
    }


    //在数据库中删除物品id 为id的操作
    private void deleteFromDatabase (int id) {
        ProductReadDbHelper mDbHelper = new ProductReadDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(ProductReaderContract.ProductEntry.TABLE_NAME,
                ProductReaderContract.ProductEntry.COLUMN_NAME_ENTRY_ID + "=?",
                new String[]{id + ""}
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

    //从数据库中获取商品总数和总价并更新
    private void updateTotal() {
        ProductReadDbHelper mDbHelper = new ProductReadDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        double totalprice = 0;
        for (int i = 0; i < CheckedProductId.size(); ++i){
            int checkedid = CheckedProductId.get(i);
            Cursor cursor = db.query(ProductReaderContract.ProductEntry.TABLE_NAME,
                    new String[]{ProductReaderContract.ProductEntry.COLUMN_NAME_PRICE, ProductReaderContract.ProductEntry.COLUMN_NAME_NUMBER},
                    ProductReaderContract.ProductEntry.COLUMN_NAME_ENTRY_ID + "=?", new String[]{checkedid + ""},
                    null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                double price = Double.parseDouble(cursor.getString(cursor.getColumnIndex
                        (ProductReaderContract.ProductEntry.COLUMN_NAME_PRICE)));

                int number = Integer.parseInt(cursor.getString(cursor.getColumnIndex
                        (ProductReaderContract.ProductEntry.COLUMN_NAME_NUMBER)));

                totalprice += price * number;
            }
            //tableName, tableColumns, whereClause, whereArgs, groupBy, having, orderBy);
            cursor.close();
        }
        mTotalChecked = CheckedProductId.size();
        mTotalMoney = totalprice;
        Message msg = new Message();
        msg.what=MSG_TOTAL;
        mHandler.sendMessage(msg);
        Log.i("Total Price", mTotalMoney + " " + mTotalChecked);
    }

    //结算listerner
    private void addListeners() {

        // 全选
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTotalChecked = 0;
                    mTotalMoney = 0;
                    for (int i = 0; i < mDatas.size(); ++i) {
                        CheckedProductId.add(Integer.parseInt(mDatas.get(i).getId()));
                        orderedDatas.add(mDatas.get(i));
                        //mTotalChecked++;
                        //mTotalMoney += mDatas.get(i).getPrice() * mDatas.get(i).getNumber();
                    }
                } else {
                    //mTotalChecked = 0;
                    //mTotalMoney = 0;
                    CheckedProductId.clear();
                    orderedDatas.clear();
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }


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

                if (GlobalParameterApplication.getLogin_status() == 1 ) {
                    if ( GlobalParameterApplication.whtherBindJC == 1){
                        if (orderedDatas.size() > 0){
                            Intent intent = new Intent(getApplicationContext(), ConfirmOrder.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("size", Integer.valueOf(orderedDatas.size()));
                            bundle.putSerializable("mTotalMoney", mTotalMoney);
                            for (int i1 = 0; i1 < orderedDatas.size(); ++i1) {
                                bundle.putSerializable("OrderedData" + i1, orderedDatas.get(i1));
                                bundle.putSerializable("CheckedProductId" + i1, CheckedProductId.get(i1));
                            }
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else{
                            Toast.makeText(ShoppingCartActivity.this,"请选择商品",Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                        Toast.makeText(ShoppingCartActivity.this,"请先绑定Jaccount",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Intent intent = new Intent(ShoppingCartActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
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

                        double price = Double.parseDouble(cursor.getString(cursor.getColumnIndex
                                (ProductReaderContract.ProductEntry.COLUMN_NAME_PRICE)));

                        int number = Integer.parseInt(cursor.getString(cursor.getColumnIndex
                                (ProductReaderContract.ProductEntry.COLUMN_NAME_NUMBER)));

                        String imgurl = cursor.getString(cursor.getColumnIndex(
                                ProductReaderContract.ProductEntry.COLUMN_NAME_IMG));

                        int stock = Integer.parseInt(cursor.getString(cursor.getColumnIndex
                                (ProductReaderContract.ProductEntry.COLUMN_NAME_STOCK)));
                        ShoppingCartEntity entity = new ShoppingCartEntity(id, name, category,
                                price, number, imgurl, stock);


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


        //public  HashMap<Integer,Boolean> mMaps = new HashMap<>();

        //public  HashMap<Integer, Boolean> getMap() {
         //   return mMaps;
        //
        //public void setmMaps(HashMap<Integer, Boolean> mMaps){
        //    this.mMaps = mMaps;
        //}


        public ShoppingCartAdapter(Context context, ArrayList<ShoppingCartEntity> mDatas) {
            mInflater = LayoutInflater.from(context);
            this.mDatas = mDatas;
            //for (int i = 0; i < mDatas.size() ; i++) {
            //    mMaps.put(i, false);
            //}
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
        public View getView(final int position, View convertView, ViewGroup parent) {
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
                holder.add_amount = (Button) convertView.findViewById(R.id.shopping_cart_add);
                holder.reduce_amount = (Button) convertView.findViewById(R.id.shopping_cart_minus);
                holder.stock = (TextView) convertView.findViewById(R.id.shopping_cart_stock);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ShoppingCartEntity entity = (ShoppingCartEntity) getItem(position);
            holder.category.setText(entity.getCategory());
            holder.name.setText(entity.getName());
            holder.price.setText(entity.getPrice() + "");
            holder.number.setText(entity.getNumber() + "");
            holder.stock.setText(entity.getStock() + "");
            ImageLoader.getInstance().displayImage(entity.getImgUrl(), holder.img);

            //============================================================
            holder.add_amount.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Thread() {
                                @Override
                                public void run(){
                                    ProductReadDbHelper mDbHelper = new ProductReadDbHelper(getApplicationContext());
                                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                                    ContentValues cv = new ContentValues();
                                    int amount = entity.getNumber();
                                    if(amount < entity.getStock()) {
                                        amount++;

                                        cv.put(ProductReaderContract.ProductEntry.COLUMN_NAME_NUMBER, amount);
                                        db.update(ProductReaderContract.ProductEntry.TABLE_NAME, cv,
                                                ProductReaderContract.ProductEntry.COLUMN_NAME_ENTRY_ID + "=?",
                                                new String[]{entity.getId()});
                                        Message msg = new Message();
                                        msg.what = MSG_NUM;
                                        Bundle bundle = new Bundle();
                                        bundle.putString("id", entity.getId());
                                        bundle.putInt("op", 1);
                                        msg.setData(bundle);
                                        mHandler.sendMessage(msg);
                                        totalThread.run();
                                    }
                                }
                            }.start();
                        }
                    }
            );

            holder.reduce_amount.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Thread() {
                                @Override
                                public void run(){
                                    ProductReadDbHelper mDbHelper = new ProductReadDbHelper(getApplicationContext());
                                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                                    ContentValues cv = new ContentValues();
                                    int amount = entity.getNumber();
                                    Log.i("Amount", amount + "");
                                    if (amount > 1) {
                                        amount--;

                                        Log.i("Amount", amount + "");
                                        cv.put(ProductReaderContract.ProductEntry.COLUMN_NAME_NUMBER, amount);
                                        db.update(ProductReaderContract.ProductEntry.TABLE_NAME, cv,
                                                ProductReaderContract.ProductEntry.COLUMN_NAME_ENTRY_ID + "=?",
                                                new String[]{entity.getId()});
                                        Message msg = new Message();
                                        msg.what = MSG_NUM;
                                        Bundle bundle = new Bundle();
                                        bundle.putString("id", entity.getId());
                                        bundle.putInt("op", 2);
                                        msg.setData(bundle);
                                        mHandler.sendMessage(msg);
                                        totalThread.run();
                                    }
                                }
                            }.start();
                        }
                    }
            );
            //============================================================


            holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int itemid = Integer.parseInt(entity.getId());

                    if (isChecked) {
                        if (!CheckedProductId.contains(itemid)) {
                            CheckedProductId.add(itemid);
                            orderedDatas.add(mDatas.get(position));
                            //mTotalMoney += entity.getNumber() * entity.getPrice();
                            //mTotalChecked++;
                            Log.i("Checked3:", CheckedProductId.toString());
                        }
                    } else {
                        if (CheckedProductId.contains(itemid)) {
                            //mTotalMoney -= entity.getNumber() * entity.getPrice();
                            //mTotalChecked--;
                            CheckedProductId.remove(CheckedProductId.indexOf(itemid));
                            orderedDatas.remove(orderedDatas.indexOf(mDatas.get(position)));
                            Log.i("Checked4:", CheckedProductId.toString());
                        }

                    }
                    //mBtnChecking.setText("去结算(" + mTotalChecked + ")");
                    //mTVTotal.setText("合计：" + mTotalMoney + " 爱心币");
                    totalThread.run();
                }
            });

            if (CheckedProductId.contains(Integer.parseInt(entity.getId()))){
                holder.cb.setChecked(true);
                Log.i("Checked1:", CheckedProductId.toString());
            }
            else{
                holder.cb.setChecked(false);
                Log.i("Checked2:", CheckedProductId.toString());
            }

            return convertView;
        }

        class ViewHolder {
            CheckBox cb;
            ImageView img;
            TextView name, category, price, number, stock;
            Button add_amount, reduce_amount;
        }
    }

    /*
    @Override
    protected void onResume(){
        super.onResume();
        Log.i("YUDING","onResume");
        Message message = Message.obtain();
        message.what = MSG_WHAT;
        mHandler.sendMessage(message);
    }*/

}
