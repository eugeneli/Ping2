package com.ping.test;

import com.ping.LoginActivity;
import com.ping.fragments.LoginFragment;
import com.ping.models.PingMap;
import com.ping.R;

import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity>
{
	private LoginActivity activity;
	private PingMap map;
	private LoginFragment loginFragment;
	
	FragmentManager fragmentManager;
	
	public LoginActivityTest()
	{
		super(LoginActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		setActivityInitialTouchMode(false);
		
		activity = getActivity();
		map = activity.getMap();
		fragmentManager = activity.getSupportFragmentManager();

		loginFragment = (LoginFragment) fragmentManager.findFragmentById(R.id.loginFragment);
	}
	
	public void testPreConditions()
	{
		assertNotNull(activity);
		assertNotNull(map);
	}
	
	public void testFragment() throws Exception
	{
        assertNotNull(loginFragment);

    }
	
}
