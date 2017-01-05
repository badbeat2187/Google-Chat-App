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
	loadNoOfLoginsPerUser();
	loadNoOfLoginsPerHours();
}

loadNoOfLoginsPerUser = function()
{
	var getNoOfLoginsPerUserURI = '/getNoOfLoginsPerUser';
	var httpRequest = makeRequest(getNoOfLoginsPerUserURI,false);
	if (httpRequest.readyState === 4) 
	{
		if (httpRequest.status === 200) 
		{
			var logins = httpRequest.responseText;
			var txt = document.createElement("div");
			txt.innerHTML = "<p> Number of logins per user is <b>"+logins+"</b><p><hr />";
			document.getElementById("loginsPerUser").appendChild(txt);	
		}
		else 
		{
			alert('There was a problem with the request.');
		}
	}
};

loadNoOfLoginsPerHours = function()
{
	var getNoOfLoginsPerHourURI = '/getNoOfLoginsPerHour';
	var httpRequest = makeRequest(getNoOfLoginsPerHourURI,false);
	if (httpRequest.readyState === 4) 
	{
		if (httpRequest.status === 200) 
		{
			var logins = httpRequest.responseText;
			var txt = document.createElement("div");
			txt.innerHTML = 
			document.getElementById("NoOfLoginsPerHour").appendChild(txt);	
		}
		else 
		{
			alert('There was a problem with the request.');
		}
	}
};