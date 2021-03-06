package com.lockminds.tayari

import android.content.Context
import android.content.SharedPreferences
import com.lockminds.tayari.constants.Constants


/**
 * Session manager to save and fetch data from SharedPreferences
 */
class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences( Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)


    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(Constants.LOGIN_TOKEN, token)
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(Constants.LOGIN_TOKEN, null)
    }

    /**
     * Function to fetch user id
     */
    fun fetchId(): String? {
        return prefs.getString(Constants.USER_ID, null)
    }

    /**
     * Function to fetch user id
     */
    fun fetchPhonenumber(): String? {
        return prefs.getString(Constants.PHONE_NUMBER, "")
    }

    /**
     * Function to fetch user id
     */
    fun getFCMToken(): String? {
        return prefs.getString(Constants.FCM_TOKEN, null)
    }

}