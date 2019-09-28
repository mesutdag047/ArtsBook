package com.mesutdag.planlmadefteri;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.HashMap;

public class ArtContentProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.mesutdag.planlmadefteri.ArtContentProvider";
    static final  String URL="content://"+PROVIDER_NAME+"/arts";
    static final Uri CONTENT_URI = Uri.parse(URL);

   static final  String NAME="name";
   static final String IMAGE="image";

   static final int ARTS=1;
   static final UriMatcher uriMatcher;

   static {
       uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
       uriMatcher.addURI(PROVIDER_NAME,"arts",ARTS);
   }

   private static HashMap<String,String>ART_PROJECTION_MAP;

   //----------------Database-----------------------

    private SQLiteDatabase sqLiteDatabase;
    static final String DATABASE_NAME ="Arts";
    static final String ARTS_TABLE_NAME="arts";
    static final int DATABASE_VERSION=1;
    static final String CREATE_DATABASE_TABLE = "CREATE TABLE " +
            ARTS_TABLE_NAME + "(name TEXT NOT NULL, " +
            "image BLOB NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            sqLiteDatabase.execSQL(CREATE_DATABASE_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ARTS_TABLE_NAME);
            onCreate(sqLiteDatabase);

        }
    }
    @Override
    public boolean onCreate() {

        Context context=getContext();
        DatabaseHelper databaseHelper=new DatabaseHelper(context);

        sqLiteDatabase=databaseHelper.getWritableDatabase();


        return sqLiteDatabase != null;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {

        SQLiteQueryBuilder sqLiteQueryBuilder=new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(ARTS_TABLE_NAME);

        switch (uriMatcher.match(uri)){
            case ARTS:
            sqLiteQueryBuilder.setProjectionMap(ART_PROJECTION_MAP);
                break;

                default:
                    //
        }

        if (s1==null || s1.matches("")){
            s1="NAME";

        }

        Cursor cursor=sqLiteQueryBuilder.query(sqLiteDatabase,strings,s,strings1,null,null,s1);

        cursor.setNotificationUri(getContext().getContentResolver(),uri);


        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        Long rowID=sqLiteDatabase.insert(ARTS_TABLE_NAME,"",contentValues);

        if(rowID>0){
            Uri newUri= ContentUris.withAppendedId(CONTENT_URI,rowID);
            getContext().getContentResolver().notifyChange(newUri,null);
            return newUri;
        }

        throw new SQLException("Hatalı");
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
