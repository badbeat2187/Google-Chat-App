package edu.uta.googleappengine.chatapp;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.*;

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
public class LoginServlet extends HttpServlet 
{
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException 
	{
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user != null) 
		{	
			String userName = user.getNickname();
			resp.getWriter().println("Welcome, " +userName );
			resp.getWriter().println(
					"<a href='"
							+ userService.createLogoutURL(req.getRequestURI())
							+ "'> LogOut </a>");
			
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			
			Query query = new Query("Users");
			query.addFilter("Username", FilterOperator.EQUAL, userName);
			PreparedQuery pq = datastore.prepare(query);
			Entity customer = pq.asSingleEntity();
			
			if(null == customer)
			{
				customer = new Entity("Users");
				customer.setProperty("Username", userName);
			    customer.setProperty("IsLoggedIn", 1);
			    customer.setProperty("NoOfLogins", 1);
			}
			else
			{
				long noOfLogins = (long)customer.getProperty("NoOfLogins");
				noOfLogins = noOfLogins +1;
				customer.setProperty("NoOfLogins", noOfLogins);
				customer.setProperty("IsLoggedIn", 1);
			}
		    
		    datastore.put(customer); //save it
		    UpdateAnalyticsTable();
		    resp.sendRedirect("ChatPage.html");
		} 
		else 
		{
			resp.getWriter().println(
					"Please <a href='"+ userService.createLoginURL(req.getRequestURI())+ "'> LogIn </a>");

		}
	}

	private void UpdateAnalyticsTable() 
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
		
		if(null == analytics)
		{
			analytics = new Entity("Analytics");
			analytics.setProperty("Year", year);
			analytics.setProperty("Month", month);
			analytics.setProperty("Day", day);
			analytics.setProperty("Hour", hour);
			analytics.setProperty("NoOfUsers", 1);
		}
		else
		{
			long noOfUsers = (long)analytics.getProperty("NoOfUsers");
			noOfUsers = noOfUsers +1;
			analytics.setProperty("NoOfUsers", noOfUsers);
		}
		
		datastore.put(analytics); //save it
	}
}
