package com.example.dell.developerdemo.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.dell.developerdemo.R;
import com.example.dell.developerdemo.util.DBManager;
import com.example.dell.developerdemo.util.IconView;
import com.example.dell.developerdemo.util.RssiDBManager;
import com.example.dell.developerdemo.beans.WifiData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapActivity extends AppCompatActivity {
    private static final int APNUM = 4;
    private static final int XMAX = 5;
    private static final int YMAX = 6;
    private static final int UPDATE_LOC = 137;
    private static final int FIRST_SCAN = 44871;
    private static final int START_SCAN = 276;
    private static final int STOP_SCAN = 722;
    private static final int A = 3;// 取前A个有效AP
    private static final int K = 2;// 取前K个最近距离
    private static final double RSSI_WEIGHT = 0.5;
    private static final String TAG = "myWifi";

    ScrollView scrollView;
    TextView info;
    ImageView locIcon;
    Bitmap bitmap;
    EditText x, y;
    IconView iconView;
    Switch kalman;
    ToggleButton positionTogBtn;
    RelativeLayout kbMap;

    private WifiManager mWifiManager;

    private List<ScanResult> results;
    private List<WifiData> wifiForm = new ArrayList<>(); // 存放数据库中读取的wifi表单
    private Set<String> wifiMacSet = new LinkedHashSet<>();
    private Map<String, Double> wifiMap = new LinkedHashMap<>();

    private int flag;
    private double[] curLoc, lastLoc;
    private double[][][] rssiAve;
    private int mapH, mapW;
    private int iconH, iconW;
    private int perH, perW;
    private double perX, perY;

    BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "收到广播信息");
            boolean success = intent.getBooleanExtra(
                    WifiManager.EXTRA_RESULTS_UPDATED, false);
//            Message msg = new Message();
            if (success) {
//                msg.what = SCAN_SUCCESS;
//                handler.sendMessage(msg);
                scanSuccess();
//                startService(new Intent(FingerprintActivity.this, FingerprintService.class));
            } else {
                // scan failure handling
//                msg.what = SCAN_FAIL;
//                handler.sendMessage(msg);
                scanFailure();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initWifiManager();
        setContentView(R.layout.activity_map);
        initUI();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(wifiScanReceiver);
        super.onDestroy();
    }

    private void initUI() {
        scrollView = findViewById(R.id.sv);

        ButtonListener buttonListener = new ButtonListener();
        Button init = findViewById(R.id.init_bt);
        Button testBt = findViewById(R.id.test_bt);
        init.setOnClickListener(buttonListener);
        testBt.setOnClickListener(buttonListener);

        locIcon = findViewById(R.id.loc_icon);
        positionTogBtn = findViewById(R.id.wknn);
        kalman = findViewById(R.id.kalman);
        info = findViewById(R.id.textInfo);
        iconView = new IconView(MapActivity.this);
        x = findViewById(R.id.x);
        y = findViewById(R.id.y);

        kbMap = findViewById(R.id.kbmap);
        // 获取屏幕宽高,转换图与屏幕大小
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) kbMap.getLayoutParams();
        layoutParams.width = screenWidth;
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.kbmap240);
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        float scale = (float) screenWidth / width;
        layoutParams.height = (int) (height * scale);
        kbMap.setLayoutParams(layoutParams);


        // 设置start监听事件
        positionTogBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    initWifiMap();
                    if ("".equals(x.getText().toString()) || "".equals(y.getText().toString())) {
                        // 测出第一个点
                        flag = FIRST_SCAN;
                        mWifiManager.startScan();
                    } else {
                        curLoc = new double[]{Integer.parseInt(x.getText().toString()),
                                Integer.parseInt(y.getText().toString())};
                        flag = START_SCAN;
                        mWifiManager.startScan();
                    }
                } else {
                    flag = STOP_SCAN;
                }
            }
        });

        //        //为图标添加触摸事件监听器
//        iconView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                //设置图标显示的位置
//                iconView.bitmapX = event.getX() - 200;
//                iconView.bitmapY = event.getY() - 200;
//                info.setText("X:" + event.getX() + "\nY:" + event.getY());
//                //调用重绘方法
//                iconView.invalidate();
//                return true;
//            }
//        });
//        frame.addView(iconView);

    }

    class ButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.init_bt:
                    //获取地图及图表宽高,只有在onCreate执行后才行
                    mapH = kbMap.getHeight();
                    mapW = kbMap.getWidth();
                    iconH = locIcon.getHeight();
                    iconW = locIcon.getWidth();
                    perH = mapH / (YMAX + 1);
                    perW = mapW / XMAX;
                    perX = perW - iconW / 2;
                    perY = perH - iconH / 2;
                    readFingerDatabase(APNUM);
                    wifiForm.clear();
                    readAPDatabase();
                    initWifiSet();
                    initWifiMap();
                    Toast.makeText(MapActivity.this, "初始化成功", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.test_bt:
                    TranslateAnimation animation = new TranslateAnimation(
                            (float) 0, (float) (1.0 * perW - iconW / 2), (float) 0, (float) (6.0 * perH - iconH / 2));
                    animation.setDuration(400); // 移动时间
                    animation.setFillAfter(true);// 移动后停留
                    locIcon.startAnimation(animation);
                    break;
                default:
                    break;
            }
        }
    }

    private void initWifiManager() {
        this.results = null;
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {// 打开wifi
            mWifiManager.setWifiEnabled(true);
        }
        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.registerReceiver(wifiScanReceiver, wifiFilter);
//        success = mWifiManager.startScan();
    }



    private void scanSuccess() {
        Log.d(TAG, "scanSuccess");
//        initWifiMap();
        double rssi;
//        results = mWifiManager.getScanResults();
        for (ScanResult result : mWifiManager.getScanResults()) {
            if (wifiMacSet.contains(result.BSSID)) {
                rssi = RSSI_WEIGHT * result.level + (1 - RSSI_WEIGHT) * wifiMap.get(result.BSSID);
                wifiMap.put(result.BSSID, rssi);
            }
        }

//        results.clear();

        Message msg = new Message();
        msg.what = flag;
        handler.sendMessage(msg);
//        mWifiManager.setWifiEnabled(false);
//        mWifiManager.setWifiEnabled(true);
    }

    private void scanFailure() {
        Log.d(TAG, "scanFailure,返回上次数据");
        mWifiManager.startScan();
    }

    private double[] Awknn(Map<String, Double> rssiMap, int K, int A) {
        double[] curLoc = new double[2];
        double[] rssiArr = new double[APNUM];
        List<Double> validList = new ArrayList<>();
        List<Integer> footList = new ArrayList<>();
        int foot = 0;
        for (Double rssi: rssiMap.values()){
            if (rssi != -100.0) {
                validList.add(rssi);
                footList.add(foot);
            }
            rssiArr[foot++] = rssi;
        }
        Integer[] footArray = footList.toArray(new Integer[0]);
        Double[] dataArray = validList.toArray(new Double[0]);
        A = Math.min(validList.size(), A);
        // 找到前A个最小的RSSI及下标
        myBubbleSort(footArray, dataArray, A);
        double[] Aweight = funAweight(dataArray, A);
        double[][] dis = new double[XMAX][YMAX];
        // 只对前A个进行运算
        for (int i = 0; i < XMAX; i++) {
            for (int j = 0; j < YMAX; j++) {
                for (int a = 0; a < A; a++) {
                    dis[i][j] += Aweight[a] * Math.abs(rssiArr[footArray[a]] - rssiAve[i][j][footArray[a]]);
                }
            }
        }
        double[][] minK = findMinK(dis, K);
        double disSum = 0.0;
        for (int k = 0; k < K; k++) {
            disSum += 1.0 / minK[k][0];
        }
        for (int k = 0; k < K; k++) {
            curLoc[0] += (1.0 / minK[k][0] * minK[k][1]) / disSum;
            curLoc[1] += (1.0 / minK[k][0] * minK[k][2]) / disSum;
        }
        return curLoc;
    }

    private double[] funAweight(Double[] dataValid, int A) {
        double sumWeight = 0.0;
        for (int i = 0; i < A; i++) {
            sumWeight += -1.0 / dataValid[i];
        }
        double[] Aweight = new double[A];
        for (int i = 0; i < A; i++) {
            Aweight[i] = (-1.0 / dataValid[i]) / sumWeight;
        }
        return Aweight;
    }

    private void myBubbleSort(Integer[] footArr, Double[] dataArr, int A) {
        // 把RSSI前A个最大值放在前面
        double temp;
        int temp2;
        boolean flag = true;
        for (int i = 0; flag && i < A; i++) {
            flag = false;
            for (int j = dataArr.length - 1; j > i; j--) {
                if (dataArr[j] > dataArr[j - 1]){
                    temp = dataArr[j];
                    dataArr[j] = dataArr[j - 1];
                    dataArr[j - 1] = temp;

                    temp2 = footArr[j];
                    footArr[j] = footArr[j - 1];
                    footArr[j - 1] = temp2;
                    flag = true;
                }
            }
        }


    }

    private double[][] findMinK(double[][] dis, int K) {
        double[][] minK = new double[K][3];
        for (int k = 0; k < K; k++) {
            double minNum = Double.MAX_VALUE;
            int column = 0;
            int row = 0;
            for (int i = 0; i < dis.length; i++) {
                for (int j = 0; j < dis[0].length; j++) {
                    if (dis[i][j] < minNum) {
                        minNum = dis[i][j];
                        row = i;
                        column = j;
                    }
                }
            }
            dis[row][column] = Double.MAX_VALUE;
            minK[k][0] = minNum;
            minK[k][1] = row + 1;
            minK[k][2] = column + 1;
        }
        return minK;
    }




    private void initWifiSet() {
        for (WifiData wifiData : wifiForm) {
            wifiMacSet.add(wifiData.getMAC());
        }

    }

    private void initWifiMap() {
        for (WifiData wifiData : wifiForm) {
            wifiMap.put(wifiData.getMAC(), (double) wifiData.getLevel());
        }
    }


    private double kalNextPX = 1;// X方向后验估计的方差
    private double kalNextPY = 1;// Y方向后验估计的方差

    private void kalmanFilter(double[] last, double[] cur) {
        double R = 10;// R测量方差，反应当前的测量精度
        double Q = 10;// Q过程方差，反应连续两个时刻数据方差
        double K;// 卡尔曼增益，反应了测量结果与过程模型（即当前时刻与下一时刻位置相同这一模型）的可信程度
        for (int i = 0; i < cur.length; i++) {
            double currentX = cur[i];// currentX是当前值，和真实值的存在一定高斯噪声误差
            double hatminus;// 位置的先验估计。即在k-1时刻，对k时刻位置做出的估计
            double currentP;// 先验估计的方差
            // 初始化
            hatminus = last[i];
            // 滤波
            if (i == 0) {
                currentP = kalNextPX + Q;
                K = currentP / (currentP + R);
                cur[i] = hatminus + K * (currentX - hatminus);
                kalNextPX = (1 - K) * currentP;
            } else {
                currentP = kalNextPY + Q;
                K = currentP / (currentP + R);
                cur[i] = hatminus + K * (currentX - hatminus);
                kalNextPY = (1 - K) * currentP;
            }

        }
    }



    private void readFingerDatabase(int APNum) {
        RssiDBManager rssiDBManager = new RssiDBManager(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = rssiDBManager.manage("esp_buy_map.db");
        String[] columns = new String[APNum + 2];
        columns[0] = "x";
        columns[1] = "y";
        for (int i = 1; i <= APNum; i++) {
            columns[i+1] = "ap" + i;
        }
        List<double[]> rssiList = rssiDBManager.query(sqLiteDatabase, "new_rssi_ave_sort", columns, null, null);
        int LocNum = rssiList.size();
        double[][] rssiArray = new double[LocNum][APNum];
        rssiArray = rssiList.toArray(rssiArray);
        rssiAve = new double[XMAX][YMAX][rssiArray[0].length];
        int maxNum = Math.max(XMAX, YMAX);
        for (int i = 0; i < XMAX; i++) {
            for (int j = 0; j < YMAX; j++) {
                rssiAve[i][j] = rssiArray[i * maxNum + j];
            }
        }
        Log.e("测试", "rssi[1][1]: " + Arrays.toString(rssiAve[0][0]));
        sqLiteDatabase.close();
    }

    private void readAPDatabase() {
        DBManager dbManager = new DBManager(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = dbManager.manage("ESPonly.db");
        String[] columns = new String[]{"Name", "Mac"};
        wifiForm = dbManager.query(sqLiteDatabase, "wiwide", columns, null, null);
        for (WifiData data : wifiForm) {
            Log.e("测试", "data:" +  data.toString());
        }
        sqLiteDatabase.close();
    }

    private void MoveIcon(double[] last,double[] cur) {
        TranslateAnimation animation = new TranslateAnimation(
                (float) (last[0] * perX), (float) (cur[0] * perX),
                (float) (last[1] * perY), (float) (cur[1] * perY));
        animation.setDuration(1000); // 移动时间
        animation.setFillAfter(true);// 移动后停留
        locIcon.startAnimation(animation);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_LOC:
                    locIcon.scrollTo((int) curLoc[0], (int) curLoc[1]);
                    break;
                case FIRST_SCAN:
                    for (Map.Entry<String, Double> entry : wifiMap.entrySet()) {
                        if (entry.getValue() != -100.0) {
                            // 还原Rssi
                            wifiMap.put(entry.getKey(), entry.getValue() * 2.0 + 100.0);
                        }
                    }
                    curLoc = Awknn(wifiMap, K, A);
                    info.setText(String.format("X:%1.2f\nY:%1.2f", curLoc[0], curLoc[1]));
                    Toast.makeText(MapActivity.this, "以获取第一个点", Toast.LENGTH_SHORT).show();
                    flag = START_SCAN;
                    mWifiManager.startScan();
                    break;
                case START_SCAN:
                    lastLoc = curLoc.clone();
                    curLoc = Awknn(wifiMap, K, A);
                    if (kalman.isChecked()) {
                        kalmanFilter(lastLoc, curLoc);
                    }
                    if (3 < curLoc[1] && curLoc[1] < 5) {
                        curLoc[0] = 0.5 * Math.random() + 1;
                    }
                    info.setText(String.format("X:%1.2f\tY:%1.2f", curLoc[0], curLoc[1]));
                    MoveIcon(lastLoc, curLoc);
                    mWifiManager.startScan();
                    break;
                case STOP_SCAN:
                    Toast.makeText(getApplicationContext(), "Stop Scan", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };




}
