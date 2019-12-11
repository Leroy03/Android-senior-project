package com.example.shoes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.graphics.PixelFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;


public class ChatActivity extends AppCompatActivity {
    private WindowManager wm=null;
    private WindowManager.LayoutParams wmParams=null;
    private MyFloatView myFV=null;
    private static final String TAG="ChatActivity";
    private ListView listView;
    private MyAdapter ma;
    private int context_num = 0;
    private ImageButton msgbtn;
    private EditText msg;
    Intent intent;
    String user = "";
    String shoe = "";
    String context;
    String[] p = new String[1024];
    String[] c = new String[1024];
    TextView tx;
    Handler handler =  new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 1){
                String message = msg.obj.toString();
                ArrayMap<String,String> arrayMap = new ArrayMap<>(255);
                arrayMap = (ArrayMap<String, String>) msg.obj;
                Iterator<String> it = arrayMap.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    message = key + " ";
                    message += arrayMap.get(key);
                }
                //tx.setText(message);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle bd =this.getIntent().getExtras();
        if (savedInstanceState != null) {
            // 由 Bundle 取回先前儲存的資訊
            // 先前我用 putInt 儲存了整數值，這裡就用 getInt 取出整數
            // STATE_SCORE 就是先前我用的 key
            user = savedInstanceState.getString(user);
            shoe = savedInstanceState.getString(shoe);
            Log.d("asdasdasdadsa","Yes");
        }else{
            Log.e("asdasdsad","No");
        }
        user = bd != null ? bd.getString("user") : "Nobody";
        shoe = bd != null ? bd.getString("shoe_name", "") : null;

        msgbtn = (ImageButton) findViewById(R.id.msgbtn);
        msg = (EditText) findViewById(R.id.message);
        listView = (ListView) findViewById(R.id.forum);
        ma = new ChatActivity.MyAdapter();
        listView.setAdapter(ma);

        //懸浮窗
        //createView();
        //

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayMap<String,String> result = data(shoe);
                    //login()为向php服务器提交请求的函数，返回数据类型为int
                    //tx.setText(result);
                    Log.d(TAG,result.toString());
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }
                catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();

        msgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            context = msg.getText().toString();
                            upload(user, shoe, context);
                            //refresh(listView);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ma.notifyDataSetChanged();
                                }
                            });
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent it = new Intent(ChatActivity.this, MainActivity.class);
            Bundle bd = new Bundle();
            bd.putString("shoe_name",shoe);
            bd.putString("user",user);
            it.putExtras(bd);
            startActivity(it);
            finish();
            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent it = new Intent(ChatActivity.this, MainActivity.class);
        Bundle bd = new Bundle();
        bd.putString("shoe_name",shoe);
        bd.putString("user",user);
        it.putExtras(bd);
        startActivity(it);
        finish();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // 儲存目前遊戲的狀態
        // 下面例子用 putInt 指的是記錄一個整數鍵值對，實際上你還可以用 putFloat, putString, putBoolean ...等等
        // STATE_SCORE 是你自訂的字串、用作為存取資料的 key
        // mCurrentScore 就是你要儲存的值、資料類型跟 putInt 所接受的類型要相符、此例為整數
        savedInstanceState.putString(user, user);
        savedInstanceState.putString(shoe, shoe);

        // 別忘了、在最後面一定要呼叫上層類別的 onSaveInstanceState 方法，才算是真正有儲存
        super.onSaveInstanceState(savedInstanceState);
    }
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // 別忘了先呼叫上層類別 onRestoreInstanceState 方法、它才能恢復 view 的狀態
        super.onRestoreInstanceState(savedInstanceState);

        // 由 Bundle 取回先前儲存的資訊
        user = savedInstanceState.getString(user);
        shoe = savedInstanceState.getString(shoe);
    }
    private void upload(String user,String shoe,String context) throws IOException {
        String urlstr = "https://ttyl.ddns.net/old_websites/Android/upload.php";
        //建立網路連線
        URL url = new URL(urlstr);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        //往网页写入POST数据，和网页POST方法类似，参数间用‘&’连接
        String params = "user=" + user + "&" + "shoe=" + shoe + "&" + "context=" + context;
        Log.d(TAG,user + shoe + context);
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
        String returnResult = null;
        while (null != (line = bufferedReader.readLine())) {//结束会读入一个null值
            sb.append(line);//写缓冲区
        }
        String result = sb.toString();//返回结果
        //textView.setText(result);
        try {
            /*获取服务器返回的JSON数据*/
            JSONObject jsonObject = new JSONObject(result);
            returnResult = jsonObject.getString("sql");//获取JSON数据中status字段值
            //textView.setText(returnResult);
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("log_tag", "the Error parsing data " + e.toString());
        }
        Log.d("Sql",returnResult);
    }


    private ArrayMap<String, String> data(String name) throws IOException {
        String returnResult = "";


        String urlstr = "https://ttyl.ddns.net/old_websites/Android/data.php";
        //建立網路連線
        URL url = new URL(urlstr);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        //往网页写入POST数据，和网页POST方法类似，参数间用‘&’连接
        String params = "shoe=" + name;
        Log.d(TAG,name);
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
        int x = 0;
        ArrayMap<String,String> ap = new ArrayMap<>(255);
        //textView.setText(result);
        try {
            /*获取服务器返回的JSON数据*/
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("msg");
            context_num = jsonArray.length();
            Log.d(TAG,"asd" + context_num);
            for (int i = jsonArray.length() - 1;i >= 0;i--) {
                JSONObject item = jsonArray.getJSONObject(i);
                String pub = null;
                try {
                    pub = item.getString("publisher");
                }catch (Exception e){
                }
                String con = null;
                try {
                    con = item.getString("context");
                }catch (Exception e){
                }
                ap.put(pub,con);
                p[i] = pub;
                c[i] = con;
            }

        } catch (Exception e) {
            // TODO: handle exception
            Log.e("log_tag", "the Error parsing data " + e.toString());
        }
        Log.e(TAG,result);
        return ap;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id)
        {
            case android.R.id.home: // 按了 Action Bar 的返回鍵
                onBackPressed();
                return true;    // 注意! 一定要回傳 true
            case R.id.parent:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void createView(){
        myFV=new MyFloatView(getApplicationContext());
        myFV.setImageResource(R.drawable.yeezy);
        //獲取WindowManager
        wm=(WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //設置LayoutParams(全局變數）相關參數
        wmParams = ((MyApplication)getApplication()).getMywmParams();
        wmParams.type=LayoutParams.TYPE_PHONE;   //設置window type
        wmParams.format=PixelFormat.RGBA_8888;   //設置圖片格式，效果為背景透明
        //設置Window flag
        wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity=Gravity.LEFT|Gravity.TOP;   //調整懸浮視窗至左上角
        //以屏幕左上角為原點，設置x、y初始值
        wmParams.x=0;
        wmParams.y=0;
        //設置懸浮視窗長寬數據
        wmParams.width=40;
        wmParams.height=40;
        //顯示myFloatView圖像
        wm.addView(myFV, wmParams);

    }
    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return context_num;
        }
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return p[position];
        }
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            //            System.out.println("position=" + position);
            //            System.out.println(convertView);
            //            System.out.println("------------------------");
            ChatActivity.ViewHolder vh = new ChatActivity.ViewHolder();
            //通过下面的条件判断语句，来循环利用。如果convertView = null ，表示屏幕上没有可以被重复利用的对象。
            if(convertView==null){
                //创建View
                convertView = getLayoutInflater().inflate(R.layout.forum, null);
                //vh.iv = (ImageView) convertView.findViewById(R.id.flag);
                vh.tv = (TextView) convertView.findViewById(R.id.publisher);
                vh.tv2 = (TextView) convertView.findViewById(R.id.context);
                convertView.setTag(vh);
            }else{
                vh = (ChatActivity.ViewHolder)convertView.getTag();
            }
            //vh.iv.setImageResource(flags[position]);
            vh.tv.setText(p[position]);
            vh.tv2.setText("評論：" + c[position]);
            return convertView;
        }

    }
    static class ViewHolder{
        ImageView iv; TextView tv,tv2;
    }
}
