package com.example.shoes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Regist extends AppCompatActivity {

    EditText acc,pwd;
    RadioButton rbtn,btnm,btnf,btnn;
    RadioGroup btngp;
    NumberPicker mynp;
    Button btn;
    String account;
    String password;
    int age = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        btngp = (RadioGroup) findViewById(R.id.radioGroup);
        acc = (EditText) findViewById(R.id.acc);
        pwd = (EditText) findViewById(R.id.pwd);
        btn = (Button) findViewById(R.id.rgt);
        btnm = (RadioButton) findViewById(R.id.rbtnmale);
        btnf = (RadioButton) findViewById(R.id.rbtnfemale);
        btnn = (RadioButton) findViewById(R.id.rbtnnone);

        mynp = (NumberPicker)findViewById(R.id.np);
        mynp.setMinValue(0); //設定最小值
        mynp.setMaxValue(120); //設定最大值
        mynp.setValue(18); //設定現值
        age = mynp.getValue(); //取得現值
        mynp.setOnValueChangedListener(numPickerOnValueChange); //設定數字變化監聽事件

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = acc.getText().toString();
                password = pwd.getText().toString();

                int selected = btngp.getCheckedRadioButtonId();
                rbtn = (RadioButton) findViewById(selected);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int status = register();
                            if (status == 1) {
                                Intent it = new Intent(Regist.this, MainActivity.class);
                                it.putExtra("name",account);
                                startActivity(it);
                                finish();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }

        });
    }

    private NumberPicker.OnValueChangeListener numPickerOnValueChange
            = new NumberPicker.OnValueChangeListener(){
        @Override
        public void onValueChange(NumberPicker view, int oldValue, int newValue)
        {
            //do something...
        }
    };
    private int register() throws IOException {
        int returnResult = 0;
        String urlstr = "https://ttyl.ddns.net/old_websites/Android/Regist.php";
        //建立網路連線
        URL url = new URL(urlstr);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        //往网页写入POST数据，和网页POST方法类似，参数间用‘&’连接
        String params = "uid=" + account + '&' + "pwd=" + password+ '&' + "gender=" + rbtn.getText() + '&' + "age=" + age;
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
        String sql = "";
        //textView.setText(result);
        try {
            /*获取服务器返回的JSON数据*/
            JSONObject jsonObject = new JSONObject(result);
            returnResult = jsonObject.getInt("status");//获取JSON数据中status字段值
            sql = jsonObject.getString("sql");
            //textView.setText(returnResult);
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("log_tag", "the Error parsing data " + e.toString());
        }

        Log.d("sql","" + sql);
        Log.d("tag","" +returnResult);
        return returnResult;

    }
}
