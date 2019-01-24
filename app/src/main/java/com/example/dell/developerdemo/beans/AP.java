package com.example.dell.developerdemo.beans;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Objects;

public class AP {
    private String Name;
    private String MAC;

    public AP() {
    }

    public AP(String name, String MAC) {
        this.Name = name;
        this.MAC = MAC;
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AP ap = (AP) o;
        return Objects.equals(getMAC(), ap.getMAC());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {

        return Objects.hash(getMAC());
    }

    @Override
    public String toString() {
        return "AP{" +
                "Name = '" + Name + '\'' +
                ", MAC = '" + MAC + '\'' +
                '}';
    }
}
