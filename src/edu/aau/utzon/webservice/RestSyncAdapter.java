package edu.aau.utzon.webservice;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

public class RestSyncAdapter extends AbstractThreadedSyncAdapter{

	private final Context mContext;
	
	public RestSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mContext = context;
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		// TODO Auto-generated method stub
		
	}

}
