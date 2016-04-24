package com.aixinwu.axw.fragment;

import android.app.Activity;
import android.os.Handler;
import android.app.AlertDialog.Builder;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.EventLogTags;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import com.aixinwu.axw.R;
import com.aixinwu.axw.activity.MainActivity;
import com.aixinwu.axw.tools.GlobalParameterApplication;
import com.aixinwu.axw.tools.Tool;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
//import org.json.JSONObject;
import org.json.simple.JSONObject;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


/**
 * Created by liangyuding on 2016/4/6.
 */
public class SubmitThings extends Fragment {

    public String imageSet;
    private GridView mGridView;
    private Button buttonPublish;
    private EditText doc;
    private String Descrip;    private ArrayList<HashMap<String, Object>> imageItem;
    private Bitmap bmp;
    public SimpleAdapter simpleAdapeter;
    private final int IMAGE_OPEN = 1;      //打开图片标记
    private final int GET_DATA = 2;           //获取处理后图片标记
    private final int TAKE_PHOTO = 3;       //拍照标记
    private String pathTakePhoto;
    private Uri imageUri;
    String pathImage;
    private Tool am = new Tool();
    private Spinner spinner1;
    private String TypeName;
    private Spinner spinner2;
    private Handler handler;
    private int HowNew;
    private CheckBox checkBox;
    private boolean YesorNo;
    private EditText price;
    private int money;
    private EditText itemname;
    private String ItemName;
    private EditText itemnumber;
    private int itemnum;
    private EditText jaccount;
    private String JaccountID;
    private ArrayAdapter<String> type;
    private ArrayAdapter<String> neworold;
    private final String surl = GlobalParameterApplication.getSurl();
    public  java.lang.String MyToken;
    private long NumPhoto = 0;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        handler=new Handler();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //打开图片
        if(resultCode== Activity.RESULT_OK && requestCode==IMAGE_OPEN) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                //查询选择图片
                Cursor cursor = getActivity().getContentResolver().query(
                        uri,
                        new String[] { MediaStore.Images.Media.DATA },
                        null,
                        null,
                        null);
                //返回 没找到选择图片
                if (null == cursor) {
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
            getActivity().sendBroadcast(intentBc);
            //向处理活动传递数据
            pathImage = pathTakePhoto;
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        //获取传递的处理图片在onResume中显示
        //Intent intent = getIntent();
        //pathImage = intent.getStringExtra("pathProcess");
        //适配器动态显示图片
        if(!TextUtils.isEmpty(pathImage)){
            Toast.makeText(getActivity(),pathImage,Toast.LENGTH_LONG).show();
            BitmapFactory.Options cc = new BitmapFactory.Options();
            cc.inSampleSize=8;

            Bitmap addbmp=BitmapFactory.decodeFile(pathImage,cc);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", addbmp);
            map.put("pathImage", pathImage);
            imageItem.add(map);
            SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),

                    imageItem, R.layout.griditem_addpic,
                    new String[] { "itemImage"}, new int[] { R.id.imageView1});
            //接口载入图片
            simpleAdapter.setViewBinder(new ViewBinder() {
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
            mGridView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
            //刷新后释放防止手机休眠后自动添加
            pathImage = null;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.submit_things,null);


        mGridView = (GridView)view.findViewById(R.id.gridView1);
        buttonPublish = (Button)view.findViewById(R.id.button1);
        doc = (EditText)view.findViewById(R.id.editText1);
        spinner1 = (Spinner)view.findViewById(R.id.type);
        spinner2 = (Spinner)view.findViewById(R.id.neworold);
        checkBox = (CheckBox)view.findViewById(R.id.checkforaxw);
        price = (EditText)view.findViewById(R.id.price);
        itemname=(EditText)view.findViewById(R.id.itemname);
        itemnumber=(EditText)view.findViewById(R.id.itemnumber);
        jaccount=(EditText)view.findViewById(R.id.jaccountID);
        String[] mType=getResources().getStringArray(R.array.spinner1name);
        String[] mNew=getResources().getStringArray(R.array.spinner2name);
        type = new ArrayAdapter<String>(getActivity(),R.layout.spinner_textview,mType);
        neworold = new ArrayAdapter<String>(getActivity(),R.layout.spinner_textview,mNew);
        spinner1.setAdapter(type);
        spinner2.setAdapter(neworold);

        buttonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalParameterApplication gpa = (GlobalParameterApplication) getActivity().getApplicationContext();
                if(gpa.getLogin_status()==0)Toast.makeText(getActivity(),"你跪了",Toast.LENGTH_LONG).show();
                else{
                Descrip = doc.getText().toString();
                TextView v = (TextView)spinner1.getSelectedView().findViewById(R.id.item);
                TypeName = v.getText().toString();
                v = (TextView)spinner2.getSelectedView().findViewById(R.id.item);
                HowNew = Integer.parseInt(v.getText().toString());
                YesorNo = checkBox.isChecked();
                ItemName = itemname.getText().toString();
                if (!itemnumber.getText().toString().isEmpty())
                    itemnum = Integer.parseInt(itemnumber.getText().toString());
                JaccountID = jaccount.getText().toString();
                if (!price.getText().toString().isEmpty()) money = Integer.parseInt(price.getText().toString());
                if (imageItem.size() == 1 && !YesorNo) {
                    Toast.makeText(getActivity(), "No Picture", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Descrip.isEmpty()&& !YesorNo){
                    Toast.makeText(getActivity(), "No Discription", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (price.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "No Money", Toast.LENGTH_SHORT).show();
                    return;
                }
                Descrip = Descrip + "  " + HowNew;
                ItemName = ItemName+"*"+itemnum;
               // String itemID = AddItem(TypeName,money,Descrip,YesorNo);
                if (!YesorNo)
                    new Thread(runnable).start();
                else new Thread(runnable1).start();}

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
            simpleAdapeter = new SimpleAdapter(getActivity(), imageItem, R.layout.griditem_addpic,
                    new String[] {"itemImage"}, new int[]{R.id.imageView1});
            simpleAdapeter.setViewBinder(new ViewBinder(){
                @Override
                public boolean setViewValue(View view, Object o, String s) {
                    if (view instanceof ImageView && o instanceof Bitmap){
                        ImageView i = (ImageView)view;
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
                    if (imageItem.size() == 10) {
                        Toast.makeText(getActivity(), "Picture is enough", Toast.LENGTH_SHORT).show();
                    }
                    else if (position == 0){
                     //   Toast.makeText(getActivity(), "Add", Toast.LENGTH_SHORT).show();
                        AddImageDialog();
                    }
                    else {
                //        Toast.makeText(getActivity(), "Del", Toast.LENGTH_SHORT).show();//DeleteDialog(position);
                    }
                }
            });


        return view;
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
            String ss= AddItem(HowNew,money,Descrip,imageSet);
            Log.i("UPLOAD",ss);
        }
    };
    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {

            MyToken=GlobalParameterApplication.getToken();
            JSONObject data = new JSONObject();
            JSONObject iteminfo = new JSONObject();
            iteminfo.put("jacount_id",JaccountID);
            iteminfo.put("desc",ItemName);
            data.put("itemInfo",iteminfo);
            data.put("token",MyToken);
            try {
                URL url = new URL(surl + "/item_add_aixinwu");
                try {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.getOutputStream().write(data.toJSONString().getBytes());
                    java.lang.String oss = IOUtils.toString(conn.getInputStream());
                    System.out.println(oss);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


        }
    };
    /*
    protected void DeleteDialog(final int position){
        AlertDialog.Builder builder = new Builder(getActivity());
        builder.setMessage("Can I delete the image?");
        builder.setTitle("Watch out");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                imageItem.remove(position);
                simpleAdapeter.notifyDataSetChanged();

            }
        });
        builder.setNegativeButton("No", new DialogIn1terface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }*/
    protected  void AddImageDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, IMAGE_OPEN);
                                //通过onResume()刷新数据
                                break;
                            case 1: //手机相机
                                dialog.dismiss();
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
                                startActivityForResult(intentPhoto, TAKE_PHOTO);
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
    protected String AddItem(int Type, int Money, String Doc, String picstr){
        String result = null;
        JSONObject matadata = new JSONObject();

        matadata.put("timestamp","12312312213");
        JSONObject iteminfo = new JSONObject();
        iteminfo.put("status",Type);
        iteminfo.put("price",Money);
        iteminfo.put("description",Doc);
        iteminfo.put("images",picstr);
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
