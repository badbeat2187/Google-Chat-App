package edu.uta.googleappengine.chatapp;

import java.io.IOException;

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
public class NumberOfLoginsPerUser extends HttpServlet 
{
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException 
	{
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user != null) 
		{	
			String userName = user.getNickname();
			
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			
			Query query = new Query("Users");
			query.addFilter("Username", FilterOperator.EQUAL, userName);
			PreparedQuery pq = datastore.prepare(query);
			Entity customer = pq.asSingleEntity();
			
			if(null != customer)
			{				
				long noOfLogins = (long)customer.getProperty("NoOfLogins");
				resp.getWriter().print(noOfLogins);
			}		    
		} 
		else 
		{
			resp.getWriter().println(
					"Please <a href='"+ userService.createLoginURL(req.getRequestURI())+ "'> LogIn </a>");

		}
	}
}
