package edu.uta.googleappengine.chatapp;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelFailureException;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

@SuppressWarnings("serial")
public class MessageServlet extends HttpServlet 
{
	private static ChannelService channelService = ChannelServiceFactory.getChannelService();

	  protected void doPost(HttpServletRequest request, HttpServletResponse response)
	  throws ServletException, IOException 
	  {
		  String message = request.getParameter("message");
		  String user = request.getParameter("to");
		  String from = request.getParameter("from");
		  if (user != null && !user.equals("") && message != null
		        && !message.equals("")) 
		  {
		      try
		      {
		    	  String outputMessage ="<data>" +
						"<type>updateChatBox</type>" +
						"<message>"+message+"</message>" +
						"<from>"+from+"</from>" +
						"</data>"; 
		    	  
		    	  sendMessageToChannel(user, outputMessage);
		    	  SaveToChatHistory(from, user, message);
		      }
		      catch (ChannelFailureException channelFailure) 
		      {
		    	  response.getWriter().print("OFFLINE");
		      }
		      catch (Exception e) 
		      {
		    	  response.getWriter().print("OFFLINE");
		      }
		   }
	  }
	
	  private void SaveToChatHistory(String from, String to, String message) 
	  {
		  message = from + ": '" +message+ "'|";
		  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		  
		  Entity matchingEntity = 
				  CheckIfParticipantExistsInCoumn("Participant1", from,
						  							"Participant2", to);
		  if(null == matchingEntity)
		  {
			  matchingEntity = CheckIfParticipantExistsInCoumn("Participant1", to,
													"Participant2", from);
		  }
		  		  
		  if(null == matchingEntity)
		  {
			  matchingEntity = new Entity("Chats");
			  matchingEntity.setProperty("Participant1", from);
			  matchingEntity.setProperty("Participant2", to);
			  matchingEntity.setProperty("Messages", message);
		  }
		  else
		  {
			  String previousMessages = (String)
					  matchingEntity.getProperty("Messages");
			  message = previousMessages + message;
			  matchingEntity.setProperty("Messages", message);
		  }
		  
		  datastore.put(matchingEntity);
	  }
	  	  	  
	  private Entity CheckIfParticipantExistsInCoumn(String columnName1, 
			  			String columnValue1, String columnName2, String columnValue2)
	  {
		  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		  
		  Query query = new Query("Chats");
		  query.addFilter(columnName1, FilterOperator.EQUAL, columnValue1);
		  query.addFilter(columnName2, FilterOperator.EQUAL, columnValue2);
		  PreparedQuery pq = datastore.prepare(query);
		  return pq.asSingleEntity();
	  }

	public void sendMessageToChannel(String user,String message) throws ChannelFailureException
	{
		  channelService.sendMessage(new ChannelMessage(user, message));
	}
}
