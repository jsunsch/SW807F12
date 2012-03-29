package edu.aau.utzon.webservice;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class RestContentProvider extends ContentProvider{

	// SQL "backend" for the content provider
	static class RestDB extends SQLiteOpenHelper{
		private static final String DATABASE_NAME = "utzon.db";
		private static final int DATABASE_VERSION = 13;

	    private static final String POINT_TABLE_CREATE =
	                "CREATE TABLE " + ProviderContract.Points.TABLE_NAME + " (" +
	                ProviderContract.Points.ATTRIBUTE_ID + " INTEGER UNIQUE, " +
	                ProviderContract.Points.ATTRIBUTE_X + " REAL, " +
	                ProviderContract.Points.ATTRIBUTE_Y + " REAL, " +
	                ProviderContract.Points.ATTRIBUTE_STATE + " INTEGER, " +
	                ProviderContract.Points.ATTRIBUTE_LAST_MODIFIED + " INTEGER, " +
	                ProviderContract.Points.ATTRIBUTE_DESCRIPTION + " TEXT);";
		  
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
	        db.execSQL("DROP TABLE IF EXISTS " + ProviderContract.Points.TABLE_NAME);

	        // Recreates the database with a new version
	        onCreate(db);
		}
	}
	
	// Uri matcher
	private static final int URI_POINT = 1;
	private static final int URI_POINT_ID = 2;
	
	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static{
		 sUriMatcher.addURI(ProviderContract.AUTHORITY, "points", URI_POINT);
		 sUriMatcher.addURI(ProviderContract.AUTHORITY, "points/#", URI_POINT_ID);	
	}
	
	private RestDB mDBHelper;
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Open DB in write-mode
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
		// Used for counting amount of rows deleted
		int count;
		
		// Try to match the URI
		switch(sUriMatcher.match(uri))
		{
			// Whole table
			case URI_POINT:
				count = db.delete(ProviderContract.Points.TABLE_NAME, selection, selectionArgs);
				break;
			// Specific tuple
			case URI_POINT_ID:
				selection = selection + ProviderContract.Points.ATTRIBUTE_ID + " = " + uri.getLastPathSegment();
				count = db.delete(ProviderContract.Points.TABLE_NAME, selection, selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		/* Gets a handle to the content resolver object for the current context, and notifies it
         * that the incoming URI changed. The object passes this along to the resolver framework,
         * and observers that have registered themselves for the provider are notified.
         */
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of rows deleted.
        return count;
	}

	@Override
	public String getType(Uri uri) {
       /**
        * Chooses the MIME type based on the incoming URI pattern
        */
       switch (sUriMatcher.match(uri)) {
           case URI_POINT:
        	   return ProviderContract.Points.CONTENT_TYPE;
           case URI_POINT_ID:
               return ProviderContract.Points.CONTENT_ITEM_TYPE;
           // If the URI pattern doesn't match any permitted patterns, throws an exception.
           default:
               throw new IllegalArgumentException("Unknown URI " + uri);
       }
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// Validates the incoming URI. Only the full provider URI(whole table) is allowed for inserts.
        if (sUriMatcher.match(uri) != URI_POINT) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        // A map to hold the new record's values.
        ContentValues values;
        
        // If the incoming values map is not null, uses it for the new values.
        if (initialValues != null) {
            values = new ContentValues(initialValues);

        } else {
            // Otherwise, create a new value map
            values = new ContentValues();
        }
        
        // Check that all required attributes are set
        if (values.containsKey(ProviderContract.Points.ATTRIBUTE_ID) == false) {
            throw new IllegalArgumentException("Invalid insertion values " + values);
        }
        if (values.containsKey(ProviderContract.Points.ATTRIBUTE_X) == false) {
            throw new IllegalArgumentException("Invalid insertion values " + values);
        }
        if (values.containsKey(ProviderContract.Points.ATTRIBUTE_Y) == false) {
            throw new IllegalArgumentException("Invalid insertion values " + values);
        }
        if (values.containsKey(ProviderContract.Points.ATTRIBUTE_DESCRIPTION) == false) {
            throw new IllegalArgumentException("Invalid insertion values " + values);
        }

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        int id = (Integer)values.get("_ID");
        db.delete(ProviderContract.Points.TABLE_NAME, "_ID=?", new String[] {Integer.toString(id) });
 
        // set modified attribute
        
        values.put(ProviderContract.Points.ATTRIBUTE_LAST_MODIFIED, System.currentTimeMillis());
        
        // Opens the database object in "write" mode.
        
        
        long rowId = db.insert(ProviderContract.Points.TABLE_NAME, 
        		ProviderContract.Points.ATTRIBUTE_DESCRIPTION, // "A hack, SQLite sets this column value to null if values is empty." (c) Google <- What?
        		values);
        
        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            // Creates a URI with the note ID pattern and the new row ID appended to it.
            Uri noteUri = ContentUris.withAppendedId(ProviderContract.Points.CONTENT_ID_URI_BASE, rowId);

            // Notifies observers registered against this provider that the data changed.
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        return uri;
        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        // throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		mDBHelper = new RestDB(getContext());
		
		// Assumes that any failures will be reported by a thrown exception.
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
	    // Constructs a new query builder and sets its table name
	    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	    qb.setTables(ProviderContract.Points.TABLE_NAME);
		
		// Try to match the URI
		switch(sUriMatcher.match(uri))
		{
			// Whole table
			case URI_POINT:
				break;
			// Specific tuple
			case URI_POINT_ID:
				// Add the specific id to the selection
				selection = selection + ProviderContract.Points.ATTRIBUTE_ID + " = " + uri.getLastPathSegment();
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		// If no sort order is given, do ascending
		if(TextUtils.isEmpty(sortOrder))
			sortOrder = ProviderContract.Points.DEFAULT_SORT_ORDER;

		// Open DB in read-mode
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		
		// Do the query
		Cursor c = qb.query(
		           db,            // The database to query
		           projection,    // The columns to return from the query
		           selection,     // The columns for the where clause
		           selectionArgs, // The values for the where clause
		           null,          // don't group the rows
		           null,          // don't filter by row groups
		           sortOrder        // The sort order
		       );
		
		// Tells the Cursor what URI to watch, so it knows when its source data changes
	    c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// Opens the database object in "write" mode.
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        
        int count;
        
        switch(sUriMatcher.match(uri))
        {
	        case URI_POINT:
	        	count = db.update(ProviderContract.Points.TABLE_NAME, values, selection, selectionArgs);
	        	break;
	        case URI_POINT_ID:
	        	selection = selection + ProviderContract.Points.ATTRIBUTE_ID + " = " + uri.getLastPathSegment();
	        	count = db.update(ProviderContract.Points.TABLE_NAME, values, selection, selectionArgs);
	        	break;
	        default:
	        	throw new IllegalArgumentException("Unknown URI " + uri); 
        }
        
        /* Gets a handle to the content resolver object for the current context, and notifies it
         * that the incoming URI changed. The object passes this along to the resolver framework,
         * and observers that have registered themselves for the provider are notified.
         */
        getContext().getContentResolver().notifyChange(uri, null);

        // Returns the number of rows updated.
        return count;
	}
}
