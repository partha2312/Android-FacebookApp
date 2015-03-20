package com.example.login;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Parser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.model.GraphObject;
//Function to get feeds for timeline
public class WallPosts extends ActionBarActivity
{
	private Session userSession = null;
	private ListView list = null ;
	private String user="";
	private String id="";
	final List<String> PERMS =  new ArrayList<String>();
	private WallAdapter wallObj;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wall_posts);

		if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
		
		list=(ListView)findViewById(R.id.list);
		wallObj=new WallAdapter(null);
		
		Intent mainIntent=getIntent();
		user=mainIntent.getStringExtra("user");
		id=mainIntent.getStringExtra("ID");
		userSession = (Session) mainIntent.getSerializableExtra("session");
		Toast.makeText(WallPosts.this, "Generating news feed", Toast.LENGTH_LONG).show();
		
		Session currentSession = Session.getActiveSession();
		List<String> permissions = currentSession.getPermissions();
		
		if(!permissions.contains("read_stream")){
			currentSession.requestNewReadPermissions(new NewPermissionsRequest(this, "read_stream"));
		}
		populateNewsFeed();
		populateList();
	}

	private void populateList()
	{
		list.setAdapter(wallObj);
	}
	
	//Function to retrieve feeds for timeline
	private void populateNewsFeed()
	{
		try
		{
		Session sess=Session.getActiveSession();
		if(sess.getState().isOpened()){
		Bundle params = new Bundle();
		params.putString("limit", "25");
		new Request(
			    userSession,
			    "/me/home",
			    params,
			    HttpMethod.GET,
			    new Request.Callback() {
			        public void onCompleted(Response response) {
			        	JSONArray json = null;
			        	JSONObject current = null;
			        	try
						{
			        		json = (JSONArray) response.getGraphObject().getProperty("data");
			        		for(int i=0; i<25; i++){
			        			String name="",
					        			timeStamp="",
					        			statusMessage="",
					        			url="", 
					        			id="", 
					        			feedImage="";
			        			try
								{
				        			current = json.getJSONObject(i);
				        			if(current.has("picture")){
				        				feedImage=current.getString("picture").toString();
				        			}
				        			if(current.has("message")){
				        				statusMessage=current.get("message").toString();
				        			}
				        			if(current.has("updated_time")){
				        				timeStamp=current.get("updated_time").toString();
				        			}
				        			else{
				        				timeStamp=current.get("created_time").toString();
				        			}
				        			if(current.has("link")){
				        				url=current.get("link").toString();
				        			}
				        			else if(current.has("actions")){
				        				JSONArray fromObj=current.getJSONArray("actions");
				        				url = fromObj.getString(1);
				        			}
				        			JSONObject fromObj = current.getJSONObject("from");
				        			name = fromObj.getString("name");
									id=fromObj.getString("id");
									if(current.has("story")){
										name=current.get("story").toString();
									}
									wallObj.addFeed(name, timeStamp, statusMessage, url, id, feedImage);
								}
				        		catch (Exception e)
								{
									continue;
								}
				        	}
						} 
			        	catch (Exception e)
						{
			        		
						}
			        	populateList();
			        }
			    }
			).executeAsync();
		}
		} catch (Exception e)
		{
			
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wall_posts, menu);
		return true;
	}

	@Override
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
		return super.onOptionsItemSelected(item);
	}
}