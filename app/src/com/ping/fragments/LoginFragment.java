package com.ping.fragments;

import com.ping.MainActivity;
import com.ping.R;
import com.ping.util.FontTools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginFragment extends Fragment
{
	public static final String TAG = MainActivity.class.getSimpleName();
	
	private TextView loginButton;
	private TextView registerButton;
	private EditText username;
	private View separator;
	private EditText password;
	private EditText newUsername;
	private EditText newPassword;
	private EditText newEmail;
	private Button submitRegisterButton;
	
	public static LoginFragment newInstance() 
	{
	    LoginFragment myFragment = new LoginFragment();
	    return myFragment;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        
        loginButton = (TextView) view.findViewById(R.id.login);
		registerButton = (TextView) view.findViewById(R.id.register);
		username = (EditText) view.findViewById(R.id.username);
		separator = view.findViewById(R.id.separator);
		password = (EditText) view.findViewById(R.id.password);
		
		newUsername = (EditText) view.findViewById(R.id.newUsername);
		newPassword = (EditText) view.findViewById(R.id.newPassword);
		newEmail = (EditText) view.findViewById(R.id.newEmail);
		submitRegisterButton = (Button) view.findViewById(R.id.submitRegister);
		
		setupListeners(getActivity());
        
        return view;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		FontTools.applyFont(getActivity(), getActivity().findViewById(R.id.root));
	}
	
	private void setupListeners(final Activity activity)
	{
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				loginButton.setVisibility(View.GONE);
				registerButton.setVisibility(View.GONE);
				username.setVisibility(View.VISIBLE);
				separator.setVisibility(View.VISIBLE);
				password.setVisibility(View.VISIBLE);
			}
		});
		
		registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				loginButton.setVisibility(View.GONE);
				registerButton.setVisibility(View.GONE);
				newUsername.setVisibility(View.VISIBLE);
				newPassword.setVisibility(View.VISIBLE);
				newEmail.setVisibility(View.VISIBLE);
				submitRegisterButton.setVisibility(View.VISIBLE);
			}
		});
		
		password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if(actionId == EditorInfo.IME_ACTION_DONE || event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
				{
					Intent intent = new Intent(activity, MainActivity.class);
					startActivity(intent);
					return true;
				}
				return false;
			}
		});
		
		submitRegisterButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(activity, MainActivity.class);
				startActivity(intent);
			}
		});
	}
	
	
	
	
	
}
