package edu.aubg.useractivity;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "Database Helper";

    private static final String TABLE_NAME = "Activities4";
    private static final String COL1 = "ID";
    private static final String COL2 = "Type";
    private static final String COL3 = "StartTime";

    public DBHelper(Context context)
    {
        super(context,TABLE_NAME, null , 1, null);

    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            //COL2 + " TEXT, " + COL3 + " INTEGER)";
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, Type INTEGER, StartTime INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1){
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(int activityname, long starttime)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL2, activityname);
        contentValues.put(COL3, starttime);

        Log.d(TAG, "Add Data: Adding " + activityname + " " + starttime + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String getlastrowquery = "SELECT * FROM " + TABLE_NAME + " ORDER BY StartTime DESC LIMIT 1";
        Cursor data = db.rawQuery(getlastrowquery,null);
        return data;
    }

}
