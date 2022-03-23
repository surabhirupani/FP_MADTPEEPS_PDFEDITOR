package com.example.pdfeditormadtpeeps.Utility;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PDFEncryptionUtility {

    private final Activity mContext;
    private final FileUtils mFileUtils;
    private String mPassword;
    private final SharedPreferences mSharedPrefs;

    public PDFEncryptionUtility(Activity context) {
        this.mContext = context;
        this.mFileUtils = new FileUtils(context);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

}
