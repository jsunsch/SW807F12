package edu.aau.utzon.webservice;

import android.net.Uri;
import android.provider.BaseColumns;

public final class ProviderContract {
	public  static final String AUTHORITY = "edu.aau.utzon";
	
	// This class cannot be instantiated
    private ProviderContract() {
    }
    
    /*
     * "Points" table
     */
    public static final class Points implements BaseColumns {
    	// This class cannot be instantiated
    	private Points() {}
    	
    	public static final String TABLE_NAME = "points";
    	
        private static final String SCHEME = "content://";
    	
    	// Attributes
    	public static final String ATTRIBUTE_ID = "_ID"; // DONT CHANGE!
    	public static final String ATTRIBUTE_LAT = "lat";
    	public static final String ATTRIBUTE_LONG = "long";
    	public static final String ATTRIBUTE_STATE = "state";
    	public static final String ATTRIBUTE_LAST_MODIFIED = "last_modified";
    	public static final String ATTRIBUTE_DESCRIPTION = "description";
    	public static final String ATTRIBUTE_NAME = "name";
    	
    	// Attribute states
    	public static final int STATE_OK = 1;
    	public static final int STATE_POSTING = 2;
    	public static final int STATE_UPDATING = 3;
    	public static final int STATE_DELETING = 4;
    	
    	// Position of ID
    	public static final int ATTRIBUTE_ID_POSITION = 1;
    	public static final int ATTRIBUTE_LAT_POSITION = 2;
    	public static final int ATTRIBUTE_LONG_POSITION = 3;
    	public static final int ATTRIBUTE_STATE_POSITION = 4;
    	public static final int ATTRIBUTE_LAST_MODIFIED_POSITION = 5;
    	public static final int ATTRIBUTE_DESCRIPTION_POSITION = 6;
    	public static final int ATTRIBUTE_NAME_POSITION = 7;
    	
    	// URI Paths
    	public static final String PATH_POINTS = "/points";
    	public static final String PATH_POINT_ID = "/points/";
    	
    	// The content:// style URL for this table
    	public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_POINTS);
    	
    	/**
         * The content URI base for a single note. Callers must
         * append a numeric note id to this Uri to retrieve a note
         */
    	public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_POINT_ID);
    	
    	/**
         * The content URI match pattern for a single note, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + PATH_POINT_ID + "/#");
        
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.aau.point";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.aau.point";
        
        public static final String DEFAULT_SORT_ORDER = ATTRIBUTE_ID + " ASC";
        
        public final static  String[] PROJECTIONSTRING_ALL = {
    		ProviderContract.Points.ATTRIBUTE_ID, 	
    		ProviderContract.Points.ATTRIBUTE_LAT, 
    		ProviderContract.Points.ATTRIBUTE_LONG, 
    		ProviderContract.Points.ATTRIBUTE_DESCRIPTION,
    		ProviderContract.Points.ATTRIBUTE_LAST_MODIFIED,
    		ProviderContract.Points.ATTRIBUTE_NAME,
    		ProviderContract.Points.ATTRIBUTE_STATE};
    }
}
