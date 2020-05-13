package com.example.yg191.registerdemo.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yg191.registerdemo.R;
import com.example.yg191.registerdemo.bean.UserBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEt;
    private EditText passwordEt;
    private TextView registerTv;
    private Button loginBtn;
    private TextView test;
    private TextView test1;

    final OkHttpClient client = new OkHttpClient();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                String returnMessage = (String) msg.obj;
                Log.i("获取的返回信息", returnMessage);
                UserBean userBean = new Gson().fromJson(returnMessage, UserBean.class);
                String aa = userBean.getMsg();
                Log.i("MSGhahaha", aa);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        test = findViewById(R.id.test);
        test1 = findViewById(R.id.test1);

        String result = getStrFromRaw("mystr");
        test.setText(result);
        Gson gson = new Gson();
        List<UserBean> list = gson.fromJson(result, new TypeToken<List<UserBean>>(){}.getType());
        StringBuffer stringBuffer = new StringBuffer("");
        for(UserBean userBean: list){
            stringBuffer.append(userBean.toString());
            stringBuffer.append("\n");
        }
        test1.setText(stringBuffer);
//        List<UserBean> list = new Gson().fromJson(result, new TypeToken<List<UserBean>>(){}.getType());
//        test1.setText(list.toString());
    }

    private void initView() {
        usernameEt = findViewById(R.id.et_username);
        passwordEt = findViewById(R.id.et_password);
        registerTv = findViewById(R.id.register);
        loginBtn = findViewById(R.id.login_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEt.getText().toString();
                String password = passwordEt.getText().toString();
                postRequest(username, password);
            }
        });
    }

    private void postRequest(String username, String password){
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        final Request request = new Request.Builder()
                .url("")
                .post(formBody)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if(response.isSuccessful()){
                        mHandler.obtainMessage(1, response.body().string()).sendToTarget();
                    }else{
                        throw new IOException("Unexpected code:" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String getStrFromRaw(String fileName){
        InputStream in = getResources().openRawResource(R.raw.mystr);
        try {
            InputStreamReader reader = new InputStreamReader(in, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuffer buffer = new StringBuffer("");
            String line;
            while((line = bufferedReader.readLine()) != null){
                buffer.append(line);
                buffer.append("\n");
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
