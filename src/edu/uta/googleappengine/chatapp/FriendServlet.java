package edu.uta.googleappengine.chatapp;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class FriendServlet extends HttpServlet 
{
	private static ChannelService channelService = ChannelServiceFactory.getChannelService();
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException 
	{  
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user != null) 
		{	
			String userName = user.getNickname();
			response.setContentType("text/xml");
		  	String outputTxt =	"<data>\n" ;
		  	
		  	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			
			Query query = new Query("Users");
			query.addFilter("IsLoggedIn", FilterOperator.EQUAL, 1);
			PreparedQuery pq = datastore.prepare(query);
			Iterator<Entity> friendList = pq.asIterator();
			
			if(null != friendList)
			{
			  	//Add all the users logged in already to the new user friends list
			    // and also update all of them about the new user
			  	while(friendList.hasNext())
			  	{
			  		Entity friend = friendList.next();
			  		String friendName = (String)friend.getProperty("Username");
			  		
			  		if(!friendName.equals(userName))
			  		{
			  			outputTxt +="<friend><name>" + friendName +"</name></friend>\n";
			  			channelService.sendMessage(
			  					new ChannelMessage(friendName,"<data>" +
			  							"<type>updateFriendList</type>" +
			  							"<message>"+userName+"</message>" +
			  							"<from>Server</from>" +
			  							"</data>"));
			  		}
			  	}
			  	
			  	outputTxt += "</data>\n";
			  	response.getWriter().print(outputTxt);
			}
		}
	}
}
