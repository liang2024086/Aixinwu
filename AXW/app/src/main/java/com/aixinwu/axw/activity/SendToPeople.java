package com.aixinwu.axw.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aixinwu.axw.R;
import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.aixinwu.axw.tools.Tool;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SendToPeople extends Activity {
    public String imageSet;
    private GridView mGridView;
    private TextView buttonPublish;
    private EditText doc;
    private String Descrip;    private ArrayList<HashMap<String, Object>> imageItem;
    private Bitmap bmp;
    public SimpleAdapter simpleAdapeter;
    private final int IMAGE_OPEN = 1;      //打开图片标记
    private final int GET_DATA = 2;           //获取处理后图片标记
    private final int TAKE_PHOTO = 3;       //拍照标记
    private String pathTakePhoto;
    private Uri imageUri;
    private String TypeName;
    private EditText Caption;
    private String _caption;
    String pathImage;
    private Tool am = new Tool();
    private Spinner spinner1;

    private Spinner spinner2;

    private int HowNew = 1;

    private EditText price;
    private double money;

    private ArrayAdapter<String> type;
    private ArrayAdapter<String> neworold;
    private final String surl = GlobalParameterApplication.getSurl();
    public  java.lang.String MyToken;
    private long NumPhoto = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.content_send_to_people);
        mGridView = (GridView)findViewById(R.id.gridView1);
        buttonPublish = (TextView)findViewById(R.id.button1);
        doc = (EditText)findViewById(R.id.editText1);
        spinner1 = (Spinner)findViewById(R.id.type);
        spinner2 = (Spinner)findViewById(R.id.neworold);
        price = (EditText)findViewById(R.id.price);
        Caption = (EditText)findViewById(R.id.commodity_title);
        String[] mType=getResources().getStringArray(R.array.spinner1name);
        String[] mNew=getResources().getStringArray(R.array.spinner2name);
        type = new ArrayAdapter<String>(this,R.layout.spinner_textview,mType);
        neworold = new ArrayAdapter<String>(this,R.layout.spinner_textview,mNew);
        spinner1.setAdapter(type);
        spinner2.setAdapter(neworold);

        buttonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalParameterApplication gpa = (GlobalParameterApplication) getApplicationContext();
                if(gpa.getLogin_status()==0)Toast.makeText(SendToPeople.this,"未登录",Toast.LENGTH_LONG).show();
                else{
                    _caption = Caption.getText().toString();
                    Descrip = doc.getText().toString();
                    //TextView v = (TextView)spinner1.getSelectedView().findViewById(R.id.item);
                    //TypeName = v.getText().toString();
                    //v = (TextView)spinner2.getSelectedView().findViewById(R.id.item);
                    //HowNew = gpa.getNewOldInt(v.getText().toString());//Integer.parseInt(v.getText().toString());

                    TypeName = "电子产品";
                    HowNew = 1;
                    String now_price = price.getText().toString();
                    if (!now_price.toString().isEmpty()) money = Double.parseDouble(now_price.toString());
                    if (imageItem.size() == 1) {
                        Toast.makeText(SendToPeople.this, "No Picture", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (Descrip.isEmpty()){
                        Toast.makeText(SendToPeople.this, "No Discription", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (price.getText().toString().isEmpty()){
                        Toast.makeText(SendToPeople.this, "No Money", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(getApplicationContext(), money+"",
                            Toast.LENGTH_SHORT).show();
                    // String itemID = AddItem(TypeName,money,Descrip,YesorNo);

                    final ProgressDialog progressDialog = new ProgressDialog(SendToPeople.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("发布中...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    new Thread(runnable).start();
                }

//                Toast.makeText(getActivity(), "Upload Successful", Toast.LENGTH_SHORT).show();
                /*
                AddImage 部分 将itemID和imageID绑定上传
                 */
            }
        });
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.gridview_addpic);
        imageItem = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", bmp);
        map.put("pathImage", "add_pic");
        imageItem.add(map);
        simpleAdapeter = new SimpleAdapter(SendToPeople.this, imageItem, R.layout.griditem_addpic,
                new String[] {"itemImage"}, new int[]{R.id.imageView1});
        simpleAdapeter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if (view instanceof ImageView && o instanceof Bitmap) {
                    ImageView i = (ImageView) view;
                    i.setImageBitmap((Bitmap) o);
                    return true;
                }
                return false;
            }

        });
        mGridView.setAdapter(simpleAdapeter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (imageItem.size() == 8) {
                    Toast.makeText(SendToPeople.this, "Picture is enough", Toast.LENGTH_SHORT).show();
                }
                else if (position == 0){
                    //   Toast.makeText(getActivity(), "Add", Toast.LENGTH_SHORT).show();
                    AddImageDialog();
                }
                else {
                    //        Toast.makeText(getActivity(), "Del", Toast.LENGTH_SHORT).show();//
                    //DeleteDialog(position);
                }
            }
        });
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if ( i > 0){
                    DeleteDialog(i);
                    ((SimpleAdapter)mGridView.getAdapter()).notifyDataSetChanged();
                }

                return false;
            }
        });
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            /*try {

                MyToken = am.getToken(surl,"name","pwd");
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            imageSet = uploadPic(imageItem);
            MyToken=GlobalParameterApplication.getToken();


            String ss= AddItem(HowNew,money,Descrip,imageSet,_caption);

            Message msg = new Message();
            msg.what = 133;
            nHandler.sendMessage(msg);

           // Log.i("UPLOAD",ss);
        }
    };
    public Handler nHandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 133:
                    new  AlertDialog.Builder(SendToPeople.this)
                            .setTitle("消息" )
                            .setMessage("交易商品发布成功！" )
                            .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int ii) {
                                    finish();
                                }
                            }).setCancelable(false).show();
                    break;
                case 134:
                    break;
            }
        }
    };
    @Override
    public void onResume(){
        super.onResume();
        //获取传递的处理图片在onResume中显示
        //Intent intent = getIntent();
        //pathImage = intent.getStringExtra("pathProcess");
        //适配器动态显示图片
        if(!TextUtils.isEmpty(pathImage)){
            Toast.makeText(SendToPeople.this,pathImage,Toast.LENGTH_LONG).show();
            BitmapFactory.Options cc = new BitmapFactory.Options();
            cc.inSampleSize=8;

            Bitmap addbmp=BitmapFactory.decodeFile(pathImage,cc);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", addbmp);
            map.put("pathImage", pathImage);
            imageItem.add(map);
            /*SimpleAdapter simpleAdapter = new SimpleAdapter(this,

                    imageItem, R.layout.griditem_addpic,
                    new String[] { "itemImage"}, new int[] { R.id.imageView1});

            //接口载入图片
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    // TODO Auto-generated method stub
                    if(view instanceof ImageView && data instanceof Bitmap){
                        ImageView i = (ImageView)view;
                        i.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            mGridView.setAdapter(simpleAdapter);*/

            SimpleAdapter simpleAdapter = (SimpleAdapter) mGridView.getAdapter();
            simpleAdapter.notifyDataSetChanged();
            //刷新后释放防止手机休眠后自动添加
            pathImage = null;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //打开图片
        if(resultCode== Activity.RESULT_OK && requestCode==IMAGE_OPEN) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                Cursor cursor = getContentResolver().query(
                        uri,
                        new String[] { MediaStore.Images.Media.DATA },
                        null,
                        null,
                        null);
                //返回 没找到选择图片
                if (null == cursor) {
                    Log.i("FIND PIC","can't find pic");
                    return;
                }
                //光标移动至开头 获取图片路径
                cursor.moveToFirst();
                pathImage = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
                //向处理活动传递数据
                //Toast.makeText(this, path, Toast.LENGTH_SHORT).show();

            } else {
                pathImage = uri.getPath();

            }
        }  //end if 打开图片
        //获取图片

        //拍照
        if(resultCode==Activity.RESULT_OK && requestCode==TAKE_PHOTO) {
            Intent intent = new Intent("com.android.camera.action.CROP"); //剪裁
            intent.setDataAndType(imageUri, "image/*");
            intent.putExtra("scale", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            //广播刷新相册
            Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intentBc.setData(imageUri);
            sendBroadcast(intentBc);
            //向处理活动传递数据
            pathImage = pathTakePhoto;
        }
    }

    protected void DeleteDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定删除这张图片?");
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                imageItem.remove(position);
                simpleAdapeter.notifyDataSetChanged();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1122: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE_OPEN);
                    Log.i("DEBUG_TAG", "user granted the permission!");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i("DEBUG_TAG", "user denied the permission!");
                }
                return;
            }

            case 3344: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    File outputImage = new File(Environment.getExternalStorageDirectory(), "suishoupai_image"+String.valueOf(NumPhoto)+".jpg");
                    NumPhoto++;
                    if (NumPhoto >= 10) NumPhoto = 0;
                    pathTakePhoto = outputImage.toString();
                    try {
                        if(outputImage.exists()) {
                            outputImage.delete();
                        }
                        outputImage.createNewFile();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                    imageUri = Uri.fromFile(outputImage);
                    Intent intentPhoto = new Intent("android.media.action.IMAGE_CAPTURE"); //拍照
                    intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    try {
                        startActivityForResult(intentPhoto, TAKE_PHOTO);
                    }catch(Throwable e){
                        e.printStackTrace();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i("DEBUG_TAG", "user denied the permission!");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

       protected  void AddImageDialog(){
           AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setTitle("Add Picture");
           builder.setIcon(R.drawable.ic_launcher);
           builder.setCancelable(false); //不响应back按钮
           builder.setItems(new String[] {"本地相册选择","手机相机添加","取消选择图片"},
                   new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           // TODO Auto-generated method stub
                           switch(which) {
                               case 0: //本地相册
                                   dialog.dismiss();
                                   if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(SendToPeople.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                       //has permission, do operation directly

                                       Intent intent = new Intent(Intent.ACTION_PICK,
                                               android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                       startActivityForResult(intent, IMAGE_OPEN);
                                   } else {
                                       //do not have permission
                                       Log.i("DEBUG_TAG", "user do not have this permission!");

                                       // Should we show an explanation?
                                       if (ActivityCompat.shouldShowRequestPermissionRationale(SendToPeople.this,
                                               Manifest.permission.READ_EXTERNAL_STORAGE)) {

                                           // Show an explanation to the user *asynchronously* -- don't block
                                           // this thread waiting for the user's response! After the user
                                           // sees the explanation, try again to request the permission.
                                           Log.i("DEBUG_TAG", "we should explain why we need this permission!");
                                       } else {

                                           // No explanation needed, we can request the permission.
                                           Log.i("DEBUG_TAG", "==request the permission==");

                                           try {
                                               ActivityCompat.requestPermissions(SendToPeople.this,
                                                       new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                       1122);
                                           }catch (Throwable e){
                                               e.printStackTrace();
                                           }

                                           // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                           // app-defined int constant. The callback method gets the
                                           // result of the request.
                                       }
                                   }

                                   //通过onResume()刷新数据
                                   break;
                               case 1: //手机相机
                                   dialog.dismiss();
                                   if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(SendToPeople.this, Manifest.permission.CAMERA)) {
                                       //has permission, do operation directly

                                       File outputImage = new File(Environment.getExternalStorageDirectory(), "suishoupai_image"+String.valueOf(NumPhoto)+".jpg");
                                       NumPhoto++;
                                       if (NumPhoto >= 10) NumPhoto = 0;
                                       pathTakePhoto = outputImage.toString();
                                       try {
                                           if(outputImage.exists()) {
                                               outputImage.delete();
                                           }
                                           outputImage.createNewFile();
                                       } catch(Exception e) {
                                           e.printStackTrace();
                                       }
                                       imageUri = Uri.fromFile(outputImage);
                                       Intent intentPhoto = new Intent("android.media.action.IMAGE_CAPTURE"); //拍照
                                       intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                       try {
                                           startActivityForResult(intentPhoto, TAKE_PHOTO);
                                       }catch(Throwable e){
                                           e.printStackTrace();
                                       }
                                   } else {
                                       //do not have permission
                                       Log.i("DEBUG_TAG", "user do not have this permission!");

                                       // Should we show an explanation?
                                       if (ActivityCompat.shouldShowRequestPermissionRationale(SendToPeople.this,
                                               Manifest.permission.CAMERA)) {

                                           // Show an explanation to the user *asynchronously* -- don't block
                                           // this thread waiting for the user's response! After the user
                                           // sees the explanation, try again to request the permission.
                                           Log.i("DEBUG_TAG", "we should explain why we need this permission!");
                                       } else {

                                           // No explanation needed, we can request the permission.
                                           Log.i("DEBUG_TAG", "==request the permission==");

                                           try {
                                               ActivityCompat.requestPermissions(SendToPeople.this,
                                                       new String[]{Manifest.permission.CAMERA},
                                                       3344);
                                           }catch (Throwable e){
                                               e.printStackTrace();
                                           }

                                           // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                           // app-defined int constant. The callback method gets the
                                           // result of the request.
                                       }
                                   }
                                   break;
                               case 2: //取消添加
                                   dialog.dismiss();
                                   break;
                               default:
                                   break;
                           }
                       }
                   });
           //显示对话框
           builder.create().show();

       }
    protected String AddItem(int Type, double Money, String Doc, String picstr, String caption){
        String result = null;
        JSONObject matadata = new JSONObject();

        matadata.put("timestamp","12312312213");
        JSONObject iteminfo = new JSONObject();
        iteminfo.put("category",1);
        iteminfo.put("itemCondition",HowNew);
        iteminfo.put("estimatedPriceByUser",Money);
        iteminfo.put("description",Doc);
        iteminfo.put("images",picstr);
        iteminfo.put("caption",caption);
        //iteminfo.put("Check",flag);
        JSONObject data = new JSONObject();
        //data.put("mataData",matadata);
        data.put("token",MyToken);
        data.put("itemInfo",iteminfo);
        String jsonstr = data.toJSONString();
        URL url  = null;
        try {
            url = new URL(surl + "/item_add");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setDoOutput(true);
        conn.setConnectTimeout(1000);
        conn.setReadTimeout(1000);
        conn.setRequestProperty("Content-Type","application/json");
        //conn.setRequestProperty("Content-Length", String.valueOf(jsonstr.length()));
        try {
            conn.getOutputStream().write(jsonstr.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String ostr = null;
        try {
            ostr = IOUtils.toString(conn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(ostr);

        org.json.JSONObject outjson = null;

        try{
            outjson = new org.json.JSONObject(ostr);
            result = outjson.getJSONObject("itemInfo").getString("ID");
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }
    protected  String uploadPic(ArrayList<HashMap<String,Object>> adapter){
        String Picset = "";
        for (int i = 1; i < imageItem.size(); i++){
            String ss=(String)imageItem.get(i).get("pathImage");
            //   Toast.makeText(getActivity(),"Begin transication",Toast.LENGTH_SHORT).show();

            try {
                String imageID = am.sendFile(surl,ss);

                if (i>1) Picset=Picset+","+imageID;
                else Picset = imageID;
                //      Toast.makeText(getActivity(),Picset.size(),Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Picset;

    }

}
