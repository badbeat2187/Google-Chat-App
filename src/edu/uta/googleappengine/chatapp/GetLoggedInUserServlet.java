package edu.uta.googleappengine.chatapp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class GetLoggedInUserServlet extends HttpServlet 
{
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException  
	{
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user != null) 
		{	
			String userName = user.getNickname();
			response.getWriter().print(userName);
		}
	}		
}
