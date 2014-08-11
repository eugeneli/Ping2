package com.ping;

import java.io.IOException;
import java.util.Arrays;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import com.ping.models.PingMap;
import com.ping.util.FontTools;
import com.ping.util.PingApi;
import com.ping.util.PingPrefs;

import android.support.v4.app.FragmentActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LoginActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener
{
	public static final String TAG = LoginActivity.class.getSimpleName();
	
	private PingMap map;
	private PingApi pingApi;
	private PingPrefs prefs;
	private Resources resources;
	private ProgressDialog progress;
	private LinearLayout oAuthContainer;
	
	public static final String GOOGLE_PLUS = "google";
	public static final String FACEBOOK = "facebook";
	
	//GooglePlus Login variables
	private SignInButton gplusLoginButton;
	private static final int GPLUS_REQUEST_CODE_SIGNIN = 0;
	private GoogleApiClient googleApi;
	private boolean intentInProgress;
	private boolean googleSignInClicked;
	private ConnectionResult googleConnectionResult;
	
	//Facebook Login variables
	private UiLifecycleHelper uiHelper;
	private LoginButton loginButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		FontTools.applyFont(this, findViewById(R.id.root));
		
		oAuthContainer = (LinearLayout) findViewById(R.id.oauthContainer);
		
		pingApi = PingApi.getInstance();
		prefs = PingPrefs.getInstance(this);
		map = new PingMap(this, R.id.map);
		map.demoMapOrigin();
		
		resources = getResources();
		progress = new ProgressDialog(this);
		progress.setIndeterminate(true);

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
					progress.setMessage(resources.getString(R.string.gplusLoadingMessage));
					progress.show();
					
				    googleSignInClicked = true;
				    resolveSignInErrors();
				}
			}
		});
		
		loginButton = (LoginButton) findViewById(R.id.fbLogin);
		loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
		uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user)
            {
            	Session session = Session.getActiveSession();
            	if(session != null && session.isOpened())
            	{
            		progress.setMessage(resources.getString(R.string.fbLoadingMessage));
        			progress.show();
            		String token = session.getAccessToken();
                	loginWithOAuth(FACEBOOK, token);
            	}
            }
        });
	}
	
	public void loginWithOAuth(String oAuthProvider, final String oAuthAccessToken)
	{
		final Context context = this;
		oAuthContainer.setVisibility(View.GONE);
		if(oAuthProvider.equals(GOOGLE_PLUS))
		{
			Log.d(TAG, oAuthProvider + " - " + oAuthAccessToken);
			pingApi.userOAuthLogin(oAuthProvider, oAuthAccessToken, new FutureCallback<Response<JsonObject>>(){
				@Override
				public void onCompleted(Exception e, Response<JsonObject> response)
				{
					if(response.getHeaders().getResponseCode() == PingApi.HTTP_SUCCESS)
					{
						JsonObject jsonResponse = response.getResult().getAsJsonObject(PingApi.RESPONSE);
						String authToken = jsonResponse.get(PingApi.JSON_AUTHTOKEN).getAsString();
						prefs.setAuthToken(authToken);
						pingApi.setAuthToken(authToken);
						
						Log.d(TAG, authToken);

						Intent intent = new Intent(context, MainActivity.class);
						startActivity(intent);
						
						finish();
					}
				}
			});
		}
		else if(oAuthProvider.equals(FACEBOOK))
		{
			Log.d(TAG, oAuthProvider + " - " + oAuthAccessToken);
			pingApi.userOAuthLogin(oAuthProvider, oAuthAccessToken, new FutureCallback<Response<JsonObject>>(){
				@Override
				public void onCompleted(Exception e, Response<JsonObject> response)
				{
					try {
						if(response.getHeaders().getResponseCode() == PingApi.HTTP_SUCCESS)
						{
							JsonObject jsonResponse = response.getResult().getAsJsonObject(PingApi.RESPONSE);
							String authToken = jsonResponse.get(PingApi.JSON_AUTHTOKEN).getAsString();
							prefs.setAuthToken(authToken);
							pingApi.setAuthToken(authToken);
							
							Log.d(TAG, authToken);
							Intent intent = new Intent(context, MainActivity.class);
							startActivity(intent);
							
							finish();
						}
					}
					catch(Exception ex)
					{
						Toast.makeText(getBaseContext(), "Couldn't connect to Ping servers", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}
	
	public void loginWithPing(String username, String password)
	{
		//Normal username/password login
	}
	
	/********************
	 * FACEBOOK OAUTH 
	 */
	private Session.StatusCallback callback = new Session.StatusCallback()
	{
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
    
    private void onSessionStateChange(Session session, SessionState state, Exception exception)
	{
	    if (state.isOpened()){
	        Log.i(TAG, "FB logged in...");
	    } else if (state.isClosed()) {
	        Log.i(TAG, "FB logged out...");
	    }
	}
    
    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        if (progress != null)
        {
        	progress.dismiss();
        	progress = null;
        }
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	
    }

	
	/********************
	 * GOOGLE OAUTH 
	 */
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
	  if (googleConnectionResult != null && googleConnectionResult.hasResolution())
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
		AsyncTask<Object, Void, Object> task = new AsyncTask<Object, Void, Object>() {
			@Override
			protected Object doInBackground(Object... params)
			{
				String serverClientId = resources.getString(R.string.serverClientId);
				String scope = "audience:server:client_id:"+ serverClientId;// +":api_scope:" + Scopes.PLUS_LOGIN + " https://www.googleapis.com/auth/plus.profile.emails.read";
				
				try {
					String token = GoogleAuthUtil.getToken(context, Plus.AccountApi.getAccountName(googleApi), scope);
					loginWithOAuth(GOOGLE_PLUS, token);
				} catch (UserRecoverableAuthException e) {
					startActivityForResult(e.getIntent(), 0);
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
		else
			uiHelper.onActivityResult(requestCode, responseCode, intent);
	}
}
