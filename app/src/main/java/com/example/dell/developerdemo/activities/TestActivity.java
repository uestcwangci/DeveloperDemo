package com.example.dell.developerdemo.activities;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.dell.developerdemo.R;
import com.example.dell.developerdemo.util.DBManager;
import com.example.dell.developerdemo.beans.WifiData;

import java.util.List;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";
    TextView tv;
    DBManager dbManager;
    SQLiteDatabase sqLiteDatabase;
    WifiData wifiData;
//    List<WifiData> mWifiList = new ArrayList<>();
    Thread thread;
    public static final int DAN_XUAN = 5555;
    public static final int DUI_CUO = 4444;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        tv = findViewById(R.id.content);






    thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DBManager dbManager = new DBManager(getApplicationContext());
                Log.e(TAG, "run: " + getPackageName());
                SQLiteDatabase sqLiteDatabase = dbManager.manage(getPackageName());
                String[] columns = new String[]{"Name", "Mac"};
//                String selection = "title like '"+"%"+mKey+"%'";
                List<WifiData> wifiDataList = dbManager.query(sqLiteDatabase, "wiwide", columns, null, null);
//                Message message = new Message();
//                message.what = DAN_XUAN;
//                message.obj = wifiDataList;
//                handler.sendMessage(message);
                for (WifiData data : wifiDataList) {
                    Log.e(TAG, "data:" +  data.toString());
                }
                sqLiteDatabase.close();
            }
        });

    }



//    @SuppressLint("HandlerLeak")
//    Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case DUI_CUO:
//                    List<DuiCuo> mDuiCuoLists = (List<DuiCuo>) msg.obj;
//                    Toast.makeText(getApplicationContext(), "共" + mDuiCuoLists.size() + "题对错题", Toast.LENGTH_SHORT).show();
//                    DuiCuoAdapter duiCuoAdapter = new DuiCuoAdapter(NextActivity.this, mDuiCuoLists);
//                    mLv.setAdapter(duiCuoAdapter);
//                    break;
//                case DAN_XUAN:
//                    List<City> mDanxuanLists = (List<City>) msg.obj;
//                    Toast.makeText(NextActivity.this, "共" + mDanxuanLists.size() + "题选择题", Toast.LENGTH_SHORT).show();
//                    XuanZheAdapter adapter = new XuanZheAdapter(NextActivity.this, mDanxuanLists);
//                    mLv.setAdapter(adapter);
//                    break;
//            }
//        }
//    };


}
