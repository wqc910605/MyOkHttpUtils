package com.wwf.myapplication.okhttputil;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Dell on 2017/12/4.
 */

public abstract class NetObserver implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        updateData((String) arg);
    }
    public abstract void updateData(String json);
}