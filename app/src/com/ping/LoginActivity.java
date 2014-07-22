package com.ping;

import com.ping.models.PingMap;
import com.ping.util.FontTools;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Window;

public class LoginActivity extends FragmentActivity
{	
	private PingMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		FontTools.applyFont(this, findViewById(R.id.root));
		
		map = new PingMap(this, R.id.map);
		map.demoMapOrigin();
	}
	
	public PingMap getMap() { return map; }
}
