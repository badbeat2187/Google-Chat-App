package edu.uta.googleappengine.chatapp;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class NumOfLoginsPerHour extends HttpServlet 
{
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException 
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Date dateTime = new Date();
		int year = dateTime.getYear()+1900;
		int month = dateTime.getMonth()+1;
		int day = dateTime.getDate();
		int hour = dateTime.getHours();
		
		Query query = new Query("Analytics");
		query.addFilter("Year", FilterOperator.EQUAL, year);
		query.addFilter("Month", FilterOperator.EQUAL, month);
		query.addFilter("Day", FilterOperator.EQUAL, day);
		query.addFilter("Hour", FilterOperator.EQUAL, hour);
		
		PreparedQuery pq = datastore.prepare(query);
		Entity analytics = pq.asSingleEntity();
		
		if(null != analytics)
		{
			long noOfUsers = (long)analytics.getProperty("NoOfUsers");
			
			String output = "<p> Number of user logins in the past hour is <b>"+noOfUsers+"</b><p><hr />"
							+"<p> Number of user logins in the current month is <b>"+GetNumberOfUserPerMonth()+"</b><p><hr />"
							+"<p> Number of user logins in the current year is <b>"+GetNumberOfUserPerMonth()+"</b><p><hr />";;
			resp.getWriter().print(noOfUsers);
		}		    
	}

	private String GetNumberOfUserPerMonth() 
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Date dateTime = new Date();
		int year = dateTime.getYear()+1900;
		int month = dateTime.getMonth()+1;
		
		Query query = new Query("Analytics");
		query.addFilter("Year", FilterOperator.EQUAL, year);
		query.addFilter("Month", FilterOperator.EQUAL, month);
		
		PreparedQuery pq = datastore.prepare(query);
		Iterator<Entity> analytics = pq.asIterator();
		
		long noOfUser = 0;
		while(null != analytics && analytics.hasNext())
		{
			Entity data = analytics.next();
			noOfUser = noOfUser + (long)data.getProperty("NoOfUsers");
		}
		
		return String.valueOf(noOfUser);
	}
	
	private String GetNumberOfUserPerYear() 
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Date dateTime = new Date();
		int year = dateTime.getYear()+1900;
		
		Query query = new Query("Analytics");
		query.addFilter("Year", FilterOperator.EQUAL, year);
		
		PreparedQuery pq = datastore.prepare(query);
		Iterator<Entity> analytics = pq.asIterator();
		
		long noOfUser = 0;
		while(null != analytics && analytics.hasNext())
		{
			Entity data = analytics.next();
			noOfUser = noOfUser + (long)data.getProperty("NoOfUsers");
		}
		
		return String.valueOf(noOfUser);
	}
}
