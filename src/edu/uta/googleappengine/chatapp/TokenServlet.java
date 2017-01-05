package edu.uta.googleappengine.chatapp;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelFailureException;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class TokenServlet extends HttpServlet 
{
	private static ChannelService channelService = ChannelServiceFactory.getChannelService();

	/**
	 * Get the token for connecting & listening on the channel
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException 
	{
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user != null) 
		{	
			String userName = user.getNickname();
			String token = createChannel(userName);
			response.getWriter().print(token);
		}
	}

	/**
	 * Creates the Channel token for the user 
	 * @param userId The User whom the token is created for  
	 * @return The token string is returned
	 */
	public String createChannel(String userId)
	{
		String returnValue = null;
		
		try
		{
			returnValue = channelService.createChannel(userId);			
		}
		catch(ChannelFailureException channelFailureException)
		{
			
		}
		catch(Exception otherException)
		{
			
		}
		
		return returnValue;
	}
}
