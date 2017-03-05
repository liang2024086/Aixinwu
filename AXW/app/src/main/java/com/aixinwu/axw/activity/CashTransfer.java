package com.aixinwu.axw.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aixinwu.axw.R;
import com.aixinwu.axw.tools.GlobalParameterApplication;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CashTransfer extends Activity {

    RelativeLayout submit ;
    EditText enter_jaccount;
    EditText amount;

    private Boolean validate = true;
    private String _enter_jaccount;
    private String _amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_transfer);

        enter_jaccount = (EditText)findViewById(R.id.enter_jaccount);
        amount = (EditText)findViewById(R.id.enter_coin);

        submit = (RelativeLayout)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 _enter_jaccount = enter_jaccount.getText().toString();
                 _amount = amount.getText().toString();

                validate = true;

                if (_enter_jaccount.isEmpty()){
                    enter_jaccount.setError("请输入对方jaccount账号");
                    validate = false;
                }

                if (_amount.isEmpty()){
                    amount.setError("请输入转账数目");
                    validate = false;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (validate)
                            transfer_cash(_enter_jaccount,_amount);
                        Toast.makeText(getApplicationContext(), "默认的Toast", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).start();
            }
        });
    }

    private int transfer_cash(String jaccount, String amount){
        int status = -1;
        String MyToken = GlobalParameterApplication.getToken();
        String surl = GlobalParameterApplication.getSurl();

        JSONObject cashTransfer = new JSONObject();

        cashTransfer.put("token", MyToken);
        cashTransfer.put("jaccount_id",jaccount);
        cashTransfer.put("cash",amount);

        try {
            URL url = new URL(surl + "/AixinwuCashTransfer");
            try {
                Log.i("CashTransfer", "getconnection");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                Log.i("CashTransfer", cashTransfer.toJSONString());
                conn.getOutputStream().write(cashTransfer.toJSONString().getBytes());

                java.lang.String ostr = IOUtils.toString(conn.getInputStream());
                org.json.JSONObject outjson = null;
                try{
                    outjson = new org.json.JSONObject(ostr);
                    status = outjson.getJSONObject("status").getInt("code");
                    String desc = outjson.getJSONObject("status").getString("description");
                    System.out.println(outjson);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return status;
    }
}
