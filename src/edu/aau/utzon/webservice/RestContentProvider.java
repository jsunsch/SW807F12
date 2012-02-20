package edu.aau.utzon.webservice;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class RestContentProvider extends ContentProvider{

	// SQL "backend" for the content provider
	static class RestDB extends SQLiteOpenHelper{
		private static final int DATABASE_VERSION = 2;
		private static final String DATABASE_NAME = "utzondb";
	    private static final String POINT_TABLE_NAME = "points";
	    
		// Attributes
	    private static final String POINT_ID = "ID";
		private static final String POINT_X = "X";
		private static final String POINT_Y = "Y";
		private static final String POINT_DESC = "DESC";
		
	    private static final String POINT_TABLE_CREATE =
	                "CREATE TABLE " + POINT_TABLE_NAME + " (" +
	                POINT_ID + " INTEGER, " +
	                POINT_X + "REAL, " +
	                POINT_Y + "REAL, " +
	                POINT_DESC + "TEXT, );";
		  
		public RestDB(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			 db.execSQL(POINT_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Kills the table and existing data
	        db.execSQL("DROP TABLE IF EXISTS " + POINT_TABLE_NAME);

	        // Recreates the database with a new version
	        onCreate(db);
		}
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
