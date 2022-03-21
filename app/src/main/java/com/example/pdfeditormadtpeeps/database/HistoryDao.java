package com.example.pdfeditormadtpeeps.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM History order by mId desc LIMIT 15")
    List<History> getAllHistory();

    @Insert
    void insertAll(History... histories);

    @Query("Delete from History where file_name=:file_name")
    void deleteHistory(String file_name);


    @Query("Update History SET file_name=:file_name, file_path=:file_path where file_path=:file_patho")
    void updateHistory(String file_name, String file_path, String file_patho);

    @Query("select * from history where operation_type IN(:types) order by mId desc")
    List<History> getHistoryByOperationType(String[] types);
}