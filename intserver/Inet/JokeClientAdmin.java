
/*--------------------------------------------------------

1. Name: Kaijun He / Date: 01/20/2018

2. Java version used, if not the official version for the class:
    java version "1.8.0_144"

3. Precise command-line compilation examples / instructions:

> javac JokeClient.java
> javac JokeServer.java
> javac JokeClientAdmin.java

4. Precise examples / instructions to run this program:

In separate shell windows:

> java JokeClient
> java JokeServer
> java JokeClientAdmin

5. List of files needed for running the program.

 a. JokeClient.java
 b. JokeServer.java
 c. JokeClientAdmin.java

5. Notes:

i have some problem in jokeclientAdmin, if you want quit the client, press q when u 
see Press enter to see state of server or q to quit:  and afte pop out another message just press enter
then you could leave. for the jokeserver file, if you want to quit the file, you have to use control c to
kill it by yourself.


----------------------------------------------------------*/

import java.io.*;
import java.net.*;

public class JokeClientAdmin
{
    private final static int port = 5050; // use given port number from handout
    public static void main(String[] args)
    {
        String serverName;
        if(args.length == 0) //if there is no arguement, then suppose to use local host 
            serverName = "localhost";
        else {// if not then use args[0] instead
            serverName = args[0]; 
        }
        // printout admin client servername and port number
        System.out.println("Joke Admin Client");
        System.out.println("Using server: " + serverName + " Port: " + port); 
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
       
        try
        {
            	String name;
            do
            {
            	
                System.out.println("Press enter to see state of server or q to quit: ");
                name = in.readLine(); //get what user type in, either check the mode or quit the client
                 
        		Socket socket;
		        BufferedReader fromServer;
		        PrintStream toServer;

		        try
		        {
		            socket = new Socket(serverName, port); // create new socket for this connection
		            fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
		            toServer = new PrintStream(socket.getOutputStream()); //send output stream to server

		            BufferedReader input = new BufferedReader(new InputStreamReader(System.in)); // read input to bufferreader
		        	String state = fromServer.readLine();
		            System.out.println( "Server mode: " + state); //printout and let user to decide three 3 mode
		            System.out.println("press 1 for JokeMODE, press 2 for proverbMode, press 3 for maintenanceMode");
		            int mode = input.read();
		            mode -= 48;
		            toServer.write(mode);
		            toServer.flush();
		            
		        }
		        catch(IOException e) //throw input and output exception
		        {
		            System.out.println("Socket read error");  //print socket read error and exceptions
		            e.printStackTrace(); 
		        }
		    

            } while(name.indexOf("q") < 0); //if user type in q as quit to cancel request

            System.out.println("user has cancelled the request by user himself"); //print message when quit
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
      
        }

}