/*
 * Project done by Parthasarathy Krishnamurthy
 * z1729253
 * For Android Mobile Device Programming CSCI 522 Graduate Project
 * This application queries facebook
 * This app logs in using facebook credentials
 * The logged in user will be able to post a status/picture in his account
 * The logged in user can view his news feed
 * The logged in user can view all the wishes he received for his birthday
 * The logged in user can reply to each post(wish) or can thank them all
 */
package com.example.login;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.facebook.widget.ProfilePictureView;

public class MainActivity extends FragmentActivity {

	//Form variables, Permission variables, Other variables
	private LoginButton loginBtn;
	private Button postImageBtn;
	private Button updateStatusBtn;
	private Button accessWallPosts;
	private Button btnUpload;
	private Button btnCancel;
	private TextView userName;
	private UiLifecycleHelper uiHelper;
	private static final List<String> PUBLISH_PERMISSIONS = Arrays.asList("publish_actions","publish_stream");
	private static final List<String> READ_PERMISSIONS = Arrays.asList("read_stream","user_birthday");
	private String message = "";
	private static String loggedUser="";
	private static String loggedUserID="";
	private static String loggedUserBDay="";
	private Session userSession = null;
	ProfilePictureView ppvProfilePic;
	ImageView ivTemp;
	EditText etStatus;
	private Bitmap imgBitmap = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		uiHelper = new UiLifecycleHelper(this, statusCallback);
		uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		//Linking form elements to variables
		userName = (TextView) findViewById(R.id.user_name);
		ppvProfilePic=(ProfilePictureView)findViewById(R.id.userImage);
		etStatus=(EditText)findViewById(R.id.etStatus);
		ivTemp=(ImageView)findViewById(R.id.ivTemp);
		loginBtn = (LoginButton) findViewById(R.id.fb_login_button);
		//loginBtn.setReadPermissions(READ_PERMISSIONS);
		loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback() {
			@Override
			public void onUserInfoFetched(GraphUser user) { 
				if(isOnline()){
				if (user != null) {
					//getting user info
					loggedUser=user.getName();
					loggedUserID=user.getId();
					loggedUserBDay=user.getBirthday();
					userName.setText("Hello, " + user.getName());
					ppvProfilePic.setProfileId(user.getId());
					}				
				else {
					userName.setText("You are not logged in");
					ppvProfilePic.setProfileId(null);
					hideElements();
				}
				}
				else{
					Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
				}
			}
		});

		//Handling image post button click event
		postImageBtn = (Button) findViewById(R.id.post_image);
		postImageBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			    builder.setTitle("Choose an action");
			    builder.setMessage("The text entered as status will be the caption of your image");
			    builder.setPositiveButton("Take a new picture", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						// TODO Auto-generated method stub
						Intent capIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						if(capIntent.resolveActivity(getPackageManager())!=null)
						{
							startActivityForResult(capIntent,2);
						}
					}
				});
			    
			    builder.setNeutralButton("Choose from gallery", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						// TODO Auto-generated method stub
						Intent imgIntent = new Intent();
						imgIntent.setType("image/*");
						imgIntent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(imgIntent, 1);
					}
				});
			    
			    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
				AlertDialog alert=builder.create();
				alert.show();
			}
		});
		
		//Handling status update button click event
		updateStatusBtn = (Button) findViewById(R.id.update_status);
		updateStatusBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				message=etStatus.getText().toString();
				if(validateStatus(message))
				{
					postStatusMessage();	
				}
				else
				{
					Toast.makeText(MainActivity.this, "Enter valid text", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		//Handle wall posts button click event
		accessWallPosts = (Button)findViewById(R.id.access_wall);
		accessWallPosts.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if(isOnline()){
				Intent intentObj=new Intent(MainActivity.this,TimeLine.class);
				intentObj.putExtra("user", loggedUser);
				intentObj.putExtra("ID", loggedUserID);
				intentObj.putExtra("session", userSession);
				intentObj.putExtra("bDay", loggedUserBDay);
				userSession.requestNewReadPermissions(new NewPermissionsRequest(MainActivity.this, READ_PERMISSIONS));
				startActivity(intentObj);
				}
				else{
					Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
				}
			}
		});

		//Handle photo upload cancel button click event
		btnCancel=(Button)findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				hideElements();
				Toast.makeText(MainActivity.this, "Photo upload cancelled",Toast.LENGTH_SHORT).show();
			}
		});
		
		//Handle upload button click event
		btnUpload=(Button)findViewById(R.id.btnUpload);
		btnUpload.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				postImage(imgBitmap);
				hideElements();
			}
		});
		
		buttonsEnabled(false);
		hideElements();
	}
	
	//Function to check if net connection is available
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return netInfo != null && netInfo.isConnectedOrConnecting();
	}
	
	//Function to hide elements
	public void hideElements()
	{
		imgBitmap=null;
		ivTemp.setImageBitmap(imgBitmap);
		btnUpload.setVisibility(View.INVISIBLE);
		btnCancel.setVisibility(View.INVISIBLE);
	}
	
	//Funtion to make elements visible
	public void showElements()
	{
		btnUpload.setVisibility(View.VISIBLE);
		btnCancel.setVisibility(View.VISIBLE);
	}
	
	//Function to check if the status has valid text
	public Boolean validateStatus(String status)
	{
		if(etStatus.getText().toString().trim().equalsIgnoreCase(""))
		{
			return false;
		}
		return true;
	}
	
	//Status call back handle
	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			userSession = session; 
			if (state.isOpened()) {
				buttonsEnabled(true);
				Log.d("FacebookSampleActivity", "Facebook session opened");
			} else if (state.isClosed()) {
				buttonsEnabled(false);
				Log.d("FacebookSampleActivity", "Facebook session closed");
			}
		}
	};
	
	//Function to enable button only if user is logged in
	public void buttonsEnabled(boolean isEnabled) {
		postImageBtn.setEnabled(isEnabled);
		updateStatusBtn.setEnabled(isEnabled);
		accessWallPosts.setEnabled(isEnabled);
	}

	//Function to handle photo upload call back
	Request.Callback uploadPhotoRequestCallback = new Request.Callback() {
	    @Override
	    public void onCompleted(Response response) {
	    	etStatus.setText("");
			Toast.makeText(MainActivity.this,
					"Photo uploaded successfully",
					Toast.LENGTH_LONG).show();
		}
	};

	//Function to post image
	public void postImage(Bitmap img){
		if(isOnline()){
			Request request = Request.newUploadPhotoRequest(Session.getActiveSession(), img, uploadPhotoRequestCallback);
			Bundle params=request.getParameters();
			if(validateStatus(etStatus.getText().toString())){
			params.putString("caption", etStatus.getText().toString());
			request.setParameters(params);
			}
			request.executeAsync();	
		}
		else{
			Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
		}
	}
	
	//Function to post status message
	public void postStatusMessage() 
	{	
		if (checkPermissions()) 
		{
			if(isOnline()){
			Request request = Request.newStatusUpdateRequest(
					Session.getActiveSession(), message,
					new Request.Callback() {
					@Override
					public void onCompleted(Response response) {
					if (response.getError() == null)
					Toast.makeText(MainActivity.this,"Status updated successfully",Toast.LENGTH_LONG).show();
					etStatus.setText("");
					}
					});
					request.executeAsync();
				}
			else{
				Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
			}	
				}
				else {
					requestPermissions();
				}
		}

	//Funtion to check if user has permissions
	public boolean checkPermissions() {
		Toast.makeText(this, "check", Toast.LENGTH_SHORT).show();
		Session s = Session.getActiveSession();
		if (s != null) {
			return ( s.getPermissions().contains("publish_actions"));
		} else
			return false;
	}

	//Function to request permission
	public void requestPermissions() {
		Session s = Session.getActiveSession();
		if (s != null){
			s.requestNewPublishPermissions(new Session.NewPermissionsRequest(
					this, PUBLISH_PERMISSIONS));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		buttonsEnabled(Session.getActiveSession().isOpened());
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		hideElements();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
		hideElements();
	}

	//Handling intent activity result
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode==1)
		{
			if(resultCode == RESULT_OK)
			{
				Uri imgUri=data.getData();
				try
				{
					imgBitmap  = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
				} 
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else if(requestCode==2)
		{
			if(resultCode==RESULT_OK)
			{
				Bundle extras = data.getExtras();
				imgBitmap = (Bitmap)extras.get("data");
			}
		}
		if(imgBitmap!=null)
		{
			ivTemp.setImageBitmap(imgBitmap);
			showElements();
		}
		else
		{
			ivTemp.setImageBitmap(null);
			hideElements();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	//Handling menu click
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		else if(id==R.id.newsFeed)
		{
			if(Session.getActiveSession().isOpened()){
				Intent newsIntent=new Intent(MainActivity.this,WallPosts.class);
				newsIntent.putExtra("user", loggedUser);
				newsIntent.putExtra("ID", loggedUserID);
				newsIntent.putExtra("session", userSession);
				startActivity(newsIntent);	
			}
			else{
				Toast.makeText(MainActivity.this, "Please login to view your feed", Toast.LENGTH_SHORT).show();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		uiHelper.onSaveInstanceState(savedState);
	}
}