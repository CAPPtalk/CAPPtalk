package com.example.jangheejun.capptalk;

import android.content.Context;
import android.content.SharedPreferences;

public class SingleSharedPreference {

    public static final String PREFERENCE_NAME="AUTO";
    private static SingleSharedPreference preferencemodule = null;
    private static Context mContext;
    private static SharedPreferences auto;
    private static SharedPreferences.Editor editor;

    public static SingleSharedPreference getInstance(Context context) {
        mContext = context;

        if (preferencemodule == null) {
            preferencemodule = new SingleSharedPreference();
        }
        if(auto==null){
            auto = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            editor = auto.edit();
        }
        return preferencemodule;
    }

    public void putIntExtra(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void putStringExtra(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void putLongExtra(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public void putBooleanExtra(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void clearExtra() {
        editor.clear();
        editor.commit();
    }

    public int getIntExtra(String key) {
        return auto.getInt(key, 0);
    }

    public String getStringExtra(String key) {
        return auto.getString(key, "");
    }


    public long getLongExtra(String key) {
        return auto.getLong(key, 0);
    }


    public boolean getBooleanExtra(String key) {
        return auto.getBoolean(key, false);
    }

    public void removePreference(String key) {
        editor.remove(key).commit();
    }

    public boolean containCheck(String key) {
        return auto.contains(key);
    }

}
