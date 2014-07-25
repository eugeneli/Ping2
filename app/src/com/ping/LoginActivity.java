package com.ping;

import java.io.IOException;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

import com.ping.fragments.FacebookLoginFragment;
import com.ping.models.PingMap;
import com.ping.util.FontTools;

import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

public class LoginActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener
{	
	public static final String TAG = LoginActivity.class.getSimpleName();
	
	private PingMap map;
	
	//GooglePlus Login variables
	private SignInButton gplusLoginButton;
	private static final int GPLUS_REQUEST_CODE_SIGNIN = 0;
	private GoogleApiClient googleApi;
	private boolean intentInProgress;
	private boolean googleSignInClicked;
	private ConnectionResult googleConnectionResult;
	
	//Facebook Login variables
	private FacebookLoginFragment fbLoginFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		FontTools.applyFont(this, findViewById(R.id.root));
		
		map = new PingMap(this, R.id.map);
		map.demoMapOrigin();
		
		googleApi = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_LOGIN)
        .build();
		
		gplusLoginButton = (SignInButton) findViewById(R.id.gplusLogin);
		gplusLoginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if(!googleApi.isConnecting())
				{
				    googleSignInClicked = true;
				    resolveSignInErrors();
				}
			}
		});
		
		if (savedInstanceState == null) {
	        // Add the fragment on initial activity setup
	        fbLoginFragment = new FacebookLoginFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(android.R.id.content, fbLoginFragment)
	        .commit();
	    } else {
	        // Or set the fragment from restored state info
	    	fbLoginFragment = (FacebookLoginFragment) getSupportFragmentManager()
	        .findFragmentById(android.R.id.content);
	    }
	}
	
	public PingMap getMap() { return map; }
	
	protected void onStart()
	{
		super.onStart();
		googleApi.connect();
	}
	
	protected void onStop()
	{
	    super.onStop();
	    if (googleApi.isConnected())
	      googleApi.disconnect();
	}
	
	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInErrors()
	{
	  if (googleConnectionResult.hasResolution())
	  {
	    try {
	      intentInProgress = true;
	      googleConnectionResult.startResolutionForResult(this, GPLUS_REQUEST_CODE_SIGNIN);
	    } catch (SendIntentException e) {
	      // The intent was canceled before it was sent.  Return to the default
	      // state and attempt to connect to get an updated ConnectionResult.
	      intentInProgress = false;
	      googleApi.connect();
	    }
	  }
	}

	@Override
	public void onConnectionFailed(ConnectionResult result)
	{
		if (!intentInProgress)
		{
		    // Store the ConnectionResult so that we can use it later when the user clicks
		    // 'sign-in'.
		    googleConnectionResult = result;

		    if (googleSignInClicked) {
		      // The user has already clicked 'sign-in' so we attempt to resolve all
		      // errors until the user is signed in, or they cancel.
		    	resolveSignInErrors();
		    }
		}
	}

	@Override
	public void onConnected(Bundle connectionHint)
	{
		final Context context = this.getApplicationContext();
		AsyncTask task = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params)
			{
				String scope = "oauth2:" + Scopes.PLUS_LOGIN;
				try {
					String token = GoogleAuthUtil.getToken(context, Plus.AccountApi.getAccountName(googleApi), scope);
					System.out.println(token);
				} catch (UserRecoverableAuthException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (GoogleAuthException e) {
					e.printStackTrace();
				}
		    return null;
			}
		};
		task.execute((Void) null);
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}
	
	protected void onActivityResult(int requestCode, int responseCode, Intent intent)
	{
		if (requestCode == GPLUS_REQUEST_CODE_SIGNIN)
		{
			intentInProgress = false;
			if (!googleApi.isConnecting())
			  googleApi.connect();
		}
	}
}
