package com.example.login;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;
import com.facebook.widget.ProfilePictureView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.provider.MediaStore;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
//Class to handle wall posts
public class FeedAdapter extends BaseAdapter
{
	//ArrayList of FeedItem class
	ArrayList<FeedItem> data;
	
	//Constructor
	public FeedAdapter(ArrayList<FeedItem> cItem)
	{
		// TODO Auto-generated constructor stub
		super();
		this.data=cItem;
		data=new ArrayList<FeedItem>();
	}

	//Add a new item to feed
	public void addFeed(String profilePic, String Textname, String statusMessage)
	{
		FeedItem addObj = new FeedItem(profilePic, Textname, statusMessage);
		data.add(addObj);
	}
	
	//Clear the list
	public void clearData(){
		data.clear();
	}
	
	//get total number of elements
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return data.size();
	}

	//Function to get element at a particular position
	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return (long)position;
	}
	
	//Function to set the view
	@Override
	public View getView(int position, View convertView, final ViewGroup parent)
	{
		// TODO Auto-generated method stub
		LayoutInflater inflater;
		FeedItem temp;
		TextView tvName,tvStatusMsg;
		ProfilePictureView ppvFb=null;
		if(convertView == null)
		{
			inflater=LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.wishlayout, parent, false);
		}
		tvName=(TextView)convertView.findViewById(R.id.tvWisherName);
		tvStatusMsg=(TextView)convertView.findViewById(R.id.tvWish);
		ppvFb=(ProfilePictureView)convertView.findViewById(R.id.wishUserImage);
		temp=(FeedItem)getItem(position);
		tvName.setText(temp.getTextname());
		tvStatusMsg.setText(temp.getStatusMessage());
		ppvFb.setProfileId(temp.getProfilePic());
		return convertView;
	}
}