package com.aixinwu.axw.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.aixinwu.axw.R;
import com.aixinwu.axw.tools.GlobalParameterApplication;
/**
 * Created by liangyuding on 2016/4/15.
 */
public class HelloWorld extends Activity{


    @Override
    protected void onCreate(Bundle savedInstanceState){
        setContentView(R.layout.helloworld2);
        Log.i("AAAAA", "WHY");
        super.onCreate(savedInstanceState);
        //GlobalParameterApplication gpa = (GlobalParameterApplication) getApplicationContext();
        if (GlobalParameterApplication.getLogin_status() == 1) {
            setContentView(R.layout.helloworld2);
            TextView a = (TextView) findViewById(R.id.helloworld2);
            a.setText(GlobalParameterApplication.getToken());

        }
        else if (GlobalParameterApplication.getLogin_status() == 0) {
            setContentView(R.layout.helloworld);
        }
        //setContentView(R.layout.helloworld);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
