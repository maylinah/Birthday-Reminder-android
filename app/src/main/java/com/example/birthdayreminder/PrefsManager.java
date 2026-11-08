package com.example.birthdayreminder;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PrefsManager {
    private static final String PREF_NAME = "BirthdayPrefs";
    private static final String KEY_BIRTHDAY_LIST = "birthday_list";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PrefsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void addBirthdayItem(String name, int day, int month, int year, int daysBefore, boolean isSoundEnabled) {
        JSONArray currentList = getBirthdayListJson();

        try {
            JSONObject newItem = new JSONObject();
            newItem.put("name", name);
            newItem.put("day", day);
            newItem.put("month", month);
            newItem.put("year", year);
            newItem.put("daysBefore", daysBefore);
            newItem.put("isSoundEnabled", isSoundEnabled);

            currentList.put(newItem);
            editor.putString(KEY_BIRTHDAY_LIST, currentList.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getBirthdayListJson() {
        String jsonString = sharedPreferences.getString(KEY_BIRTHDAY_LIST, "[]");
        try {
            return new JSONArray(jsonString);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    public void clearAll() {
        editor.remove(KEY_BIRTHDAY_LIST);
        editor.apply();
    }
}