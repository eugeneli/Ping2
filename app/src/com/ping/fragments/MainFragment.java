package com.ping.fragments;

import com.ping.R;
import com.ping.interfaces.Interactable;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainFragment extends Fragment implements Interactable
{	
	public static final String TAG = MainFragment.class.getSimpleName();
	
	private Context context;
	
	private ImageButton addPingButton;
	private ImageButton settingsButton;
	
	public static MainFragment newInstance() 
	{
		return new MainFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		addPingButton = (ImageButton) view.findViewById(R.id.addButton);
		settingsButton = (ImageButton) view.findViewById(R.id.settingsButton);
		
		attachListeners();
		
		return view;
	}
	
	@Override
	public void attachListeners()
	{
		settingsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				SettingsFragment.newInstance().show(getActivity().getSupportFragmentManager(), TAG);
			}
		});
		
		addPingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				NewPingFragment.newInstance(false, null).show(getActivity().getSupportFragmentManager(), TAG);
			}
		});
	}
}
