package com.uniqueapps.UnknownBot.objects;

import java.util.ArrayList;
import java.util.List;

public class Warn {

    public List<String> warnCauses = new ArrayList<>();
    public int warns;
    long userId;

    public Warn(String cause, long userId) {
        warns++;
        warnCauses.add(cause);
        this.userId = userId;
    }

    public Warn() {}

    public void newWarn(String cause) {
        warns++;
        warnCauses.add(cause);
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long id) {
        userId = id;
    }

    public int getWarns() {
        return warns;
    }

    public void setWarns(int warns) {
        this.warns = warns;
    }

    public List<String> getWarnCauses() {
        return warnCauses;
    }

    public void setWarnCauses(List<String> warnCauses) {
        this.warnCauses = warnCauses;
    }
}