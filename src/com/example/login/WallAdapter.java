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

//Class to handle news feed
public class WallAdapter extends BaseAdapter
{
	ArrayList<WallItem> data;
	
	//Constructor
	public WallAdapter(ArrayList<WallItem> cItem)
	{
		// TODO Auto-generated constructor stub
		super();
		this.data=cItem;
		data=new ArrayList<WallItem>();
	}

	//Function to add a new feed
	public void addFeed(String name, String timeStamp, String statusMessage, String url, String id, String feedImage)
	{
		WallItem addObj = new WallItem(name, timeStamp, statusMessage, url, id, feedImage);
		data.add(addObj);
	}
	
	//Function to clear the list
	public void clearData(){
		data.clear();
	}
	
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return data.size();
	}

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
	
	//Function to handle view 
	@Override
	public View getView(int position, View convertView, final ViewGroup parent)
	{
		// TODO Auto-generated method stub
		LayoutInflater inflater;
		WallItem temp;
		TextView tvName,tvStatusMsg,tvTimeStamp, tvURL;
		ProfilePictureView ppvFb=null;
		ImageView ivPhoto;
		if(convertView == null)
		{
			inflater=LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.feedlayout, parent, false);
		}
		tvName=(TextView)convertView.findViewById(R.id.name);
		tvTimeStamp=(TextView)convertView.findViewById(R.id.timestamp);
		ppvFb=(ProfilePictureView)convertView.findViewById(R.id.userImage);
		tvStatusMsg=(TextView)convertView.findViewById(R.id.txtStatusMsg);
		tvURL=(TextView)convertView.findViewById(R.id.txtUrl);
		ivPhoto=(ImageView)convertView.findViewById(R.id.feedImage);
		temp=(WallItem)getItem(position);
		tvName.setText(temp.getName());
		tvTimeStamp.setText(temp.getTimeStamp());
		tvStatusMsg.setText(temp.getStatusMessage());
		ppvFb.setProfileId(temp.getId());
		ivPhoto.setImageBitmap(null);
		URL url=null;
		try
		{
			url = new URL(temp.getFeedImage());
			ivPhoto.setImageBitmap(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
		} catch (Exception e)
		{
			// TODO: handle exception
		}
		return convertView;
	}
}