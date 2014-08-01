package com.ping.fragments;

import com.ping.R;
import com.ping.models.Ping;
import com.ping.util.FontTools;

import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PingFragment extends Fragment
{	
	public static final String TAG = NewPingFragment.class.getSimpleName();
	
	private TextView pingTitle;
	private TextView pingAuthor;
	private ImageView pingImage;
	private TextView pingMessage;
	private TextView pingAddress;
	
	public static final String PING_DATA = "ping_data";
	
	public static PingFragment newInstance() 
	{
		PingFragment frag = new PingFragment();
	    return frag;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        View view = inflater.inflate(R.layout.fragment_ping, container, false);
        
        pingTitle = (TextView) view.findViewById(R.id.pingTitle);
        pingAuthor = (TextView) view.findViewById(R.id.pingAuthor);
        pingImage = (ImageView) view.findViewById(R.id.pingImage);
        pingMessage = (TextView) view.findViewById(R.id.pingMessage);
        pingAddress = (TextView) view.findViewById(R.id.pingAddress);
        
        return view;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		FontTools.applyFont(getActivity(), getActivity().findViewById(R.id.root));
		
		Bundle bundle = getArguments();
		Ping ping = bundle.getParcelable(PING_DATA);
		
		pingTitle.setText(ping.getTitle());
		pingAuthor.setText(ping.getAuthorName());
		pingMessage.setText(ping.getMessage());
		pingAddress.setText("near " + ping.getAddress());
	}
}
