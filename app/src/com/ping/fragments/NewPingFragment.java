package com.ping.fragments;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.ping.MainActivity;
import com.ping.R;
import com.ping.interfaces.PingInterface;
import com.ping.models.EncodedBitmap;
import com.ping.models.Ping;
import com.ping.util.FontTools;
import com.ping.util.PingApi;
import com.ping.util.PingPrefs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class NewPingFragment extends Fragment
{	
	public static final String TAG = NewPingFragment.class.getSimpleName();
	
	private final NewPingFragment fragment = this;
	private PingInterface dataPasser;
	private PingApi pingApi;
	private PingPrefs prefs;
	
	private EditText title;
	private EditText duration;
	private EditText address;
	private EditText message;
	private ImageButton addImageButton;
	private ImageView addedImage;
	private Button submitButton;
	
	private static final int CAMERA_PHOTO = 0;
	
	public static final String LATLNG_INCLUDED = "latlng_included";
	public static final String BUNDLE_LATLNG = "bundle_latlng";
	
	private Ping ping = new Ping();
	
	public static NewPingFragment newInstance()
	{
		return new NewPingFragment();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_newping, container, false);
		
		title = (EditText) view.findViewById(R.id.title);
		//duration = (EditText) view.findViewById(R.id.duration);
		address = (EditText) view.findViewById(R.id.address);
		message = (EditText) view.findViewById(R.id.message);
		submitButton = (Button) view.findViewById(R.id.submitPingButton);
		addedImage = (ImageView) view.findViewById(R.id.addedImage);
		addImageButton = (ImageButton) view.findViewById(R.id.addImageButton);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		FontTools.applyFont(getActivity(), getActivity().findViewById(R.id.root));
		prefs = PingPrefs.getInstance(getActivity());
		pingApi = PingApi.getInstance();
		
		final Bundle bundle = getArguments();
		final boolean includedLatLng = bundle.getBoolean(LATLNG_INCLUDED);
		
		addImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile()));
				startActivityForResult(i, CAMERA_PHOTO);
			}
		});
		
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				ping.setTitle(title.getText().toString());
				ping.setMessage(message.getText().toString());
				ping.setAddress(address.getText().toString());
				ping.setCreatorId(prefs.getCurrentUser().getId());
				//ping.setAuthorName("TODO. GET USER'S NAME FROM BACKEND. GET USER MODEL WHEN FIRST SIGNING IN!");
				
				if(!includedLatLng)
				{
					Geocoder gc = new Geocoder(fragment.getActivity().getBaseContext());
					try {
						List<Address> list = gc.getFromLocationName(address.getText().toString(), 1);
						LatLng loc = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());
						
						if(loc != null)
						{
							ping.setLocation(loc);
							postNewPing(ping);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else
				{
					LatLng loc = bundle.getParcelable(BUNDLE_LATLNG);
					ping.setLocation(loc);
					postNewPing(ping);
				}
			}
		});
	}
	
	private void postNewPing(final Ping ping)
	{
		pingApi.postNewPing(ping, new FutureCallback<Response<JsonObject>>() {
			@Override
			public void onCompleted(Exception e, Response<JsonObject> response)
			{
				JsonObject jsonResponse = response.getResult().getAsJsonObject(PingApi.RESPONSE);
				ping.setId(jsonResponse.get(Ping.ID).getAsInt());
				
				JsonArray imageArray = jsonResponse.get(Ping.IMAGES).getAsJsonArray();
				if(imageArray.size() > 0)
				{
					JsonObject imageData = imageArray.get(0).getAsJsonObject();
					
					ping.setImageUrlThumb(imageData.get(Ping.IMAGE_URL_THUMB).getAsString());
					ping.setImageUrlSmall(imageData.get(Ping.IMAGE_URL_SMALL).getAsString());
					ping.setImageUrlLarge(imageData.get(Ping.IMAGE_URL_LARGE).getAsString());
				}
				
				Bundle b = new Bundle();
				b.putInt(MainActivity.BUNDLE_ACTION, MainActivity.Actions.NEW_PING);
				b.putParcelable(MainActivity.BUNDLE_DATA, ping);
				passData(b);
				getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
			}
		});
	}
	
	private File getTempFile()
	{
		return new File(Environment.getExternalStorageDirectory(), "pingimage.jpg");
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
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_PHOTO)
		{
			File tempFile = getTempFile();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;
			Bitmap bmp = BitmapFactory.decodeFile(tempFile.getPath(), options);
			if (bmp != null)
			{
				Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.pictureAdded), Toast.LENGTH_SHORT).show();
				ping.setImage(new EncodedBitmap(bmp));
				addedImage.setImageBitmap(bmp);
				addedImage.setVisibility(View.VISIBLE);
			}
			else
				Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.pictureNotAdded), Toast.LENGTH_SHORT).show();
			tempFile.delete();			
		}
	}
}
