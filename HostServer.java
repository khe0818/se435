/*--------------------------------------------------------

1. Name / Date: Kaijun He 2/25/2018

2. java version "1.8.0_144"
Java(TM) SE Runtime Environment (build 1.8.0_144-b01)
Java HotSpot(TM) 64-Bit Server VM (build 25.144-b01, mixed mode)

3. 

> javac HostServer.java


4. 

>java HostServer


5. 
HostServer.java

6. Notes:
this is copy and run project then what i do is to follow the instruction, tools i use is mac terminal console, and firefox, chrome browser
excute the java HostServer file, then in firefox and chrome browser type in localhost:1565, then follow what shown in browser and test different condition 
by check the output message in terminal, then i could understand how the host Server works





----------------------------------------------------------*/


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

class AgentWorker extends Thread {
	
	// define a socket type with varible name sock like normal server and client project
	Socket sock; 
	// define an agentHolder type parameter where agentHolder constructor defined below
	agentHolder parentAgentHolder; 
	// define local port parameter
	int localPort; 

	
	// define an AgentWorker consturctor with socket, port number and given parentAgentHolder 
	AgentWorker (Socket s, int prt, agentHolder ah) {
		sock = s;
		localPort = prt;
		parentAgentHolder = ah;
	}
	public void run() {
		
		//initialize bufferreader and printout stream in and out
		PrintStream out = null;
		BufferedReader in = null;
		// create a string with "localhost"
		String NewHost = "localhost";
		// since we define default port is 1565, then for the agentworker we define same port number as well
		int NewHostMainPort = 1565;		
		String buf = "";
		int newPort;
		Socket clientSock;
		// define bufferreader and printStream which indicate what we write and read
		BufferedReader fromHostServer;
		PrintStream toHostServer;
		
		try {
			// each time we run a new agent then we should create a new printStream out and new BufferReader in
			out = new PrintStream(sock.getOutputStream());
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			
			//use readline mehtod to read first line and store in string inLine.
			String inLine = in.readLine();
			//define StringBuilder type for HTML repsonse by using api 
			StringBuilder htmlString = new StringBuilder();
			
			// printout the message what we read
			System.out.println();
			System.out.println("Request line: " + inLine);
			// when the in put text is migrate then jump into if loop
			if(inLine.indexOf("migrate") > -1) {
				// create a new socket
				clientSock = new Socket(NewHost, NewHostMainPort);
				// same as before, create new BufferReader and printStream from server and to server
				fromHostServer = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
				toHostServer = new PrintStream(clientSock.getOutputStream());
				toHostServer.println("Please host me. Send my port! [State=" + parentAgentHolder.agentState + "]");
				toHostServer.flush();
				
				for(;;) {
					//set buf as read line form host Server
					buf = fromHostServer.readLine();
					if(buf.indexOf("[Port=") > -1) {
						break;
					}
				}
				
				//use substring to get port number string between [Port= appears and ]
				String tempbuf = buf.substring( buf.indexOf("[Port=")+6, buf.indexOf("]", buf.indexOf("[Port=")) );
				// parse string into integer for what we get above
				newPort = Integer.parseInt(tempbuf);
				//print out new port number in console
				System.out.println("newPort is: " + newPort);
				
				//then send new information to browser when we recieve the test migrate
				htmlString.append(AgentListener.sendHTMLheader(newPort, NewHost, inLine));
				htmlString.append("<h3>We are migrating to host " + newPort + "</h3> \n");
				htmlString.append("<h3>View the source of this page to see how the client is informed of the new location.</h3> \n");
				htmlString.append(AgentListener.sendHTMLsubmit());

				System.out.println("Killing parent listening loop.");
				// set serversocket ss as parentagentHOlder's sock
				ServerSocket ss = parentAgentHolder.sock;
				//and then close the socket 
				ss.close();
				
				// when type in text is valid then go this loop
			} else if(inLine.indexOf("person") > -1) {
				// each time it's valid then increase agentstate number
				parentAgentHolder.agentState++;
				// use sendHTMLheader and SendHTMLsubmit method to update message in browser
				htmlString.append(AgentListener.sendHTMLheader(localPort, NewHost, inLine));
				htmlString.append("<h3>We are having a conversation with state   " + parentAgentHolder.agentState + "</h3>\n");
				htmlString.append(AgentListener.sendHTMLsubmit());

			} else {
				//if it's not valid then print out in browerer that our request is not valid
				htmlString.append(AgentListener.sendHTMLheader(localPort, NewHost, inLine));
				htmlString.append("You have not entered a valid request!\n");
				htmlString.append(AgentListener.sendHTMLsubmit());		
				
		
			}
			//send output message to terminal console
			AgentListener.sendHTMLtoStream(htmlString.toString(), out);
			
			//at last close the socket
			sock.close();
			
			
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}
	
}
/*
define a new class agent holder with two properties one is serversocket and an integer for agentstate and also use constructor to achieve
to set new serversocket by calling agentholder.
 */
class agentHolder {
	ServerSocket sock;
	int agentState;
	agentHolder(ServerSocket s) { sock = s;}
}

class AgentListener extends Thread {
 	// this is to define a socket type with a variable for agentListerne
 	Socket sock;
 	// define an integer for local port number
	int localPort;
	
	//build a constructor which set value of socket and local portnumber for each agentListener
	AgentListener(Socket As, int prt) {
		sock = As;
		localPort = prt;
	}
    // initialize agentstate value with 0;
	int agentState = 0;
	
	public void run() {

		// define bufferreader and printstream for input and output in my terminal console. 
		BufferedReader in = null;
		PrintStream out = null;
		// set a string newHost parameter with given name localhost
		String NewHost = "localhost";
		//print out to indicate it's in agentListernet right now
		System.out.println("In AgentListener Thread");		
		try {
			String buf;
			//call new to assign a new output and input for bufferReader and printStream
			out = new PrintStream(sock.getOutputStream());
			in =  new BufferedReader(new InputStreamReader(sock.getInputStream()));
			
			//use readline mehtod to read first line and store in string buf.
			buf = in.readLine();
			
			//if buf is not null and in what we read has string [State= then jump into the if loop
			if(buf != null && buf.indexOf("[State=") > -1) {
				//read string between the index of string [State= and ], then we could know the agentstate string
				String tempbuf = buf.substring(buf.indexOf("[State=")+7, buf.indexOf("]", buf.indexOf("[State=")));
				//parse string tempbuf to integer
				agentState = Integer.parseInt(tempbuf);
				//printout agentstate number in my terminal screen
				System.out.println("agentState is: " + agentState);
					
			}
			//printout the first line we read
			System.out.println(buf);
			//define StringBuilder type for HTML repsonse by using api 
			StringBuilder htmlResponse = new StringBuilder();
			// use HTML repsonse to send information including to broswer where sendHTMheader , sendHTMLsubmit method are defined below
			htmlResponse.append(sendHTMLheader(localPort, NewHost, buf));
			htmlResponse.append("Now in Agent Looper starting Agent Listening Loop\n<br />\n");
			htmlResponse.append("[Port="+localPort+"]<br/>\n");
			htmlResponse.append(sendHTMLsubmit());

			sendHTMLtoStream(htmlResponse.toString(), out);
			
			//open a new connection with local port which is given
			ServerSocket servsock = new ServerSocket(localPort,2);
			// create a new agentholder which this connection  serversocket
			agentHolder agenthold = new agentHolder(servsock);
			// and set agentstate value 
			agenthold.agentState = agentState;
			
			// while the connection is running
			while(true) {
				sock = servsock.accept();
				//then we printout a sentense in console to indicate we have a successful connection with given local Port number
				System.out.println("Got a connection to agent at port " + localPort);
				//when connection is created then start a new agentWorker
				new AgentWorker(sock, localPort, agenthold).start();
			}
		
		} catch(IOException ioe) {
			// printout error message when above condition is not satisfied
			System.out.println("Either connection failed, or just killed listener loop for agent at port " + localPort);
			System.out.println(ioe);
		}
	}
	 // this method is to send string in html format with given input information and local port number
	static String sendHTMLheader(int localPort, String NewHost, String inLine) {
		
		StringBuilder htmlString = new StringBuilder();

		htmlString.append("<html><head> </head><body>\n");
		htmlString.append("<h2>This is for submission to PORT " + localPort + " on " + NewHost + "</h2>\n");
		htmlString.append("<h3>You sent: "+ inLine + "</h3>");
		htmlString.append("\n<form method=\"GET\" action=\"http://" + NewHost +":" + localPort + "\">\n");
		htmlString.append("Enter text or <i>migrate</i>:");
		htmlString.append("\n<input type=\"text\" name=\"person\" size=\"20\" value=\"YourTextInput\" /> <p>\n");
		
		return htmlString.toString();
	}
	//this is used to end sendHTMLheader
	static String sendHTMLsubmit() {
		return "<input type=\"submit\" value=\"Submit\"" + "</p>\n</form></body></html>\n";
	}
	///this is used to send information to console with some information to tell differences of different content
	static void sendHTMLtoStream(String html, PrintStream out) {
		
		out.println("HTTP/1.1 200 OK");
		out.println("Content-Length: " + html.length());
		out.println("Content-Type: text/html");
		out.println("");		
		out.println(html);
	}
	
}

public class HostServer {
	// set a static integer value for port we use
	public static int NextPort = 3000;
	// main function with input arguments string array
	public static void main(String[] a) throws IOException {

		// define default port number 1565 to test in mutiple browsers 
		int q_len = 6;
		int port = 1565;
		Socket sock;
		
		// create new serverSocket with given default port and printout message
		ServerSocket servsock = new ServerSocket(port, q_len);
		System.out.println("John Reagan's DIA Master receiver started at port 1565.");
		System.out.println("Connect from 1 to 3 browsers using \"http:\\\\localhost:1565\"\n");
		// while the connection is true 
		while(true) {
			// then increase port number based on we seted port value by 1 each time 
			NextPort = NextPort + 1;
			//start accept socket
			sock = servsock.accept();
			//print out the agentLister message with given port number
			System.out.println("Starting AgentListener at port " + NextPort);
			//and then create new agentListerner for n reponsing requests
			new AgentListener(sock, NextPort).start();
		}
		
	}
}