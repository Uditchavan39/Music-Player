package com.thursday.musicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class DBhandler extends SQLiteOpenHelper {
    public static final String DB_Name = "All_AlbumList";
    private static final int DB_Version = 1;
     private String Table_Name = "songs";
    private final String Name_Col = "Songs";
    ArrayList<String> playlist_list=new ArrayList<>();
    ArrayList<String> Song_list=new ArrayList<>();
    public DBhandler(@Nullable Context context) {
        super(context, DB_Name, null, DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String Query = "CREATE TABLE IF not exists " + Table_Name +"( "
                 + "ID INTEGER Primary key AUTOINCREMENT not null, "
                + Name_Col + " not null )";
        db.execSQL(Query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Table_Name);
        onCreate(db);
  }

    public void addNewSongstoplaylist(Context context,String songName,String Table_Name) {
      if (Table_Name!=null){
          Table_Name="\""+Table_Name+"\"";
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
          values.put(Name_Col,songName);
        db.insert(Table_Name, null, values);
        db.close();}
      else {
          Toast.makeText(context,"Failed to Add Songs.",Toast.LENGTH_SHORT).show();
      }
    }
    public void removeSongstoplaylist(Context context,String songName,String Table_Name) {
        if (Table_Name!=null){
            SQLiteDatabase db = this.getWritableDatabase();
        String query="DELETE FROM "+"\""+Table_Name+"\""+" WHERE Songs = "+"\""+songName+"\"";
        db.execSQL(query);
            db.close();}
        else {
            Toast.makeText(context,"Failed to Remove Songs.",Toast.LENGTH_SHORT).show();
        }
    }
public  void  createplaylist(String mtext){
        Table_Name="\""+mtext+"\"";
        onCreate(this.getWritableDatabase());
}
    public ArrayList<String> getPlaylistsName() {
        playlist_list.clear();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' and name!='android_metadata' and name!='sqlite_sequence'", null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                playlist_list.add(c.getString(c.getColumnIndex("name")));
     c.moveToNext();
            }
        }
    return playlist_list;
    }
    public ArrayList<String> getSongsName(String table_Name) {
       Song_list.clear();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM "+"\""+table_Name+"\"", null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                Song_list.add(c.getString(c.getColumnIndex("Songs")));
                c.moveToNext();
            }
        }
        return Song_list;
    }
    public void RemovePlaylistName(Context context,String name){
        SQLiteDatabase db=this.getWritableDatabase();
        String query="DROP TABLE IF EXISTS "+"\""+name+"\"";
        db.execSQL(query);
        Toast.makeText(context,name+" Removed.",Toast.LENGTH_SHORT).show();

    }
}
