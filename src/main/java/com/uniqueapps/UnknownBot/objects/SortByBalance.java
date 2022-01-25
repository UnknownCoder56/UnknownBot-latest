package com.uniqueapps.UnknownBot.objects;

import java.util.Comparator;
import java.util.Map;

import org.javacord.api.entity.user.User;

public class SortByBalance implements Comparator<User> {

    Map<Long, Long> bals;

    public SortByBalance(Map<Long, Long> bals) {
        this.bals = bals;
    }

    @Override
    public int compare(User o1, User o2) {
        return (int) (bals.get(o1.getId()) - bals.get(o2.getId()));
    }
}
