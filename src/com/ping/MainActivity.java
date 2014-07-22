package com.ping;

import com.ping.fragments.NewPingFragment;
import com.ping.interfaces.PingInterface;
import com.ping.models.Ping;
import com.ping.models.PingMap;
import com.ping.util.FontTools;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends FragmentActivity implements PingInterface
{	
	public static final String TAG = MainActivity.class.getSimpleName();
	
	private PingMap map;
	private Button addPingButton;
	
	public static final String BUNDLE_ACTION = "bundle_action";
	public static final String BUNDLE_DATA = "bundle_data";
	
	public static class Actions
	{
		public static final int NEW_PING = 0;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		FontTools.applyFont(this, findViewById(R.id.root));
		
		addPingButton = (Button) findViewById(R.id.addButton);
		
		map = new PingMap(this, R.id.map);
		map.demoMapMarkers();
		
		setupListeners();
	}
	
	private void setupListeners()
	{
		addPingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down,R.anim.slide_in_up, R.anim.slide_out_down);

				NewPingFragment newFragment = NewPingFragment.newInstance();

				ft.replace(R.id.fragmentContainer, newFragment, "loginFragment");

				// Start the animated transition.
				ft.addToBackStack("A TAG GOES HERE").commit();
			}
		});
	}

	@Override
	public void onFragmentResult(Bundle bundle)
	{
		switch(bundle.getInt(BUNDLE_ACTION))
		{
			case Actions.NEW_PING:
				Ping ping = bundle.getParcelable(BUNDLE_DATA);
				map.moveCamera(ping.getLocation(), 13);
				map.addMarker(ping);
				break;
		}
		
	}
}
