package com.example.shoes;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.TextView;




public class MainActivity extends AppCompatActivity{
    public static final int author = 1;
    boolean login = false;
    private String user;
    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] flags = new int[]{
            R.drawable.air_jordan,
            R.drawable.air_force,
            R.drawable.air_jordan_og_bred,
            R.drawable.ultraboost,
            R.drawable.yeezy
    };
    String[] info = new String[]{
            "air_jordan",
            "air_force",
            "ait_jordan_og_bred",
            "ultraboost",
            "yeezy"
    };
    int[] popu = new int[]{
            1,1,1,1,1
    };

    private TextView tv_out;
    private ListView listView;
    private MyAdapter ma;

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode == RESULT_OK) {
            Bundle bData = data.getExtras();
            if (bData != null) {
                user = bData.getString("user");
            }
            else{
                user = "Nobody";
            }
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Intent in = new Intent(this,ChatActivity.class);
        //startActivityForResult(in,0);    // 呼叫 child activity，並要求回傳資料


        listView = (ListView) findViewById(R.id.listview);
        ma = new MyAdapter();
        listView.setAdapter(ma);

        // Each row in the list stores country name, currency and flag

        Bundle bd =this.getIntent().getExtras();
        
        user = bd != null ? bd.getString("user") : "Nobody";

        tv_out = (TextView) findViewById(R.id.textView);
        tv_out.setText("Hello " + user);
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item

        // Getting a reference to listview of main.xml layout file




        // Setting the adapter to the listView

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                ListView l = (ListView) arg0;
                String name = l.getItemAtPosition(arg2).toString();


                Intent it = new Intent(MainActivity.this,ChatActivity.class);
                Bundle bd = new Bundle();
                bd.putString("shoe_name",name);
                bd.putString("user",user);
                it.putExtras(bd);
                startActivity(it);
                finish();



            }
        });

    }


    class MyAdapter extends BaseAdapter {
         @Override
      public int getCount() {
                     // TODO Auto-generated method stub
                     return info.length;
                }
         @Override
         public Object getItem(int position) {
                     // TODO Auto-generated method stub
                     return info[position];
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
             ViewHolder vh = new ViewHolder();
             //通过下面的条件判断语句，来循环利用。如果convertView = null ，表示屏幕上没有可以被重复利用的对象。
             if(convertView==null){
                 //创建View
                 convertView = getLayoutInflater().inflate(R.layout.list_view, null);
                 vh.iv = (ImageView) convertView.findViewById(R.id.flag);
                 vh.tv = (TextView) convertView.findViewById(R.id.info);
                 vh.tv2 = (TextView) convertView.findViewById(R.id.popu);
                 convertView.setTag(vh);
             }else{
                 vh = (ViewHolder)convertView.getTag();
             }
             vh.iv.setImageResource(flags[position]);
             vh.tv.setText(info[position]);
             vh.tv2.setText("人氣：" + popu[position]);
             return convertView;
         }

}
    static class ViewHolder{
         ImageView iv; TextView tv,tv2;
    }



}