package com.example.shoes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class LoginActivity extends AppCompatActivity {

    Button button,register;
    EditText name,pwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button = findViewById(R.id.log); // 宣告按鈕
        register = findViewById(R.id.register);
        name = findViewById(R.id.name);
        pwd = findViewById(R.id.pwd);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this,Regist.class);
                startActivity(it);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//登陆按钮监听事件
                /*((App)getApplicationContext()).setTextData(et.getText().toString());
                location_x.setText(((App)getApplicationContext()).getTextData());*/
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int result = login();
                            //login()为向php服务器提交请求的函数，返回数据类型为int
                            if (result == 1) {
                                //textView.setText("登入成功");
                                Log.e("log_tag", "登錄成功！");
                                //Toast toast=null;
                                Looper.prepare();
                                Toast.makeText(LoginActivity.this, "登入成功！", Toast.LENGTH_SHORT).show();
                                getIntent().putExtra("result","1");
                                setResult(RESULT_OK,getIntent());
                                Intent it = new Intent(LoginActivity.this,MainActivity.class);
                                Bundle bd = new Bundle();
                                String na = name.getText().toString();
                                Log.d("user",na);
                                bd.putString("user",na);
                                it.putExtras(bd);
                                startActivity(it);
                                finish();
                                Looper.loop();
                            } else if (result == -2) {
                                //textView.setText("密碼錯誤");
                                Log.e("log_tag", "密碼錯誤！");
                                //Toast toast=null;
                                Looper.prepare();
                                Toast.makeText(LoginActivity.this, "密碼錯誤！", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            } else if (result == -1) {
                                //textView.setText("不存在該用戶");
                                Log.e("log_tag", "不存在該用户！");
                                //Toast toast=null;
                                Looper.prepare();
                                Toast.makeText(LoginActivity.this, "不存在該用戶！", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }).start();
            }
        });
    }


    private int login() throws IOException {
        int returnResult = 0;
        /*獲取姓名跟密碼*/
        String user_id = name.getText().toString();
        String input_pwd = pwd.getText().toString();
        if (user_id.equals("") || user_id.length() <= 0) {
            Looper.prepare();
            Toast.makeText(LoginActivity.this, "請輸入帳號", Toast.LENGTH_LONG).show();
            Looper.loop();
            return 0;

        }
        if (input_pwd.equals("") || input_pwd.length() <= 0) {
            Looper.prepare();
            Toast.makeText(LoginActivity.this, "請輸入密碼", Toast.LENGTH_LONG).show();
            Looper.loop();
            return 0;
        }
        String urlstr = "https://ttyl.ddns.net/old_websites/Android/login.php";
        //建立網路連線
        URL url = new URL(urlstr);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        //往网页写入POST数据，和网页POST方法类似，参数间用‘&’连接
        String params = "uid=" + user_id + '&' + "pwd=" + input_pwd;
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        OutputStream out = http.getOutputStream();
        out.write(params.getBytes());//post提交参数
        out.flush();
        out.close();

        //读取网页返回的数据
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream()));//获得输入流
        String line = "";
        StringBuilder sb = new StringBuilder();//建立输入缓冲区
        while (null != (line = bufferedReader.readLine())) {//结束会读入一个null值
            sb.append(line);//写缓冲区
        }
        String result = sb.toString();//返回结果
        //textView.setText(result);
        try {
            /*获取服务器返回的JSON数据*/
            JSONObject jsonObject = new JSONObject(result);
            returnResult = jsonObject.getInt("status");//获取JSON数据中status字段值
            //textView.setText(returnResult);
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("log_tag", "the Error parsing data " + e.toString());
        }

        return returnResult;
    }
}


