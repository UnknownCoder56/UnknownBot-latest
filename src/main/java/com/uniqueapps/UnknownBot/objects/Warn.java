package com.uniqueapps.UnknownBot.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class Warn implements Serializable {

    public ArrayList<String> warnCauses = new ArrayList<>();
    public int warns;
    long userId;

    public Warn(String cause, long userId) {
        warns++;
        warnCauses.add(cause);
        this.userId = userId;
    }

    public void newWarn(String cause) {
        warns++;
        warnCauses.add(cause);
    }

    public long getUserId() {
        return userId;
    }
}