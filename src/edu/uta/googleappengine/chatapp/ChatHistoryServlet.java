package edu.uta.googleappengine.chatapp;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

@SuppressWarnings("serial")
public class ChatHistoryServlet extends HttpServlet 
{
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{ 
		String outputTxt = "";
		String to = request.getParameter("to").trim();
		String from = request.getParameter("from").trim();
	
		Entity matchingEntity = 
				  CheckIfParticipantExistsInCoumn("Participant1", from,
						  							"Participant2", to);
		if(null == matchingEntity)
		{
			matchingEntity = CheckIfParticipantExistsInCoumn("Participant1", to,
													"Participant2", from);
		}
		
		if(null != matchingEntity)
		{
			String message = (String)matchingEntity.getProperty("Messages");
			
			String[] messageArray = StringUtils.split(message, "|");
			
			for (String string : messageArray) 
			{
				if(string.trim().startsWith(from))
				{
					String value = string.trim().replaceAll(from+":", 
													"<b>me</b>:");
					outputTxt = outputTxt.concat(value+"<br />").trim();
				}
				else if(string.trim().startsWith(to))
				{
					String value = string.trim().replaceAll(to+":", 
							"<b>"+to+"</b>:");
					outputTxt = outputTxt.concat(value + "<br />").trim();
				}
			}	
		}
		
		response.getWriter().print(outputTxt);
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
}
