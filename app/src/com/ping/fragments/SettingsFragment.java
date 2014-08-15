package com.ping.fragments;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.ping.LoginActivity;
import com.ping.R;
import com.ping.interfaces.Interactable;
import com.ping.util.FontTools;
import com.ping.util.PingApi;
import com.ping.util.PingPrefs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends DialogFragment implements Interactable
{	
	public static final String TAG = NewPingFragment.class.getSimpleName();
	
	private PingApi pingApi;
	private PingPrefs prefs;
	private Context context;
	
	private TextView profile;
	private TextView logout;
	
	public static SettingsFragment newInstance() 
	{
		return new SettingsFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = getActivity();
		prefs = PingPrefs.getInstance(context);
		pingApi = PingApi.getInstance();
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		final Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		FontTools.applyFont(context, view.findViewById(R.id.root));
		
		profile = (TextView) view.findViewById(R.id.profile);
		logout = (TextView) view.findViewById(R.id.logout);
		
		attachListeners();
		
		return view;
	}

	public void attachListeners()
	{
		profile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				dismiss();
				UserFragment.newInstance(prefs.getCurrentUser().getId()).show(((FragmentActivity)context).getSupportFragmentManager(), TAG);
			}
		});
		
		logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				pingApi.userLogout(new FutureCallback<Response<JsonObject>>() {
					@Override
					public void onCompleted(Exception e, Response<JsonObject> response)
					{
						if(PingApi.validResponse(response, context, getResources()))
						{
							prefs.setAuthToken(null);
							Toast.makeText(context, getResources().getString(R.string.loggedOut), Toast.LENGTH_LONG).show();
							Intent intent = new Intent(context, LoginActivity.class);
							startActivity(intent);
						}
					}
				});
			}
		});
	}
}
