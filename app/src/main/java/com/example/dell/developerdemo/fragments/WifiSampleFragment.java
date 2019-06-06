package com.example.dell.developerdemo.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.developerdemo.R;
import com.example.dell.developerdemo.beans.WifiData;
import com.example.dell.developerdemo.util.DBManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WifiSampleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WifiSampleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WifiSampleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int SHOW_WIFI = 656;
    private static final int START_SCAN = 276;
    private static final int STOP_SCAN = 722;
    private static final int SCAN_TIMES = 522;
    private static final int APNUM = 10;
    private static final String TAG = "myWifi";

    private WifiManager mWifiManager;
    private SQLiteDatabase db;

    private View mView;
    Button showWifi, startSample, startSampleTimes, initBt, stopSample;
    TextView textView;
    EditText x, y, sampleDuration, timesView;
    private Ringtone mRingtone;


    //    private int timer = 60;
    private int flag;
    private long startTime, endTime;
    private long samplePeriod;
    private int sampleCount;
    private int sampleTimes;

    private List<WifiData> wifiForm = new ArrayList<>(); // 存放数据库中读取的wifi表单
    private List<ScanResult> results;
    private Set<String> wifiMacSet = new LinkedHashSet<>();
    private Map<String, Integer> wifiMap = new LinkedHashMap<>();

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public WifiSampleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WifiSampleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WifiSampleFragment newInstance(String param1, String param2) {
        WifiSampleFragment fragment = new WifiSampleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_wifi_sample, container, false);
        //不息屏
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initWifiManager();
        initUI();
        return mView;
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(wifiScanReceiver);
//        db.close();
        super.onDestroy();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    private void initWifiManager() {
        this.results = null;
        mWifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {// 打开wifi
            mWifiManager.setWifiEnabled(true);
        }
        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getContext().registerReceiver(wifiScanReceiver, wifiFilter);
        readAPDatabase();

//        success = mWifiManager.startScan();
    }

    private void initUI() {
        ButtonListener buttonListener = new ButtonListener();
        showWifi = mView.findViewById(R.id.show_wifi);
        startSample = mView.findViewById(R.id.start_sample);
        startSampleTimes = mView.findViewById(R.id.sample_times);
        stopSample = mView.findViewById(R.id.stop_sample);
        initBt = mView.findViewById(R.id.initBt);
        showWifi.setOnClickListener(buttonListener);
        initBt.setOnClickListener(buttonListener);
        startSample.setOnClickListener(buttonListener);
        startSampleTimes.setOnClickListener(buttonListener);
        stopSample.setOnClickListener(buttonListener);

        textView = mView.findViewById(R.id.textview);
        x = mView.findViewById(R.id.x);
        y = mView.findViewById(R.id.y);
        sampleDuration = mView.findViewById(R.id.timer);
        timesView = mView.findViewById(R.id.times);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mRingtone = RingtoneManager.getRingtone(getContext(), uri);
    }

    class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.initBt:
                    if (wifiForm.isEmpty()) {
                        readAPDatabase();
                    }
                    initWifiSet();
                    initWifiMap();
                    db = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory() + "/wifiInfo.db", null);
                    createTable(db);
                    Toast.makeText(getContext(), "初始化成功", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.show_wifi:
                    flag = SHOW_WIFI;
                    mWifiManager.startScan();
                    break;
                case R.id.start_sample:
                    String str = String.valueOf(sampleDuration.getText());
                    if (str == null || str.isEmpty()) {
                        samplePeriod = 999999999;
                    } else {
                        samplePeriod = Integer.parseInt(str) * 60 * 1000;
                    }
                    startTime = System.currentTimeMillis();
                    flag = START_SCAN;
                    mWifiManager.startScan();
                    break;
                case R.id.sample_times:
                    String timesStr = String.valueOf(timesView.getText());
                    if (timesStr.isEmpty()) {
                        sampleTimes = 100;
                    } else {
                        sampleTimes = Integer.parseInt(timesStr);
                    }
                    sampleCount = 0;
                    flag = SCAN_TIMES;
                    mWifiManager.startScan();
                    break;
                case R.id.stop_sample:
                    flag = STOP_SCAN;
                    break;
                default:
                    break;
            }
        }
    }

    private void readAPDatabase() {
        DBManager dbManager = new DBManager(getContext().getApplicationContext());
        SQLiteDatabase sqLiteDatabase = dbManager.manage("ESPonly.db");
        String[] columns = new String[]{"Name", "Mac"};
        wifiForm = dbManager.query(sqLiteDatabase, "wiwide", columns, null, null);

        for (WifiData data : wifiForm) {
            Log.e("form", "data:" + data.toString());
        }
        sqLiteDatabase.close();
    }


    private void scanSuccess() {
        Log.d(TAG, "scanSuccess");
        initWifiMap();
        results = mWifiManager.getScanResults();
        for (ScanResult result : results) {
            if (wifiMacSet.contains(result.BSSID)) {
//                flag = 1;
                wifiMap.put(result.BSSID, result.level);
            }
        }
        results.clear();

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



    private void initWifiSet() {
        for (WifiData wifiData : wifiForm) {
            wifiMacSet.add(wifiData.getMAC());
        }
    }


    private void initWifiMap() {
        for (WifiData wifiData : wifiForm) {
            wifiMap.put(wifiData.getMAC(), wifiData.getLevel());
        }
        Log.d("Form", wifiForm.toString());
        Log.d("Map", wifiMap.toString());
    }



    /**
     * 将搜索到的wifi根据信号强度从强到时弱进行排序
     *
     * @param list 存放周围wifi热点对象的列表
     */
    private void sortByLevel(ArrayList<WifiData> list) {

        Collections.sort(list, new Comparator<WifiData>() {

            @Override
            public int compare(WifiData lhs, WifiData rhs) {
                return rhs.getLevel() - lhs.getLevel();
            }
        });
    }


    private void createTable(SQLiteDatabase db) {
        StringBuilder APBuilder = new StringBuilder();
        for (WifiData data : wifiForm) {
            APBuilder.append("Mac_");
            APBuilder.append(data.getMAC().replace(":", "_"));
            APBuilder.append(" VARCHAR(4),");
        }
        String str = APBuilder.toString();
        Log.e("builder", str);
        //创建表SQL语句
        String stu_table = "create table if not exists wifi_table (Id integer primary key autoincrement not null," +
                "x smallint," +
                "y smallint," +
                str +
                "Date char(10))";
        //执行SQL语句
        db.execSQL(stu_table);
    }


    private void insertDB() {
        //获取日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dateStr = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);

        //实例化常量值
        ContentValues cValue = new ContentValues();

        // 添加数据
        //添加Location
        if (x.getText().toString().isEmpty() || y.getText().toString().isEmpty()) {
            cValue.put("x", 0);
            cValue.put("y", 0);
        } else {
            cValue.put("x", Integer.parseInt(x.getText().toString()));
            cValue.put("y", Integer.parseInt(y.getText().toString()));
        }
        for (Map.Entry<String, Integer> entry : wifiMap.entrySet()) {
            //添加功率
            cValue.put("Mac_" + entry.getKey().replace(":", "_"), entry.getValue());
        }
        //添加日期
        cValue.put("Date", dateStr);
        //调用insert()方法插入数据
        db.insert("wifi_table", null, cValue);
    }



    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_WIFI:
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, Integer> entry : wifiMap.entrySet()) {
                        sb.append("Name:").append(entry.getKey()).append("\tRSSI:").append(entry.getValue()).append("\n");
                    }
                    textView.setText(sb.toString());
                    mWifiManager.startScan();
                    break;
                case START_SCAN:
                    endTime = System.currentTimeMillis();
                    if (endTime - startTime < samplePeriod) {
                        insertDB();
                        mWifiManager.startScan();
                    } else {
                        showDialog();
                    }
                    break;
                case SCAN_TIMES:
                    if (sampleCount < sampleTimes) {
                        if (sampleCount != 0 && (sampleCount + 1) % 10 == 0) {
                            textView.setText(String.valueOf(sampleCount));
                        }
                        sampleCount++;
                        insertDB();
                        mWifiManager.startScan();
                    } else {
                        showDialog();
                    }
                    break;
                case STOP_SCAN:
                    Toast.makeText(getContext(), "Stop Scan", Toast.LENGTH_SHORT).show();
                    db.close();
                    break;
                default:
                    break;
            }
        }
    };

    private void showDialog() {
        flag = STOP_SCAN;
        mRingtone.play();
        sampleCount = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("采集完成");
        builder.setMessage("数据采集完成");//提示内容
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(getApplicationContext(), "点击了确定按钮", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
