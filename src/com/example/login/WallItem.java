package com.example.login;
//Class to define timeline
public class WallItem
{
	//Variables
	String name="",
			timeStamp="",
			statusMessage="",
			url="", 
			id="", 
			feedImage="";
	
	//Constructor
	public WallItem(String name, String timeStamp, String statusMessage, String url, String id, String feedImage)
	{
		this.name=name;
		this.timeStamp=timeStamp;
		this.statusMessage=statusMessage;
		this.url=url;
		this.id=id;
		this.feedImage=feedImage;
	}

	//Getters and setters
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp)
	{
		this.timeStamp = timeStamp;
	}

	public String getStatusMessage()
	{
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getFeedImage()
	{
		return feedImage;
	}

	public void setFeedImage(String feedImage)
	{
		this.feedImage = feedImage;
	}
	
}