package com.example.dell.developerdemo.beans;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Objects;


public class WifiData {
    private String Name;
    private String MAC;
    private int level;

    public WifiData() {
    }

    public WifiData(String name, String MAC) {
        Name = name;
        this.MAC = MAC;
        this.level = -100;
    }

    public WifiData(String name, String MAC, int level) {
        Name = name;
        this.MAC = MAC;
        this.level = level;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "WifiData{" +
                "Name = '" + Name + '\'' +
                ", MAC = '" + MAC + '\'' +
                ", level = " + level +
                '}';
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WifiData wifiData = (WifiData) o;
        return Objects.equals(getMAC(), wifiData.getMAC());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {

        return Objects.hash(getMAC());
    }
}
