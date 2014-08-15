package com.ping.fragments;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.ping.R;
import com.ping.interfaces.Interactable;
import com.ping.interfaces.Renderable;
import com.ping.models.Ping;
import com.ping.models.User;
import com.ping.util.FontTools;
import com.ping.util.HtmlBuilder;
import com.ping.util.PingApi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

public class PingFragment extends DialogFragment implements Renderable, Interactable
{
	public static final String TAG = NewPingFragment.class.getSimpleName();
	
	private PingApi pingApi;
	private Bundle bundle;
	private Context context;
	
	private Ping ping;
	private User pingCreator;
	
	private TextView pingTitle;
	private TextView pingAuthor;
	private TextView pingMessage;
	private TextView pingAddress;
	private WebView pingImageWebView;
	
	public static final String PING_DATA = "ping_data";
	
	public static PingFragment newInstance(Ping selectedPing) 
	{
		PingFragment pf = new PingFragment();
		Bundle b = new Bundle();
		b.putParcelable(PingFragment.PING_DATA, selectedPing);
		pf.setArguments(b);
		return pf;
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
		pingCreator = new User();
		
		ping = bundle.getParcelable(PING_DATA);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_ping, container, false);
		FontTools.applyFont(context, view.findViewById(R.id.root));
		
		pingTitle = (TextView) view.findViewById(R.id.pingTitle);
		pingAuthor = (TextView) view.findViewById(R.id.pingAuthor);
		pingMessage = (TextView) view.findViewById(R.id.pingMessage);
		pingAddress = (TextView) view.findViewById(R.id.pingAddress);
		pingImageWebView = (WebView) view.findViewById(R.id.pingImageWebView);
		
		pingApi.getUserById(ping.getCreatorId(), new FutureCallback<Response<JsonObject>>() {
			@Override
			public void onCompleted(Exception e, Response<JsonObject> response)
			{
				if(PingApi.validResponse(response, context, getResources()))
				{
					JsonObject jsonResponse = response.getResult().getAsJsonObject(PingApi.RESPONSE);
					
					pingCreator.fromJson(jsonResponse);
					pingAuthor.setText(pingCreator.getFullName());
				}
			}
		});
		
		renderView();
		attachListeners();
		
		return view;
	}
	
	@Override
	public void attachListeners()
	{
		pingAuthor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				dismiss();
				UserFragment.newInstance(pingCreator.getId()).show(((FragmentActivity)context).getSupportFragmentManager(), TAG);
			}
		});
	}
	
	@Override
	public void renderView()
	{
		pingTitle.setText(ping.getTitle());
		pingMessage.setText(ping.getMessage());
		//pingAddress.setText("near " + ping.getAddress());
		
		if(ping.getImageUrlSmall() != null)
			pingImageWebView.loadData(HtmlBuilder.buildImageHtml(ping.getImageUrlSmall()), "text/html; charset=UTF-8", null);
		else
			pingImageWebView.setVisibility(View.GONE);
	}
}
