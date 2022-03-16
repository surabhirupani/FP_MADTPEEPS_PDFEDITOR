package com.example.pdfeditormadtpeeps.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class History {
    @PrimaryKey(autoGenerate = true)
    private int mId;

    @ColumnInfo(name = "file_path")
    private String mFilePath;

    @ColumnInfo(name = "date")
    private String mDate;

    @ColumnInfo(name = "operation_type")
    private String mOperationType;

    @ColumnInfo(name = "file_name")
    private String mFileName;

    public History(String filePath, String date, String operationType, String mFileName) {
        this.mFilePath = filePath;
        this.mDate = date;
        this.mOperationType = operationType;
        this.mFileName = mFileName;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String fileName) {
        this.mFilePath = fileName;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public String getOperationType() {
        return mOperationType;
    }

    public void setOperationType(String operationType) {
        this.mOperationType = operationType;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String FileName) {
        this.mFileName = FileName;
    }
}
