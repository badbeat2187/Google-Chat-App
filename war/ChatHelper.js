var userid;
window.onload= init;


//general ajax function for all requests 
function makeRequest(url,async) 
{
	var httpRequest;
	if (window.XMLHttpRequest) 
	{
		// Mozilla, Safari, ...
		httpRequest = new XMLHttpRequest();
	} 
	else if (window.ActiveXObject) 
	{
		// IE
		try 
		{
			httpRequest = new ActiveXObject("Msxml2.XMLHTTP");
		} 
		catch (e) 
		{
			try 
			{
				httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
			} 
			catch (e) 
			{}
		}
	}

	if (!httpRequest) 
	{
		alert('Giving up :( Cannot create an XMLHTTP instance');
		return false;
	}
	httpRequest.open('POST', url,async);
	httpRequest.send();
	return httpRequest;
}


function init()
{
	getLoggedInUser();
	requestToken();
	loadFriendList();
}

getLoggedInUser = function()
{
	var getTokenURI = '/getloggedinuser';
	var httpRequest = makeRequest(getTokenURI,true);
	httpRequest.onreadystatechange = function()
	{
		if (httpRequest.readyState === 4) 
		{
			if (httpRequest.status === 200) 
			{
				setUserId(httpRequest.responseText);
			}
			else 
			{
				alert('There was a problem with the request.');
			}
		}
	}
};

setUserId = function(name)
{
	userid = name;
	var txt = document.createElement("div");
	txt.innerHTML = "<p> Logged in as <b>"+userid+"</b><p><hr />";
	document.getElementById("loggedInUser").appendChild(txt);
};

requestToken = function()
{
	var getTokenURI = '/gettoken';
	var httpRequest = makeRequest(getTokenURI,true);
	httpRequest.onreadystatechange = function(){
		if (httpRequest.readyState === 4) {
			if (httpRequest.status === 200) {
				openChannel(httpRequest.responseText);
			}else {
				alert('There was a problem with the request.');
			}
		}
	}
};

openChannel = function(token) 
{	
	var channel = new goog.appengine.Channel(token);
	var socket = channel.open();
	socket.onopen = onSocketOpen;
	socket.onmessage = onSocketMessage;
	socket.onerror = onSocketError;
	socket.onclose = onSocketClose;
};

onSocketError = function(error){
	alert("Error is <br/>"+error.description+" <br /> and HTML code"+error.code);
};

onSocketOpen = function() {
	
};

onSocketClose = function() {
	alert("Socket Connection closed");
};

onSocketMessage = function(message) {
	var messageXML =  ((new DOMParser()).parseFromString(message.data, "text/xml"));
	var messageType = messageXML.documentElement.getElementsByTagName("type")[0].firstChild.nodeValue;
	if(messageType == "updateFriendList"){
		addToFriends(messageXML.documentElement.getElementsByTagName("message")[0].firstChild.nodeValue);
	}else if(messageType == "updateChatBox"){
		var friend = messageXML.documentElement.getElementsByTagName("from")[0].firstChild.nodeValue ;
		document.getElementById(friend+"chatBox").style.display="block";
		updateChatBox(messageXML.documentElement.getElementsByTagName("message")[0].firstChild.nodeValue,friend);
	}
};


loadFriendList = function()
{		
	var getFriendListURI = '/getFriendList';
	var httpRequest = makeRequest(getFriendListURI,false);
	if (httpRequest.readyState === 4) 
	{
		if (httpRequest.status === 200) 
		{
			var friendListXML = httpRequest.responseXML.getElementsByTagName("friend");
			for( var i =0 ; i < friendListXML.length ; i++)
			{
				addToFriends(friendListXML[i].getElementsByTagName("name")[0].firstChild.nodeValue);
			}	
		}
		else 
		{
			alert('There was a problem with the request.');
		}
	}
};


var friendsList= new Array();


addToFriends = function(friend){
	//check if the user already added
	var contains = false;
	for(var i = 0 ; i < friendsList.length ; i++){
		if(friendsList[i]==friend){
			contains=true;
			break;
		}
	}
	if(!contains){
		friendsList.push(friend);
		var a = "<a id='"+friend+"'>"+friend+"</a>";
		var txt = document.createElement("div");
		txt.innerHTML = a;
		txt.style.cursor="pointer";
		txt.setAttribute("onclick","openChat(\""+friend+"\");");
		document.getElementById("friendsListPage").appendChild(txt);

		//adding chat boxes 
		var chatBox = document.createElement("div");
		chatBox.style.display = "none";
		chatBox.setAttribute("id",friend+"chatBox");
		chatBox.setAttribute("class","chatbox");

		var headerContainer = document.createElement("div");
		headerContainer.setAttribute("class","headerContainer");

		var closeButton = document.createElement("a");
		closeButton.innerHTML="X";
		closeButton.setAttribute("class","closeButton");
		closeButton.setAttribute("onclick","closeWindow('"+friend+"')");
		headerContainer.appendChild(closeButton);

		var headerMessage = document.createElement("p");
		headerMessage.setAttribute("class","headerMessage");
		headerMessage.innerHTML = "<b>"+friend+"</b><br /><br />";
		headerContainer.appendChild(headerMessage);

		chatBox.appendChild(headerContainer);
		var chatBoxMessagesContainer = document.createElement("div");
		chatBoxMessagesContainer.setAttribute("id",friend+"chatBoxMessageContainer");
		chatBoxMessagesContainer.setAttribute("class","chatBoxMessagesContainer");
		chatBox.appendChild(chatBoxMessagesContainer);

		var chatBoxMessagesTextarea = document.createElement("div");
		var textareaStr = "<textarea id='"+friend+"textarea' cols='25' rows='2'" +
			"onkeydown=\"if(event.keyCode == 13){sendMessage('"+friend+"') }\"" +
			"class=\"chatTextArea\"" +
			" ></textarea>";
		chatBox.innerHTML += textareaStr ;

		var submitButton = document.createElement("input");
		submitButton.setAttribute("type","button");
		submitButton.setAttribute("value","send");
		submitButton.setAttribute("id",friend+"submitButton");
		submitButton.setAttribute("onclick","sendMessage('"+friend+"')");
		
		var uploadButton = document.createElement("input");
		uploadButton.setAttribute("type","file");
		uploadButton.setAttribute("id",friend+"files");
		uploadButton.setAttribute("onchange","uploadFile('"+friend+"'," +
											"'"+friend+"files')");
		
		chatBox.appendChild(document.createElement("br"));
		chatBox.appendChild(uploadButton);
		chatBox.appendChild(submitButton);
		
		chatBox.appendChild(document.createElement("br"));
		document.getElementById("chatMessagesPage").appendChild(chatBox);
	}
};

closeWindow = function(friend){
	document.getElementById(friend+"chatBox").style.display="none";
};

openChat = function(friend)
{
	var getChatHistoryUri ='/chathistory?from='+userid+'&to='+friend;
	var httpRequest = makeRequest(getChatHistoryUri,true);
	
	httpRequest.onreadystatechange = function()
	{
		if (httpRequest.readyState === 4) 
		{
			if (httpRequest.status === 200) 
			{
				var chatHistory = httpRequest.responseText;
				var mesgDiv = document.createElement("a");
				mesgDiv.innerHTML = chatHistory;
				
				var abc = document.getElementById(friend+"chatBoxMessageContainer");
				while (abc.firstChild) 
				{
					abc.removeChild(myNode.firstChild);
				}
				
				abc.appendChild(mesgDiv);					
			}
			else {
				alert('There was a problem with the request.');
			}
		}
	}
	
	document.getElementById(friend+"chatBox").style.display = "block";
	document.getElementById(friend+"textarea").focus();
};

sendMessage = function(friend)
{
	var message = document.getElementById(friend+"textarea").value;
	var sendMessageURI = '/message?message=' + message + '&to=' + friend+'&from='+userid ;
	var httpRequest = makeRequest(sendMessageURI,true);
	httpRequest.onreadystatechange = function(){
		if (httpRequest.readyState === 4) {
			if (httpRequest.status === 200) {
				}else {
				alert('There was a problem with the request.');
			}
		}
	}
	var mesgDiv = document.createElement("a");
	mesgDiv.innerHTML ="<b>me</b>:  "+ message+"<br />";
	var abc = document.getElementById(friend+"chatBoxMessageContainer");
	if(abc)
		abc.appendChild(mesgDiv);
	else
		alert("error");
	document.getElementById(friend+"textarea").value="";
};

updateChatBox = function(message,from){
	var mesgDiv = document.createElement("a");
	mesgDiv.innerHTML ="<b>"+from+"</b>:  "+ message+"<br />";
	var abc = document.getElementById(from+"chatBoxMessageContainer");
	if(abc)
		abc.appendChild(mesgDiv);
	else
		alert("error");
};

uploadFile = function(friend, id)
{
	var files = document.getElementById(id).files;
	for (var i = 0; i < files.length; i++)
	{
		var uploadFileURI = '/uploadfile?file=' + files[i] + '&to=' + friend+'&from='+userid ;
		var httpRequest = makeRequest(uploadFileURI,true);
		httpRequest.onreadystatechange = function()
		{
			if (httpRequest.readyState === 4) 
			{
				if (httpRequest.status === 200) 
				{			
					var source = httpRequest.responseText;
					alert(source);
					var mesgDiv = document.createElement("a");
					mesgDiv.innerHTML ="<b>me</b>:  <img src='.//"+source+"'/> <br />";
					var abc = document.getElementById(friend+"chatBoxMessageContainer");
					if(abc)
						abc.appendChild(mesgDiv);
					else
						alert("error");					
				}
				else
				{
					alert('There was a problem with the request.');
				}
			}
		}
	}
};
