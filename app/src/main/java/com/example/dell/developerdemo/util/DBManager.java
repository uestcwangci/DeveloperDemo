package com.example.dell.developerdemo.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.dell.developerdemo.beans.WifiData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBManager {
    private Context mContext;
    private Set<String> macSet;
    /*选择题的集合*/
    public List<WifiData> mBeanLists = new ArrayList<>();

    public DBManager(Context mContext) {
        this.mContext = mContext;
    }

    //把assets目录下的db文件复制到dbpath下
    public SQLiteDatabase manage(String dbName) {
        String dbPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/databases/" + dbName;
        if (new File(dbPath).exists()) {
            File file = new File(dbPath);
            file.delete();
        }
        if (!new File(dbPath).exists()) {
            try {
                boolean flag = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/databases/").mkdirs();
                boolean newFile = new File(dbPath).createNewFile();
                try {
                    FileOutputStream out = new FileOutputStream(dbPath);
                    InputStream in = mContext.getAssets().open(dbName);
                    byte[] buffer = new byte[1024];
                    int readBytes = 0;
                    while ((readBytes = in.read(buffer)) != -1)
                        out.write(buffer, 0, readBytes);
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return SQLiteDatabase.openOrCreateDatabase(dbPath, null);
    }

    //查询选择题
    public List<WifiData> query(SQLiteDatabase sqliteDB, String tableName, String[] columns, String selection, String[] selectionArgs) {
        WifiData wifiData = null;
        try {
            macSet = new HashSet<>();
            Cursor cursor = sqliteDB.query(tableName, columns, selection, selectionArgs, null, null, null);
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("Name"));
                String mac = cursor.getString(cursor.getColumnIndex("Mac"));
                if (!macSet.contains(mac)) {
                    macSet.add(mac);
                    wifiData = new WifiData(name, mac);
                    mBeanLists.add(wifiData);
                }
            }
            cursor.close();
            return mBeanLists;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //
}
