package com.ping.fragments;

import com.ping.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class MainFragment extends Fragment
{	
	public static final String TAG = MainFragment.class.getSimpleName();
	
	private ImageButton addPingButton;
	
	public static MainFragment newInstance() 
	{
		MainFragment frag = new MainFragment();
	    return frag;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		addPingButton = (ImageButton) getActivity().findViewById(R.id.addButton);
		
		setupListeners();
	}
	
	private void setupListeners()
	{
		addPingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down,R.anim.slide_in_up, R.anim.slide_out_down);

				NewPingFragment newFragment = NewPingFragment.newInstance();

				ft.replace(R.id.fragmentContainer, newFragment, "loginFragment");

				ft.addToBackStack(NewPingFragment.TAG).commit();
			}
		});
	}

}
