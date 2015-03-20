package com.example.login;
//Class to handle the wall posts
public class FeedItem
{

	//local variables
	String profilePic, Textname, statusMessage;
	
	//Constructor
	public FeedItem(String profilePic, String Textname, String statusMessage)
	{
		// TODO Auto-generated constructor stub
		this.profilePic=profilePic;
		this.Textname=Textname;
		this.statusMessage=statusMessage;		
	}

	//Getters and setters for all variables
	
	public String getProfilePic()
	{
		return profilePic;
	}

	public void setProfilePic(String profilePic)
	{
		this.profilePic = profilePic;
	}

	public String getTextname()
	{
		return Textname;
	}

	public void setTextname(String textname)
	{
		Textname = textname;
	}

	public String getStatusMessage()
	{
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}
}