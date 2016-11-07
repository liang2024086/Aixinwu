package com.aixinwu.axw.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aixinwu.axw.R;
import com.aixinwu.axw.tools.GlobalParameterApplication;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResetPWD extends ActionBarActivity {

    private static final String TAG = "ResetPWD";

    @Bind(R.id.input_name)
    EditText _nameText;
    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.catchVerificationCode)
    Button _catchVerificationCode;
    @Bind(R.id.confirm_password)
    TextView _confirm_password;

    private int signUpStatus = -1;

    public Handler nHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 128429:
                    Toast.makeText(getApplicationContext(),"验证码10分钟有效，此手机已获取过有效验证码",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });


        _catchVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "开始获取验证码……", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        String phone_number = _nameText.getText().toString();
                        if (phone_number.length() == 11){
                            int code = catchVerficationCode(phone_number);
                            switch (code){
                                case 8:
                                    Message msg = new Message();
                                    msg.what = 128429;
                                    nHandler.sendMessage(msg);
                                    break;
                            }
                        }
                    }
                }).start();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ResetPWD.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("重置密码中...");
        progressDialog.show();


        String phoneNumber = _nameText.getText().toString();
        String password = _passwordText.getText().toString();
        String verifyCode = _emailText.getText().toString();
        // TODO: Implement your own signup logic here.

        RegisterThread registerThread = new RegisterThread(phoneNumber, verifyCode, password);

        registerThread.start();

        try{
            registerThread.join();
        }catch (Exception e){
            e.printStackTrace();
        }



        if (signUpStatus == 0){
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onSignupSuccess or onSignupFailed
                            // depending on success

                            new  AlertDialog.Builder(ResetPWD.this)
                                    .setTitle("消息")
                                    .setMessage("密码重置成功!" )
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int ii) {
                                            finish();
                                        }
                                    }).show();
                            // onSignupFailed();
                            progressDialog.dismiss();
                        }
                    }, 3000);
        }
        else{
            progressDialog.dismiss();
            Toast.makeText(getBaseContext(), "重置失败", Toast.LENGTH_LONG).show();


        }


    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        Toast.makeText(getBaseContext(), "重置成功", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "重置失败", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmPWD = _confirm_password.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("请输入手机号");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() /*|| !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()*/) {
            _emailText.setError("请输入验证码");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() ) {
            _passwordText.setError("输入密码");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (!confirmPWD.equals(password)){
            _confirm_password.setError("两次输入密码不相同");
            valid = false;
        }else{
            _confirm_password.setError(null);
        }

        return valid;
    }

    protected int catchVerficationCode(String phoneNumber){
        int code = -1;
        JSONObject phone = new JSONObject();
        phone.put("phone",phoneNumber);
        String jsonstr = phone.toJSONString();

        URL url  = null;
        try {
            url = new URL(GlobalParameterApplication.getSurl()+"/phone_verification");
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
        conn.setRequestProperty("Content-Length", String.valueOf(jsonstr.length()));
        try {
            conn.getOutputStream().write(jsonstr.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String ostr = null;
        try {
            ostr = IOUtils.toString(conn.getInputStream());
            try {
                org.json.JSONObject output = new org.json.JSONObject(ostr);
                code = output.getJSONObject("status").getInt("code");
            }catch (JSONException e){
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(ostr);

        return code;

    }

    protected int resetPWD (String username, String verifyCode, String password){
        //GlobalParameterApplication gpa = (GlobalParameterApplication) getApplicationContext();

        //String result = null;
        JSONObject matadata = new JSONObject();

        matadata.put("timestamp","12312312213");

        JSONObject userinfo = new JSONObject();
        userinfo.put("username", username);
        userinfo.put("password", password);
        userinfo.put("verification_code",verifyCode);

        String jsonstr = userinfo.toJSONString();
        System.out.println("密码重置:"+jsonstr);
        URL url  = null;
        try {
            url = new URL(GlobalParameterApplication.getSurl()+"/usr_change_psw");
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
        conn.setRequestProperty("Content-Length", String.valueOf(jsonstr.length()));
        try {
            conn.getOutputStream().write(jsonstr.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String ostr = null;
        try {
            ostr = IOUtils.toString(conn.getInputStream());
            org.json.JSONObject outjson = null;
            try{
                outjson = new org.json.JSONObject(ostr);
                signUpStatus = outjson.getJSONObject("status").getInt("code");
                System.out.println(outjson);
            }catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print("重置");
        System.out.println(ostr);

        return 1;

    }

    class RegisterThread extends Thread{

        private String phoneNumber,verifyCode,password;
        public RegisterThread(String phoneNumber,String verifyCode, String password){
            this.phoneNumber = phoneNumber;
            this.verifyCode = verifyCode;
            this.password = password;
        }

        @Override
        public void run(){
            resetPWD(phoneNumber, verifyCode, password);
        }
    }
}
