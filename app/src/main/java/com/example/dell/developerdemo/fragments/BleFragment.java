package com.example.dell.developerdemo.fragments;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.dell.developerdemo.BeaconReferenceApplication;
import com.example.dell.developerdemo.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BleFragment extends Fragment implements BeaconConsumer {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "myBle";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayAdapter<String> mAdapter;

    private View mView;
    ListView bleList;
    ToggleButton scanBt;

    private Context applicationContext;

    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());

//    private BluetoothLeScannerCompat mScanner = BluetoothLeScannerCompat.getScanner();
//
//    private final ScanCallback scanCallback = new ScanCallback() {
//        @Override
//        public void onScanResult(int callbackType, @NonNull ScanResult result) {
//            super.onScanResult(callbackType, result);
//            Log.d(TAG, "扫描模式1");
//
//
//            BluetoothDevice device = result.getDevice();
//            mAdapter.add(device.getName() + " - " + device.getAddress() + " - " + result.getRssi());
//            bleList.setAdapter(mAdapter);
//        }
//
//        @Override
//        public void onBatchScanResults(@NonNull List<ScanResult> results) {
//            super.onBatchScanResults(results);
//            Log.d(TAG, "扫描模式2");
//            BluetoothDevice device;
//            mAdapter.clear();
//            bleList.setAdapter(mAdapter);
//            if (results.size() > 0) {
//                for (ScanResult result : results) {
//                    device = result.getDevice();
//                    mAdapter.add(device.getName() + " - " + device.getAddress() + " - " + result.getRssi());
//                }
//                bleList.setAdapter(mAdapter);
//            }
//        }
//
//        @Override
//        public void onScanFailed(int errorCode) {
//            Toast.makeText(getContext(), "ScanFailed", Toast.LENGTH_SHORT).show();
//            super.onScanFailed(errorCode);
//        }
//    };



    public BleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BleFragment newInstance(String param1, String param2) {
        BleFragment fragment = new BleFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_ble, container, false);
        bleList = mView.findViewById(R.id.ble_list);
        scanBt = mView.findViewById(R.id.scan_ble);
        beaconManager.bind(this);

        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        bleList.setAdapter(mAdapter);

//        initBle();
        return mView;
    }

    @Override
    public void onDestroyView() {
//        mScanner.stopScan(scanCallback);
        beaconManager.unbind(this);
        super.onDestroyView();
    }

    private void initBle() {
//        final ScanSettings settings = new ScanSettings.Builder()
//                .setLegacy(false)
//                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                .setReportDelay(1000)
//                .setUseHardwareBatchingIfSupported(true)
//                .build();
//        final List<ScanFilter> filters = new ArrayList<>();
//        filters.add(new ScanFilter.Builder().setDeviceName("ble1").build());
//        filters.add(new ScanFilter.Builder().setDeviceName("ble2").build());
//        filters.add(new ScanFilter.Builder().setDeviceName("ble3").build());
//        filters.add(new ScanFilter.Builder().setDeviceName("ble4").build());
//        scanBt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if (isChecked) {
//                    mAdapter.clear();
//                    mScanner.startScan(filters, settings, scanCallback);
//                } else {
//                    mScanner.stopScan(scanCallback);
//                }
//            }
//        });
    }

    @Override
    public void onBeaconServiceConnect() {
        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    for (Beacon beacon : beacons) {
                        Log.d(TAG, "uuid: " + beacon.getId1());
                    }
                    Log.d(TAG, "didRangeBeaconsInRegion called with beacon count:  " + beacons.size());

                    logToDisplay(beacons, region);
                }
            }

        };
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
        } catch (RemoteException e) {   }
    }


    private void logToDisplay(Collection<Beacon> beacons, Region region) {
        for (Beacon beacon : beacons) {
            mAdapter.add(beacon.getBluetoothName() + " - Rssi: " + beacon.getRssi()
                    + " - AverageRssi:" + beacon.getRunningAverageRssi()
                    + " - Diatance: " + beacon.getDistance());
        }
        bleList.setAdapter(mAdapter);
    }

    @Override
    public Context getApplicationContext() {
        return null;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
    }
}
