package com.aixinwu.axw.tools;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aixinwu.axw.R;

/**
 * Created by liangyuding on 2016/10/6.
 */
public class MyAlertDialog extends AlertDialog {

    private Context context;
    private String comment;
    private String confirmButtonText;
    private String cancelButtonText;
    private ClickListenerInterface clickListenerInterface;
    private Handler handler;

    public interface ClickListenerInterface {

        public void doConfirm();

        public void doCancel();
    }

    public MyAlertDialog(Context context, String comment, String confirmButtonText, String cancelButtonText, Handler nhandler){
        super(context,R.style.Theme_AppCompat_Dialog);
        this.context = context;
        this.comment = comment;
        this.confirmButtonText = confirmButtonText;
        this.cancelButtonText = cancelButtonText;
        this.handler=nhandler;
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        init();
    }

    public void init(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_my_alert_dialog,null);
        setContentView(view);

        final EditText tvComment = (EditText) view.findViewById(R.id.comment);
        TextView tvConfirm = (TextView) view.findViewById(R.id.confirm);
        TextView tvCancel = (TextView) view.findViewById(R.id.cancel);

        tvComment.setText(comment);
        tvConfirm.setText(confirmButtonText);
        tvCancel.setText(cancelButtonText);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(GlobalParameterApplication.getLogin_status()==1){

                    String comment = tvComment.getText().toString();

                    if (comment.equals("")){
                        Toast.makeText(context,"请填写评论",Toast.LENGTH_SHORT).show();

                    }else{
                        Message msg=new Message();
                        msg.what=2323232;
                        msg.obj=tvComment.getText().toString();
                        handler.sendMessage(msg);

                        dismiss();
                    }
                }

            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface){
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener{

        public void  onClick(View v){

            int id = v.getId();
            switch(id){
                case R.id.confirm:
                    clickListenerInterface.doConfirm();
                    break;
                case R.id.cancel:
                    clickListenerInterface.doCancel();
                    break;
            }
        }
    }
}
