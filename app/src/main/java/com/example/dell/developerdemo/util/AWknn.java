package com.example.dell.developerdemo.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AWknn {
    private static final int XMAX = 5;
    private static final int YMAX = 6;

    private Context mContext;
    private double[][][] rssiAve;

    public AWknn(Context c) {
        this.mContext = c;
        readFingerDatabase(4);
    }

    private void readFingerDatabase(int APNum) {
        RssiDBManager rssiDBManager = new RssiDBManager(mContext);
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
        sqLiteDatabase.close();
    }

    public double[] start(Map<String, Integer> rssiMap, int K, int A) {
        double[] curLoc = new double[2];
        int[] rssiArr = new int[4];
        List<Integer> validList = new ArrayList<>();
        List<Integer> footList = new ArrayList<>();
        int foot = 0;
        for (Integer rssi: rssiMap.values()){
            if (rssi != -100) {
                validList.add(rssi);
                footList.add(foot);
            }
            rssiArr[foot++] = rssi;
        }
        Integer[] footArray = footList.toArray(new Integer[0]);
        Integer[] dataArray = validList.toArray(new Integer[0]);
        myBubbleSort(footArray, dataArray);

        A = Math.min(validList.size(), A);
        double[][] dis = new double[5][6];
        // 只对前A个进行运算
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                for (int a = 0; a < A; a++) {
                    dis[i][j] += Math.abs(rssiArr[footArray[a]] - rssiAve[i][j][footArray[a]]);
                }
            }
        }
        // minK(dis,X,Y)
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

    private void myBubbleSort(Integer[] footArr, Integer[] dataArr) {
        int temp;
        boolean flag = true;
        for (int i = 1; flag && i < dataArr.length; i++) {
            flag = false;
            for (int j = 0; j < dataArr.length - i; j++) {
                if (dataArr[j] > dataArr[j + 1]){
                    temp = dataArr[j];
                    dataArr[j] = dataArr[j + 1];
                    dataArr[j + 1] = temp;

                    temp = footArr[j];
                    footArr[j] = footArr[j + 1];
                    footArr[j + 1] = temp;
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
}
