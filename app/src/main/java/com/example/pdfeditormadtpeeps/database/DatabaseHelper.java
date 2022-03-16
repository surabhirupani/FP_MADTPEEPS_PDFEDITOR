package com.example.pdfeditormadtpeeps.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

public class DatabaseHelper {
    private final Context mContext;

    public DatabaseHelper(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * To insert record in the database
     * @param filePath path of the file
     * @param operationType operation performed on file
     */
    public void insertRecord(String filePath, String file_date, String operationType, String file_name) {
        new Insert().execute(new History(filePath,  file_date, operationType, file_name));
    }

    public void deleteRecord(String file_name) {
        new Delete().execute(file_name);
    }

    public void updateRecord(String file_name, String file_path, String file_patho) {
        new Update().execute(file_name, file_path, file_patho);
    }

    @SuppressLint("StaticFieldLeak")
    private class Insert extends AsyncTask<History, Void, Void> {

        @Override
        protected Void doInBackground(History... histories) {
            AppDatabase db = AppDatabase.getDatabase(mContext.getApplicationContext());
            db.historyDao().insertAll(histories);
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Delete extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            AppDatabase db = AppDatabase.getDatabase(mContext.getApplicationContext());
            db.historyDao().deleteHistory(params[0]);
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Update extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            AppDatabase db = AppDatabase.getDatabase(mContext.getApplicationContext());
            db.historyDao().updateHistory(params[0], params[1], params[2]);
            return null;
        }
    }
}