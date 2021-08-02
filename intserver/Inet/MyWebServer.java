import java.io.*;
import java.net.*;
import java.util.*;
class HTTP
{	
	public String command;
	public String URL;
	public String versionOfProtocol;
	public String type;
	public static String host; 
	
	private LinkedList<String> HTTPRequest;
	
	public HTTP()
	{		
		this.HTTPRequest = new LinkedList<String>();
	}
	
	public boolean addToRequest(String argument)
	{		return this.HTTPRequest.add(argument);	
	}
	
	public void printHTTPRequest()
	{
		//Print out the entire HTTP Request contents to screen.
		Iterator<String> iterator = this.HTTPRequest.iterator();
		while(iterator.hasNext())
			System.out.println(iterator.next());
	}
	
	public static String getBasePath()
	{
		return (System.getProperty("user.dir"));
	}
	
	public static File getFile(String requestedFile)
	{
		File file = new File(getBasePath() + requestedFile);
		return file;
	}
	
	public void parseHTTPRequest()
	{
		//If there is nothing in the request, return an empty request.
		if(this.HTTPRequest.isEmpty())
			return;
		
		//Delimiter is a space.
		String delims = " ";
		String[] tokens;
		
		//Split the first line of the HTTP Request : GET /dog.txt HTTP/1.1
		tokens = this.HTTPRequest.get(0).split(delims);
		
		this.command 			= tokens[0];
		this.URL 				= tokens[1];
		this.versionOfProtocol 	= tokens[2];
		
		//Split the second line, get the host.
		tokens = this.HTTPRequest.get(1).split(delims);
		
		this.host 				= tokens[1];
		
		//Delims are \ and .
		delims = "\\.";		
		
		tokens = this.URL.split(delims);
		
		//Request for directory have length of 1, aka no file type.
		if(tokens.length > 1)
			this.type = tokens[1];
		else
			this.type = "plain";
	}
	
	public static boolean isDirectory(File file)
	{
		return file.isDirectory();
	}
}

class HTTPResponse
{
	public final String request_OK  = "HTTP/1.1 200 OK";
	public final String contentLength = "Content-Length: ";
	public final String contentType = "Content-Type: ";
	public final String end = "\r\n\r\n";
	public final String enter = "\r\n";
	public String type;
	public final File file;
	
	public HTTPResponse(File file)
	{
		//Set the file equal to the file pointer passed in.
		this.file = file;
	}
	
	public byte[] constructResponse(HTTP httpRequest)
	{
		StringBuilder response = new StringBuilder();

		//Append the header infoormation to the respponse String builder
		//Request Information
		response.append(this.request_OK + enter);
		//Content type
		response.append(this.contentType + "text/" + getType(httpRequest.type) + enter);
		//Content length
		response.append(this.contentLength + getFileLength(this.file) + end);
		
		//If the file is not a directory then
		if(!HTTP.isDirectory(this.file))
			//Create a file
			response.append(createFile(this.file) + end);
		else
			//Create a directory
			response.append(createDirectory(this.file)+ end);
		
		//Print out the response to the server
		System.out.println(response.toString());
		
		//Covert  the response to byte code
		byte[] responseByte = response.toString().getBytes();
		
		//Return the byte code.
		return responseByte;
	}
	
	public String createFile(File file)
	{
		
		StringBuilder fileString = new StringBuilder();
		
		try
		{
			//Begin reading the file
			BufferedReader readFile = new BufferedReader(new FileReader(file));
			
			try
			{
				//While there is information to be read
				while(readFile.ready())
				{
					//Append information to the fileString
					fileString.append(readFile.readLine());
				}				
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		catch(FileNotFoundException x)
		{
			x.printStackTrace();
		}
		
		//Return the file information in the form of a string.
		return fileString.toString();
	}
	
	public String createDirectory(File file)
	{
		StringBuilder directoryString = new StringBuilder();
		File[] directoryContents = file.listFiles();
		String currentPath = HTTP.getBasePath();
		//Curent length
		int length = currentPath.length();
		
		//Begin writing the dynamic HTML.
		directoryString.append("<pre>");
		directoryString.append("<h1>" + "Index of " + file.toString().substring(length) + "</h1>");
		
		directoryString.append("<a href=" + " \"" + file.getParentFile() + "\"" + ">" + "Parent Directory" + "</a>" + " " + "<br>");
		
		//Go through each of the files or directorys, recursively
		for(int i = 0; i < directoryContents.length; i++)
		{
			//Take off the previous directory information
			String path = directoryContents[i].toString().substring(length);
			path = path.replace("\\", "/");
			
			if(directoryContents[i].isDirectory())
				directoryString.append("<a href=" + " \"" + path + "\"" + ">" + directoryContents[i].getName() + "</a>" + " " + "<br>"); 
			else if(directoryContents[i].isFile())
				directoryString.append("<a href=" + " \"" + path + "\"" + ">" + directoryContents[i].getName() + "</a>" + " " + "<br>");
		}
		
		directoryString.append("</pre>");
		
		return directoryString.toString();
	}
	
	public long getFileLength(File file)
	{
		if(file.isFile())
			return file.length();
		
		
		return getDirectoryLength(file);
	}
	
	public long getDirectoryLength(File file)
	{
		StringBuilder directoryString = new StringBuilder();
		File[] directoryContents = file.listFiles();
		
		directoryString.append("<pre>");
		directoryString.append("<h1>" + "Index of /brian/Files" + "</h1>");
		
		directoryString.append("<a href=" + " \"" + file.getPath() + "\"" + ">" + "Parent Directory" + "</a>" + " " + "<br>");
		
		for(int i = 0; i < directoryContents.length; i++)
			if(directoryContents[i].isDirectory())
				directoryString.append("<a href=" + " \"" + directoryContents[i] + "\"" + ">" + directoryContents[i].getName() + "</a>" + " " + "<br>"); 
			else if(directoryContents[i].isFile())
				directoryString.append("<a href=" + " \"" + directoryContents[i] + "\"" + ">" + directoryContents[i].getName() + "</a>" + " " + "<br>");
		
		directoryString.append("</pre");
		
		return directoryString.toString().length();	
	}
	
	public String getType(String type)
	{
		switch(type)
		{
		case "txt" :
			return "plain";
		case "html" :
			return "html";
		default :
			return "html";
		}
	}
}


class WebWorker extends Thread 
{
	
	Socket sock;
	WebWorker (Socket s) {this.sock = s;}
	
	public void run()
	{
		PrintStream out = null;
		BufferedReader in = null;
		String comingFromStream = null;
		HTTP httpRequest = null;
		HTTPResponse httpResponse = null;
		File requestFile = null;
		byte[] response = null;
		
		try
		{
			//Returns a buffered reader, of an input stream, of the sockets
			//input stream.
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			//Returns an output stream for the socket.
			out = new PrintStream(sock.getOutputStream());
			//Note that this branch might not execute when expected*
			
			//Create a new HTTP Request Form
			httpRequest = new HTTP();
			
			//While there is data to receive, once the entire sent data is received, continue.
			while(in.ready())
			{
				//Read the next line.
				comingFromStream = in.readLine();
				//Take that line and add it to the current http request.
				httpRequest.addToRequest(comingFromStream);
			}	
			
			//Print request.
			httpRequest.printHTTPRequest();
			
			//Parse the request
			httpRequest.parseHTTPRequest();
			
			//Get File
			requestFile = HTTP.getFile(httpRequest.URL);
			
			//HTTP response
			if(requestFile.exists())
			{
				//Create a new HTTP Response.
				httpResponse = new HTTPResponse(requestFile);
			
				//Create an http response that will be sent to the client
				response = httpResponse.constructResponse(httpRequest);
				
				//Send the response to the server.
				out.write(response);
			}
			
			//Closes the socket.
			sock.close();	
		}
		catch(IOException x)
		{
			System.out.println(x);
		}
	}
}

public class MyWebServer
{
	public static boolean controlSwitch = true;
	
	public static void main(String a[]) throws IOException
	{
		int q_len = 6;
		int port = 2540;
		Socket sock;
		ServerSocket servsock = new ServerSocket(port, q_len);
		
		System.out.println("kaijun'ss Listener server starting up, listening at port 2540.");
		
		while(controlSwitch)
		{
			sock = servsock.accept();
				if(controlSwitch)
				new WebWorker(sock).start();
		}
	}
}