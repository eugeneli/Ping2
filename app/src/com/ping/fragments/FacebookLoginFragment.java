package com.ping.fragments;

import com.ping.R;
import com.ping.util.FontTools;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FacebookLoginFragment extends Fragment
{	
	public static final String TAG = NewPingFragment.class.getSimpleName();
	
	public static FacebookLoginFragment newInstance() 
	{
		FacebookLoginFragment frag = new FacebookLoginFragment();
	    return frag;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		FontTools.applyFont(getActivity(), getActivity().findViewById(R.id.root));
		
	}
	
	
	@Override
	public void onAttach(Activity a)
	{
	    super.onAttach(a);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fblogin, container, false);
    }
}
