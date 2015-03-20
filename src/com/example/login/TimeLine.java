package com.example.login;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
//Class to handle TimeLine
public class TimeLine extends ActionBarActivity
{
	//Form variables, other variables
	private Session userSession = null;
	private ListView list = null ;
	private String user="";
	private String id="";
	private String bDay="";
	private List<String> bdayPosts = new ArrayList<String>();
	private int arrLength=0;
	private TextView totalPosts;
	private Button btnReply;
	FeedAdapter feedObj;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_line);
		
		if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
		
		Intent mainIntent=getIntent();
		user=mainIntent.getStringExtra("user");
		id=mainIntent.getStringExtra("ID");
		bDay=mainIntent.getStringExtra("bDay");
		userSession = (Session) mainIntent.getSerializableExtra("session");
		Toast.makeText(TimeLine.this, "Fetching wishes", Toast.LENGTH_LONG).show();
	
		Session currentSession = Session.getActiveSession();
		List<String> permissions = currentSession.getPermissions();
		
		if(!permissions.contains("read_stream")){
			//currentSession.requestNewReadPermissions(new NewPermissionsRequest(this, "read_stream"));
		}
		
		totalPosts=(TextView)findViewById(R.id.tvTotalPosts);
		
		list=(ListView)findViewById(android.R.id.list);
		registerForContextMenu(list);
		feedObj=new FeedAdapter(null);
		
		getWallPosts();
		populateList();
		
		//Handle reply click event
		btnReply=(Button)findViewById(R.id.btnReply);
		btnReply.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int repliesPosted=0;
				// TODO Auto-generated method stub
				if(bdayPosts.size()>0){
					for(int i=0;i<bdayPosts.size();i++){
						String[] parts=bdayPosts.get(i).split(Pattern.quote("|"));
						String part1=parts[0];
						String part2=parts[1];
						repliesPosted = postComment(part1, part2, repliesPosted);
					}
					Toast.makeText(TimeLine.this, "You thanked "+repliesPosted+" friends", Toast.LENGTH_SHORT).show();
					arrLength=0;
					bdayPosts.clear();
					feedObj.clearData();
					feedObj.notifyDataSetChanged();
					getWallPosts();
				}
				else{
					Toast.makeText(TimeLine.this, "No posts to reply", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo mInfo)
	{
		getMenuInflater().inflate(R.menu.contextmenu, menu);
	}
	
	private void populateList()
	{
		list.setAdapter(feedObj);
	}
	
	//Handling context menu click
	@Override
	public boolean onContextItemSelected(MenuItem item){
		
		AdapterContextMenuInfo info=(AdapterContextMenuInfo)item.getMenuInfo();
		int position=info.position;
		
		String[] parts=bdayPosts.get(position).split(Pattern.quote("|"));
		final String part1=parts[0];
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Post Comment");
		alert.setMessage("The comment you enter below will be posted to the selected post");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Post", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				String comment=input.getText().toString();
				if(comment.trim().equalsIgnoreCase("")){
					Toast.makeText(TimeLine.this, "Enter valid Text", Toast.LENGTH_SHORT).show();
				}
				else{
					postCustomComment(part1,comment);
					Toast.makeText(TimeLine.this, "Comment posted", Toast.LENGTH_SHORT).show();
					Toast.makeText(TimeLine.this, "Refreshing wishes", Toast.LENGTH_SHORT).show();
					arrLength=0;
					bdayPosts.clear();
					feedObj.clearData();
					feedObj.notifyDataSetChanged();
					getWallPosts();
				}
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
			  Toast.makeText(TimeLine.this, "Comment cancelled", Toast.LENGTH_SHORT).show();
		  }
		});
		alert.show();
		return super.onContextItemSelected(item);
	}
	
	//Function to post custom comment
	public void postCustomComment(String comment, String message)
	{
		Bundle params = new Bundle();
		params.putString("message", message);
		/* make the API call */
		new Request(
		    userSession,
		    "/"+comment+"/comments",
		    params,
		    HttpMethod.POST,
		    new Request.Callback() {
		        public void onCompleted(Response response) {
		            /* handle the result */
		        }
		    }
		).executeAsync();
	}
	
	//Function to get all Wall Posts
	public void getWallPosts(){
		try
		{
			Bundle params=new Bundle();
			params.putString("limit", "500");
			new Request(
				    userSession,
				    "/me/feed",
				    params,
				    HttpMethod.GET,
				    new Request.Callback() {
				        public void onCompleted(Response response) {
				        	JSONArray json = null;
				        	JSONObject current = null;
				        	String type="";
				        	String message="";
				        	String name="";
				        	String id="";
				        	String postedDate="";
				        	int i=0;
				        	try
							{
				        		json = (JSONArray) response.getGraphObject().getProperty("data");	
				        		for(i=0;i<100;i++)
					        		{
				        				try
										{
				        					current=json.getJSONObject(i);
				        					
					        				if(current.has("type")){
						        				type=current.getString("type");
						        				if(type.equalsIgnoreCase("status")){
						        					if(current.has("message")){
						        						message=current.getString("message");
						        						postedDate=GetLocalDateStringFromUTCString(current.getString("created_time"));
						        						if(condition(message, postedDate)){
						        						//if(message.toLowerCase().contains("birthday")||message.toLowerCase().contains("bday")){
						        							JSONObject fromObj = current.getJSONObject("from");
						        							if(!current.has("comments")){
						        								id=current.getString("id");
						        								name=fromObj.getString("name");
						        								feedObj.addFeed(fromObj.getString("id"), name, message);
						        								bdayPosts.add(id+"|"+name);
						        								arrLength++;
						        								populateList();
						    				        			totalPosts.setText(arrLength+" wishes on timeline");
						        							}
						        						}
						        					}
						        				}
						        			}
										} 
				        				catch (Exception e)
										{
											// TODO: handle exception
											continue;
										}
					        		}
				        	}
				        	catch (Exception e)
							{
								// TODO: handle exception
							}
				        }
				    }
				).executeAsync();
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		populateList();
		totalPosts.setText(arrLength+" wishes on timeline");
	}
	
	//Function to get posts on a condition
	public boolean condition(String message,String date){
		
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date myDate = null;
		Date birthDay = null;
		bDay=bDay.substring(0,6)+date.substring(date.lastIndexOf('/')+1);
		
		try {
			myDate = formatter.parse(date);
			birthDay=formatter.parse(bDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		int difference = (int)( (myDate.getTime() - birthDay.getTime()) / (1000 * 60 * 60 * 24));
		
		if(difference==0 || difference==-1 || difference==1 || message.toLowerCase().contains("birthday")||message.toLowerCase().contains("bday")||message.toLowerCase().contains("b'day")){
			return true;
		}
		
		return false;
	}
	
	//Function to convert UTC timestamp to local timestamp
	public String GetLocalDateStringFromUTCString(String utcLongDateTime) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	    String localDateString = null;
	    long when = 0;
	    try {
	        when = dateFormat.parse(utcLongDateTime).getTime();
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }
	    //localDateString = dateFormat.format(new Date(when + TimeZone.getDefault().getRawOffset() + (TimeZone.getDefault().inDaylightTime(new Date()) ? TimeZone.getDefault().getDSTSavings() : 0)));
	    Date localDate = new Date(when + TimeZone.getDefault().getRawOffset() + (TimeZone.getDefault().inDaylightTime(new Date()) ? TimeZone.getDefault().getDSTSavings() : 0));
	    SimpleDateFormat myFormat=new SimpleDateFormat("MM/dd/yyyy");
	    String myDate=myFormat.format(localDate);
	    return myDate;
	}
	
	//Auto reply to all
	public int postComment(String comment, String name, int repliesPosted)
	{
		Bundle params = new Bundle();
		String message="Thanks "+name;
		params.putString("message", message);
		/* make the API call */
		new Request(
		    userSession,
		    "/"+comment+"/comments",
		    params,
		    HttpMethod.POST,
		    new Request.Callback() {
		        public void onCompleted(Response response) {
		            /* handle the result */
		        	//Toast.makeText(TimeLine.this, "Comment posted", Toast.LENGTH_SHORT).show();

		        }
		    }
		).executeAsync();
		return ++repliesPosted;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.time_line, menu);
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