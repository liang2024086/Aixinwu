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
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import com.aixinwu.axw.model.ShoppingCartEntity;

public class ShoppingCartActivity extends AppCompatActivity {

    private static final int MSG_WHAT = 0x223;

    private ListView mListView;


    //content of shoppingcart
    public ArrayList<Integer> CheckedProductId = new ArrayList<>();
    private Button BtnDelChecked;
    private Button BtnDelAll;


    /**
     * 结算
     */
    private Button mBtnChecking;


    private TextView mTVTotal;

    private CheckBox mCheckBox;

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


    private void deleteFromDatabase (int id) {
        ProductReadDbHelper mDbHelper = new ProductReadDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(ProductReaderContract.ProductEntry.TABLE_NAME,
                ProductReaderContract.ProductEntry.COLUMN_NAME_ENTRY_ID+"=?",
                new String[]{id+""}
                );
        Log.i("Check!", id + " Ok");
    }

    private void deleteAllFromDatabase () {
        ProductReadDbHelper mDbHelper = new ProductReadDbHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + ProductReaderContract.ProductEntry.TABLE_NAME);
        Log.i("Delete all", " Ok");
    }

    private void addListeners() {

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
                for (int i = 0; i < mDatas.size() ; i++) {
                    map.put(i, isChecked);
                }
                mAdapter.setmMaps(map);
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

        mBtnChecking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //delete from database

                for (int i = 0; i < CheckedProductId.size(); ++i) {
                    deleteFromDatabase(CheckedProductId.get(i));
                }
                //====
                //send order request to the server





                Intent intent = new Intent(ShoppingCartActivity.this, DealFinished.class);
                intent.putExtra("overallcost", mTotalMoney + "");

                startActivity(intent);
            }
        });


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

        mBtnChecking.setText("去结算（0)");
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
            final Handler nhandler=new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0x4869) {
                        //显示从网上下载的图片
                        holder.img.setImageBitmap(bitmap);
                    }
                }
            };

            new Thread(){
                @Override
                public void run() {
                    try {
                        //创建一个url对象
                        URL url=new URL(entity.getImgUrl());
                        //打开URL对应的资源输入流
                        InputStream is= url.openStream();
                        //从InputStream流中解析出图片
                        bitmap = BitmapFactory.decodeStream(is);
                        //  imageview.setImageBitmap(bitmap);

                        //发送消息，通知UI组件显示图片
                        nhandler.sendEmptyMessage(0x4869);
                        //关闭输入流
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            //============================================================

            //holder.img.setImageResource(R.drawable.aixinwu);           //缩略图片显示




            holder.cb.setChecked(getMap().get(position));

            holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int itemid = Integer.parseInt(entity.getId());
                    if (isChecked) {
                        mTotalMoney += entity.getNumber() * entity.getPrice();
                        mTotalChecked++;
                        CheckedProductId.add(itemid);
                    } else {
                        mTotalMoney -= entity.getNumber() * entity.getPrice();
                        mTotalChecked--;
                        CheckedProductId.remove(CheckedProductId.indexOf(itemid));

                    }
                    mBtnChecking.setText("去结算（" + mTotalChecked + ")");
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
