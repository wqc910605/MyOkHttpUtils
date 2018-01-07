package com.wwf.myapplication.okhttputil;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Dell on 2017/12/4.
 */

public class NetObservable extends Observable {
//    private Object data = null;
    private static NetObservable sPublish;

    private NetObservable() {

    }

    public static NetObservable getInstance() {
        if (sPublish == null) {
            sPublish = new NetObservable();
        }
        return sPublish;
    }


    public void setData(Object obj) {
//        if (this.data != obj) {
//            this.data = obj;
//        }
        setChanged();//改变统治者的状态
        notifyObservers(obj);    //调用父类Observable方法，通知所有观察者
    }

    public void removeObserver(Observer observer) {
        deleteObserver(observer);
    }
}