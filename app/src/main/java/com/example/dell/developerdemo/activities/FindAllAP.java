package com.example.dell.developerdemo.activities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.dell.developerdemo.R;
import com.example.dell.developerdemo.beans.AP;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FindAllAP extends AppCompatActivity{
    private static final int MSG_SCAN = 449;
    private static final int SCAN1000 = 587;
    private TextView textView;
    private EditText area,areaNum;
    private Set<AP> APSet = new HashSet<>();
    private SQLiteDatabase db;
    private Thread KeepRunning,scan_1000,RunOneTime;
    private boolean isRun;
    private static final String TAG = "FindAllAP";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_all_ap);
        textView = findViewById(R.id.textview);
        area = findViewById(R.id.area);
        areaNum = findViewById(R.id.area_num);
        initButton();
        Log.e("db", Environment.getExternalStorageDirectory().getPath());
        db = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory()+ "/TotalAP.db",null);
        createTable(db);
        initThread();
//        obtainWifiInfo();
//        insertDB(db);
//        textView.setText(scanBuilder.toString());
//        APSet.clear();
//        KeepRunning.start();
        
    }

    private void initThread() {
//        isRun = false;
//        KeepRunning = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (isRun) {
//                    obtainWifiInfo();
//                    Message message = new Message();
//                    message.what = MSG_SCAN;
//                    handler.sendMessage(message);
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
        RunOneTime = new Thread(new Runnable()
        {
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        showWifiInfo();
                    }
                });

            }
        });
    }

    private void initButton() {
            ButtonListener buttonListener = new ButtonListener();
            Button sample = findViewById(R.id.sample);
            Button initBt = findViewById(R.id.clear);
            Button insertBt = findViewById(R.id.insert_db);
            ToggleButton toggleBt = findViewById(R.id.toggleSample);
            sample.setOnClickListener(buttonListener);
            initBt.setOnClickListener(buttonListener);
            insertBt.setOnClickListener(buttonListener);
            toggleBt.setOnCheckedChangeListener(buttonListener);
    }



    class ButtonListener implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.sample:
                    RunOneTime.run();
                    break;
                case R.id.clear:
                    APSet.clear();
                    isRun = false;
                    textView.setText("初始化成功");
                    break;
                case R.id.insert_db:
                    insertDB(db);
                    textView.setText("写入数据库成功");
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                startScan();
            } else {
                textView.setText("暂停采集");
                stopScan();
            }
        }
    }

    private void showWifiInfo() {
        StringBuilder scanBuilder = new StringBuilder();
        WifiManager wifiManager;
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
        List<ScanResult> scanResults = wifiManager.getScanResults();//搜索到的设备列表
        for (ScanResult scanResult : scanResults) {
            AP ap = new AP();
            ap.setMAC(scanResult.BSSID);
            ap.setName(scanResult.SSID);
            scanBuilder.append(ap.toString()).append('\n');
        }
        scanResults.clear();
        textView.setText(scanBuilder.toString());
    }

    private void obtainWifiInfo() {
        WifiManager wifiManager;
        wifiManager= (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
        List<ScanResult> scanResults=wifiManager.getScanResults();//搜索到的设备列表
        for (ScanResult scanResult : scanResults) {
            AP ap = new AP();
            ap.setMAC(scanResult.BSSID);
            ap.setName(scanResult.SSID);
            APSet.add(ap);
        }
        scanResults.clear();
        Log.e("size", String.valueOf(APSet.size()));
    }

    private void startScan(){
        isRun = true;
        KeepRunning = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRun) {
                    obtainWifiInfo();
                    Message message = new Message();
                    message.what = MSG_SCAN;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        KeepRunning.start();
    }
    private void stopScan(){
        isRun = false;
//        APSet.clear();
    }
    private void startScan_1000(){
        final int[] k = {1000};
        APSet.clear();
        scan_1000 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (k[0] > 0) {
                    APSet.clear();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    k[0]--;
                    obtainWifiInfo();
                    insertDB(db);
                }
                Message message = new Message();
                message.what = SCAN1000;
                handler.sendMessage(message);
            }
        });
        scan_1000.start();
    }



    private void delete(){
        String sql = "SELECT COUNT(*) FROM stu_table";
        SQLiteStatement statement = db.compileStatement(sql);
        long countDB = statement.simpleQueryForLong();
        db.delete("stu_table","RecNo=?", new String[]{String.valueOf(countDB)});
    }


    private void createTable(SQLiteDatabase db){
        //创建表SQL语句
        String ap_table = "create table if not exists ap_table (Area TEXT," +
                "AreaNum TEXT," +
                "Name TEXT," +
                "Mac VACCHAR(17)," +
                "Date VARCHAR(10));";
        //执行SQL语句
        db.execSQL(ap_table);
    }
    private void insertDB(SQLiteDatabase db){
        //获取日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int APSize = APSet.size();

//        实例化常量值
        ContentValues cValue = new ContentValues();
//        添加路由信息
        for (AP ap : APSet) {
            //添加Location
            if (area.getText().toString().isEmpty() || areaNum.getText().toString().isEmpty()) {
                cValue.put("Area", "未知区域");
                cValue.put("AreaNum", "编号未知");
            } else {
                cValue.put("Area", area.getText().toString());
                cValue.put("AreaNum", areaNum.getText().toString());
            }
            cValue.put("Date", String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day));
            cValue.put("Name", ap.getName());
            cValue.put("Mac", ap.getMAC());
            db.insert("ap_table", "Name", cValue);
            cValue.clear();
        }

        //用sql语句插入
//        String tArea;
//        String tAreaNum;
//        String tName;
//        String tMac;
//        String tDate;
//        for (AP ap : APSet) {
//            if (area.getText().toString().isEmpty() || areaNum.getText().toString().isEmpty()) {
//                tArea = "未知区域";
//                tAreaNum = "编号未知";
//            } else {
//                tArea = area.getText().toString();
//                tAreaNum = areaNum.getText().toString();
//            }
//            tName = ap.getName();
//            tMac = ap.getMAC();
//            tDate = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);
//            String insertTable = "INSERT INTO ap_table (ID, Area, AreaNum, Name, Mac, Date) VALUES (NULL, tArea, tAreaNum, tName, tMac, tDate)";
//            db.execSQL(insertTable);
//        }
        Log.e("size", "insertDB: " + db.getMaximumSize());

//        db.close();

    }
    private StringBuffer macconvert2(String mac){
        mac.trim();
        StringBuffer after = new StringBuffer("");
        if (mac.length() == 17) {
            for (int i = 0; i < 16; i = i + 3) {
                int k = Integer.parseInt(mac.substring(i, i + 2), 16);
                after.append(k);
                after.append(":");
            }
        }
        after = after.deleteCharAt(after.length() - 1);
        return after;
    }


    private StringBuffer coordinateconvert(String coordinate) {
        StringBuffer after = new StringBuffer("");
        if (coordinate.length() > 0) {
            int x = Integer.parseInt(coordinate);
            for (int i = 1; i <= 40; i++) {
                if (i == x) {
                    after.append("1");
                } else {
                    after.append("0");
                }
            }
        }
        return after;
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SCAN:
                    int len = APSet.size();
                    textView.setText(String.valueOf(len));
                    break;
                case SCAN1000:
                    showDialog();
                    break;
                default:
                    break;
            }
        }
    };

    private void showDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("采集完成");
        builder.setMessage("采集1000点完成");//提示内容
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(getApplicationContext(), "点击了确定按钮", Toast.LENGTH_SHORT).show();
            }
        });
//        builder.setNegativeButton("取消啊", new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                Toast.makeText(getApplicationContext(), "点击了取消按钮", Toast.LENGTH_SHORT).show();
//            }
//        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
