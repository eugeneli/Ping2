package com.ping.fragments;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.ping.R;
import com.ping.interfaces.Renderable;
import com.ping.models.User;
import com.ping.util.FontTools;
import com.ping.util.PingApi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class UserFragment extends DialogFragment implements Renderable
{
	public static final String TAG = NewPingFragment.class.getSimpleName();
	
	private PingApi pingApi;
	private Bundle bundle;
	private Context context;
	
	private User user;
	
	private TextView username;
	private TextView userbio;
	
	public static final String USER_ID = "user_id";
	
	public static UserFragment newInstance(int userId) 
	{
		UserFragment frag = new UserFragment();
		Bundle b = new Bundle();
		b.putInt(UserFragment.USER_ID, userId);
		frag.setArguments(b);
		return frag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		final Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().getAttributes().windowAnimations = R.style.DialogZoomAnimation;
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = getActivity();
		pingApi = PingApi.getInstance();
		bundle = getArguments();
		user = new User();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_user, container, false);
		FontTools.applyFont(context, view.findViewById(R.id.root));
		
		username = (TextView) view.findViewById(R.id.username);
		userbio = (TextView) view.findViewById(R.id.userbio);
		
		int userId = bundle.getInt(USER_ID);
		
		pingApi.getUserById(userId, new FutureCallback<Response<JsonObject>>() {
			@Override
			public void onCompleted(Exception e, Response<JsonObject> response)
			{
				if(PingApi.validResponse(response, context, getResources()))
				{
					JsonObject jsonResponse = response.getResult().getAsJsonObject(PingApi.RESPONSE);
					
					user.fromJson(jsonResponse);
					renderView();
				}
			}
		});

		return view;
	}
	
	@Override
	public void renderView()
	{
		username.setText(user.getFullName());
		userbio.setText(user.getBio());
	}
}
