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
import com.ping.models.User;
import com.ping.util.FontTools;
import com.ping.util.PingApi;
import com.ping.util.PingPrefs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class NewPingFragment extends DialogFragment
{	
	public static final String TAG = NewPingFragment.class.getSimpleName();
	
	private final NewPingFragment fragment = this;
	private PingInterface dataPasser;
	private PingApi pingApi;
	private PingPrefs prefs;
	private FragmentActivity context;
	private Bundle bundle;
	
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
	
	public static NewPingFragment newInstance(boolean locIncluded, LatLng loc)
	{
		NewPingFragment npf = new NewPingFragment();
		Bundle bundle = new Bundle();
		
		if(locIncluded)
		{
			bundle.putBoolean(NewPingFragment.LATLNG_INCLUDED, true);
			bundle.putParcelable(NewPingFragment.BUNDLE_LATLNG, loc);
		}
		else
			bundle.putBoolean(NewPingFragment.LATLNG_INCLUDED, false);
		
		npf.setArguments(bundle);
		return npf;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
	    final Dialog dialog = super.onCreateDialog(savedInstanceState);
	    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
	    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	    return dialog;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = getActivity();
		prefs = PingPrefs.getInstance(context);
		pingApi = PingApi.getInstance();
		bundle = getArguments();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_newping, container, false);
		FontTools.applyFont(context, view.findViewById(R.id.root));
		
		title = (EditText) view.findViewById(R.id.title);
		//duration = (EditText) view.findViewById(R.id.duration);
		address = (EditText) view.findViewById(R.id.address);
		message = (EditText) view.findViewById(R.id.message);
		submitButton = (Button) view.findViewById(R.id.submitPingButton);
		addedImage = (ImageView) view.findViewById(R.id.addedImage);
		addImageButton = (ImageButton) view.findViewById(R.id.addImageButton);
		
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
				User user = prefs.getCurrentUser();
				ping.setTitle(title.getText().toString());
				ping.setMessage(message.getText().toString());
				ping.setAddress(address.getText().toString());
				ping.setCreatorId(user.getId());
				//ping.setAuthorName(user.getFullName());
				
				if(!includedLatLng)
				{
					Geocoder gc = new Geocoder(context);
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
		
		return view;
	}
	
	private void postNewPing(final Ping ping)
	{
		pingApi.postNewPing(ping, new FutureCallback<Response<JsonObject>>() {
			@Override
			public void onCompleted(Exception e, Response<JsonObject> response)
			{
				if(PingApi.validResponse(response, context, getResources()))
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
					NewPingFragment.this.dismiss();
					
					//context.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
				}
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
			AsyncTask<Void, Void, Bitmap> loadBitmapTask = new AsyncTask<Void, Void, Bitmap>() {
				private Bitmap bmp;
				
				@Override
				protected Bitmap doInBackground(Void... params)
				{
					File tempFile = getTempFile();
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 4;
					bmp = BitmapFactory.decodeFile(tempFile.getPath(), options);
					tempFile.delete();
					
					return bmp;
				}
				
				@Override
			    protected void onPostExecute(Bitmap bitmap)
				{
					if (bmp != null)
					{
						Toast.makeText(context, context.getResources().getString(R.string.pictureAdded), Toast.LENGTH_SHORT).show();
						ping.setImage(new EncodedBitmap(bmp));
						addedImage.setImageBitmap(bmp);
						addedImage.setVisibility(View.VISIBLE);
					}
					else
						Toast.makeText(context, context.getResources().getString(R.string.pictureNotAdded), Toast.LENGTH_SHORT).show();
			    }
			};
			loadBitmapTask.execute();
		}
	}
}
