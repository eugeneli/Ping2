package com.ping.fragments;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.ping.MainActivity;
import com.ping.R;
import com.ping.interfaces.PingInterface;
import com.ping.models.Ping;
import com.ping.util.FontTools;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class NewPingFragment extends Fragment
{	
	public static final String TAG = NewPingFragment.class.getSimpleName();
	private final NewPingFragment fragment = this;
	private PingInterface dataPasser;
	
	private EditText title;
	private EditText duration;
	private EditText address;
	private EditText message;
	private Button submitButton;
	
	public static NewPingFragment newInstance() 
	{
		NewPingFragment frag = new NewPingFragment();
	    return frag;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		FontTools.applyFont(getActivity(), getActivity().findViewById(R.id.root));
		
		title = (EditText) getActivity().findViewById(R.id.title);
		duration = (EditText) getActivity().findViewById(R.id.duration);
		address = (EditText) getActivity().findViewById(R.id.address);
		message = (EditText) getActivity().findViewById(R.id.message);
		submitButton = (Button) getActivity().findViewById(R.id.submitPingButton);
		
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				Ping ping = new Ping();
				ping.setTitle(title.getText().toString());
				ping.setMessage(message.getText().toString());
				
				Geocoder gc = new Geocoder(fragment.getActivity().getBaseContext());
				try {
					List<Address> list = gc.getFromLocationName(address.getText().toString(), 1);
					LatLng loc = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());
					ping.setLocation(loc);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				    
				Bundle b = new Bundle();
				b.putInt(MainActivity.BUNDLE_ACTION, MainActivity.Actions.NEW_PING);
				b.putParcelable(MainActivity.BUNDLE_DATA, ping);
				passData(b);
				getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
			}
		});
	}
	
	public void passData(Bundle data)
	{
	    dataPasser.onFragmentResult(data);
	}
	
	@Override
	public void onAttach(Activity a)
	{
	    super.onAttach(a);
	    dataPasser = (PingInterface) a;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_newping, container, false);
    }
}
