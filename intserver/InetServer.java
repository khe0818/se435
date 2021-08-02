/*--------------------------------------------------------

1. Name: Kaijun He / Date: 01/10/2018

2. Java version used, if not the official version for the class:
    java version "1.8.0_144"

3. Precise command-line compilation examples / instructions:

> javac InetServer.java
> Javac InetClient.java

4. Precise examples / instructions to run this program:

In separate shell windows:

> java InetServer
> java InetClient

5. List of files needed for running the program.

 a. InetServer.java
 b. InetClient.java

5. Notes:

because this code for InetServer and Inetclient is from professor's sample code, so it must be the same. and i add 
up my own comments to those two files for grading. i test the client and server file to using my local internet ip address


----------------------------------------------------------*/


import java.io.*; // import library for input and output
import java.net.*; // import librart for network
class Worker extends Thread { // define a worker class with a constructor with member socket 
	// where the constructor worker will read an input arguments s and assign it to local type socket sock
	Socket sock; 
	Worker (Socket s) {
		sock = s;
	} 
	public void run(){
	 // In run function, intial the out and input stream and sign it to null;
		PrintStream out = null;
		BufferedReader in = null;
		try {
			//assign in and out value by calling sock with getInputStream() and getOutputStream()
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintStream(sock.getOutputStream());
			try {
				String name;
				name = in.readLine (); // read the host name or ip address typing from the terminal window client
				System.out.println("Looking up the name from input " + name);
				printRemoteAddress(name, out);
			} catch (IOException x) { // exception to throw message when could not read server correctly
				System.out.println("Server read error");
				x.printStackTrace (); 
			}
			sock.close(); // close this sock connection
		} catch (IOException ioe) {System.out.println(ioe);}
	}

// printREmoteAddress is used to print out what the server find out and send to client window
	static void printRemoteAddress (String name, PrintStream out) {
		try {
			out.println("Looking up " + name + "...");
			InetAddress machine = InetAddress.getByName (name);
			out.println("Host name : " + machine.getHostName ()); 
			out.println("Host IP : " + toText (machine.getAddress ()));
		} catch(UnknownHostException ex) {
			out.println ("Failed in atempt to look up " + name);
		}
	}

	 // this part is not useful for simple server and client running process
	 static String toText (byte ip[]) { 
		 StringBuffer result = new StringBuffer ();
		 for (int i = 0; i < ip.length; ++ i) {
		 	if (i > 0) 
		 		result.append (".");
		 	result.append (0xff & ip[i]);
		 }
		 return result.toString ();
	 }
}
// main function for InetServer java file, in this case we just make port number same as client java file. 
// then it will be able to commnicate between server and client
public class InetServer {

 	public static void main(String a[]) throws IOException {
	 	int q_len = 6; 
	 	int port = 2000; // port number define by user 
	 	Socket sock;

	 	ServerSocket servsock = new ServerSocket(port, q_len);

	 	System.out.printf("sample Inet server 1.8 starting up, listening at port %d.\n", port);
	 	while (true) {
	 		sock = servsock.accept(); // wait for client to do next lookup
	 		new Worker(sock).start(); // and make a new work to handle the work.
	 	}
 	}
}