package com.uniqueapps.unknownbot.objects;

public class UserSettings {

    boolean bankDmEnabled;
    boolean bankPassiveEnabled;

    public UserSettings(boolean bankDmEnabled, boolean bankPassiveEnabled) {
        this.bankDmEnabled = bankDmEnabled;
        this.bankPassiveEnabled = bankPassiveEnabled;
    }

    public UserSettings() {
        bankDmEnabled = true;
        bankPassiveEnabled = false;
    }

    public void setBankDmEnabled(boolean set) {
        bankDmEnabled = set;
    }

    public void setBankPassiveEnabled(boolean set) {
        bankPassiveEnabled = set;
    }

    public boolean isBankDmEnabled() {
        return bankDmEnabled;
    }

    public boolean isBankPassiveEnabled() {
        return bankPassiveEnabled;
    }
}
