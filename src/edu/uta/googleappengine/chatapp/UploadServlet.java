package edu.uta.googleappengine.chatapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.channel.ChannelFailureException;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.files.GSFileOptions.GSFileOptionsBuilder;

@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet 
{
	 protected void doPost(HttpServletRequest request, HttpServletResponse response)
			  throws ServletException, IOException 
	  {		 
		  try
		  {
			  BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			  Map<String, List<BlobKey>> uploadedFile = blobstoreService.getUploads(request);
			  
			  if(uploadedFile != null)
			  {
				  List<BlobKey> fileList = uploadedFile.get("file");
				  if((fileList !=null) && (fileList.size() > 0))
				  {
					  BlobKey fileKey = fileList.get(0);
					  if(fileKey != null)
					  {
						  response.sendRedirect("/blobstorepreview?key=" + fileKey.getKeyString());
					  }
				  }
			  }
			  else
			  {
				  response.sendRedirect("Analytics.html");
			  }
			  
			  /*
			  File fileToRead = new File(request.getParameter("file"));
			  String user = request.getParameter("to");
			  String from = request.getParameter("from");
			  
			  FileReader fileReader = new FileReader(fileToRead);
			  BufferedReader reader = new BufferedReader(fileReader);
			 
			  
			// Get a file service
			  FileService fileService = FileServiceFactory.getFileService();
		
			  // Create a new Blob file with mime-type "image/jpeg"
			  AppEngineFile file = fileService.createNewBlobFile("image/jpeg");
			  
			  // Open a channel to write to it
			  boolean lock = true;
			  FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);
			  
			  // Different standard Java ways of writing to the channel
			  // are possible. Here we use a PrintWriter:
			  PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
			  
			  String line;
			  while (null != (line = reader.readLine())) 
		      {
				  out.println(line);
		      }		  
		
			  // Close without finalizing and save the file path for writing later
			  out.close();
			  String path = file.getFullPath();
		
			  // Now finalize
			  writeChannel.closeFinally();
			  response.getWriter().print(path);
		  }
		  catch(Exception e)
		  {
			  response.getWriter().print(e.getMessage());
		  } 
		  */
		 /*try
		  {
			  File fileToRead = new File(request.getParameter("file"));
			  String fileName = request.getParameter("fileName");
			  String user = request.getParameter("to");
			  String from = request.getParameter("from");
			  
			  FileReader fileReader = new FileReader(fileToRead);
			  BufferedReader reader = new BufferedReader(fileReader);
			  
			  byte[] byteArray = IOUtils.toByteArray(reader);
			  
			  FileService fileService = FileServiceFactory.getFileService();
			    GSFileOptionsBuilder optionsBuilder = new GSFileOptionsBuilder()
			        .setBucket("1000828709bucket")
			        .setKey(fileName)
			        .setMimeType("image/jpeg")
			        .setAcl("public_read")
			        .addUserMetadata("myfield1", "my field value");
			    AppEngineFile writableFile =
			        fileService.createNewGSFile(optionsBuilder.build());
			    			    // Open a channel to write to it
			    boolean lock = true;
			    
			    FileWriteChannel writeChannel = fileService.openWriteChannel(writableFile, lock);

			    // This time we write to the channel directly
			    writeChannel.write(ByteBuffer.wrap
			        (byteArray));

			    // Now finalize
			    writeChannel.closeFinally();
			    
			    response.getWriter().print(writableFile.getFullPath());
			    
			    // At this point the file is visible in App Engine as:
			    // "/gs/mybucket/myfile"
			    // and to anybody on the Internet through Google Storage as:
			    // (http://storage.googleapis.com/mybucket/myfile)
			    // So reading it through Files API:
			    
			    /*String filename = "/gs/mybucket/myfile";
			    AppEngineFile readableFile = new AppEngineFile(filename);
			    FileReadChannel readChannel =
			        fileService.openReadChannel(readableFile, false);
			    // Again, different standard Java ways of reading from the channel.
			    BufferedReader reader =
			        new BufferedReader(Channels.newReader(readChannel, "UTF8"));
			    String line = reader.readLine();
			    // line = "The woods are lovely dark and deep."
			    readChannel.close();*/
		  }
		  catch(Exception e)
		  {
			  response.getWriter().print(e.getMessage());
		  }
	  }
}
