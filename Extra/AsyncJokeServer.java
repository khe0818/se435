
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
import java.util.*;

class Worker extends Thread { //define a class worker to creat thread with member socket
    Socket socket;             


    Worker(Socket s)       //consturctor assign s to socket  
    {
        socket = s;
    }

    public void run() {
    	//intinial the input and output stream to null value
        PrintStream out = null;
        BufferedReader in = null;

        try {
        	// sign value to in and out by calling getInputStream() and getOutputStream()
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());
            if(AsyncJokeServer.controlSwitch != true){ // set a boolean flag controlswitch to printout message when server is shutting down
            	System.out.println("server is now shutting down");
            }
            else{// condition when server is on
            	try {
	 	            String username;
	                String  nextOutput;
	                int userID;
	                //  check whether the jokeserver state is mantenance mode or not if yes then write 0, if not write 1 to client
	                if(AsyncJokeServer.serverState == AsyncJokeServer.ServerState.MAINTENANCEMODE){
	                	out.write(0);
	                }
	                else{
	                	out.write(1);
	                }
	                // if it's maintenance mode, then start conversation between client and server
	                if(AsyncJokeServer.serverState != AsyncJokeServer.ServerState.MAINTENANCEMODE){

		                username = in.readLine(); //recieve username from Client

		                userID = in.read(); //get userID from Client
		                if (userID == 0) {  //if userId is 0 then we increase id number by 1 by calling function incrementUserId() function and record down userId 
		                    userID = AsyncJokeServer.incrementUserId(); 
		                }
		               
		                out.write(userID);

		                //get joke index from JokeClient
		                int jokeIndex = in.read();

		                //get proverb index index from JokeClient
		                int proverbIndex = in.read();

		                if (AsyncJokeServer.serverState == AsyncJokeServer.ServerState.JOKEMODE) { //if serverState is in JOKEMODE 
		                	// then replace the placeholder in jokes list with current username
		                    out.println(AsyncJokeServer.Jokes[jokeIndex].replaceAll("Placeholder", username)); 
		                } else{ //if serverState is in PROVERBMODE, then replace the placeholder in proverbs list with current username
		                    out.println(AsyncJokeServer.Proverbs[proverbIndex].replaceAll("Placeholder", username)); 
		                }
		               
		                // printout the current mode is
		                System.out.println("mode is :" + AsyncJokeServer.serverState);
		                int mode = 0; 
		                // create a new integer flag mode
		                //set jokemode to value 1 and proverb mode to value 2,
		                if(AsyncJokeServer.serverState == AsyncJokeServer.ServerState.JOKEMODE){
		                	mode = 1;
		                }
		                else if (AsyncJokeServer.serverState == AsyncJokeServer.ServerState.PROVERBMODE){
		                	mode = 2;
		                }
		                //update joke index and proverb index by calling nexjoke and nextProverb function.	                
		                int newJokeIndex = AsyncJokeServer.nextJoke(jokeIndex, mode); 
		                int newProverbIndex = AsyncJokeServer.nextProverb(proverbIndex, mode); 
		                // using shuffle method to ramdomize the order of jokes after one cycle
		                if (jokeIndex == AsyncJokeServer.Jokes.length - 1) { 
		                    List<String> jokesList = Arrays.asList(AsyncJokeServer.Jokes); 
		                    Collections.shuffle(jokesList);
		                    AsyncJokeServer.Jokes = jokesList.toArray(new String[jokesList.size()]); 
		                }
		                // using shuffle method to ramdomize the order of proverbs after one cycle
		                if (proverbIndex == AsyncJokeServer.Proverbs.length - 1) { 
		                    List<String> proverbsList = Arrays.asList(AsyncJokeServer.Proverbs);
		                    Collections.shuffle(proverbsList); 
		                    AsyncJokeServer.Proverbs = proverbsList.toArray(new String[proverbsList.size()]); 
		                }
		                //update joke index and proverb index in client
		                out.write(newJokeIndex);     
		                out.write(newProverbIndex);  
		                out.write(mode);
		                // if it's shutdown mode,we switch shutdown flag controlswitch to false
		                if(username.indexOf("shutdown") >= 0){
		                	AsyncJokeServer.controlSwitch = false;
		                	System.out.println("we are shutting down the server");

		                }
		                else {
		                	// printout which user is sending request recently
		                	System.out.println("we recieve request from user: " + username + " with an userID(" + userID + ").");
		           		}
	            	}


	            } catch (IOException e) { // throw exceptions and printout server error message
	                System.out.println("Server read error"); 
	                e.printStackTrace(); //print a stack trace exception
	            }
	        }
	            socket.close(); //close this socket connection
	        } catch (IOException e) {
	            System.out.println(e); //throw out input output exception.
	        }

    }
}

public class AsyncJokeServer {

    public static int port = 4545; //default port number

    public DatagramSocket socket;

    public enum ServerState { //define three server states jokemode, proverbmode, and maintenancemode
        JOKEMODE, PROVERBMODE, MAINTENANCEMODE
    }
    public static boolean controlSwitch = true; // flag for shuting down condition

    //4 samples jokes from online resources
	public static String[] Jokes = {"JA Placeholder: Can a kangaroo jump higher than a house? Of course, a house doesn’t jump at all.",
			                         "JB Placeholder: Anton, do you think I’m a bad mother? My name is Paul.",
			                         "JC Placeholder: What is the difference between a snowman and a snowwoman? Snowballs",
			                         "JD Placeholder: My dog used to chase people on a bike a lot. It got so bad, finally I had to take his bike away."};
	// 4 popular proverbs from online resources
	public static String[] Proverbs = {"PA Placeholder: Two wrongs don't make a right.", 
										"PB Placeholder: The pen is mightier than the sword.", 
										"PC Placeholder: When in Rome, do as the Romans.", 
										"PD Placeholder: The squeaky wheel gets the grease"};

    public static ServerState serverState = ServerState.JOKEMODE; //serverstate default mode is jokemode
    //define a nextuserId
    private static int nextUserID = 0;

    //increase userid to make sure each user has uique id number
    public static synchronized int incrementUserId() {
        return ++nextUserID;
    }

    //return next joke index by given mode, if mode changes then save index for current user
    public static int nextJoke(int index, int mode) {
    	if(mode == 1){
	        if (index == AsyncJokeServer.Jokes.length - 1) //if jokeNumber is last joke index
	            return 0; //return 0 for jokeNumber
	        else
	            return index += 1; //else, add 1 to jokeNumber
        }
        else{
        	return index;
        }
    }

    //return next proverb index by given mode, if mode changes then save proverb index for current user
    public static int nextProverb(int index, int mode) {
    	if(mode == 2){
	        if (index == AsyncJokeServer.Proverbs.length - 1) //if proverbNumber is last proverb index
	            return 0; //return 0 for proverbNumber
	        else
	            return index += 1; //else, add 1 to proverbNumber
        }
        else{
        	return index;
        }
    }

    //Change server state by admin client
    public static void changeState(int state) { // define changeState method

        System.out.println("Recieved state is: " + state);

        if (state == 1) //state is 1 then set to jokemode
            AsyncJokeServer.serverState = ServerState.JOKEMODE; 
        else if (state == 2) //if state is 2 then set to proverbmode
            AsyncJokeServer.serverState = ServerState.PROVERBMODE; 
        else if(state == 3) // if state is 3 then set to maintenancemode
        	AsyncJokeServer.serverState = serverState.MAINTENANCEMODE;

        System.out.println("Server state: " + AsyncJokeServer.serverState);
    }

    public static int portNumbers(String args){
        return Integer.parseInt(args);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int q_len = 6; 
        int sleepTime = 40000;
        int sleepTime2 = 70000;
       // System.out.println("args[0]" + args[0]);
        AdminLoop AL = new AdminLoop(); // create new threat for admin client
        Thread t = new Thread(AL);
    
        t.sleep(sleepTime);

        if(args[0] != null){
            port = portNumbers(args[0]);
        }
        else {
            port = 4545;
        }
           // t.sleep(40000);
            t.start();
            Socket socket;

            ServerSocket servsock = new ServerSocket(port, q_len); //creat a new servsock using two parameter port number and length

            System.out.println("Asynchronous JokeServer listening at port:" + port); //print out message for portnumber
            while (controlSwitch) { 
                socket = servsock.accept(); //wait for the next client connection
                if(controlSwitch){
                    new Worker(socket).start(); //start a new work thread
                }
                
            }
        

       
    }
}

    class AdminLoop implements Runnable {
        public static boolean adminControlSwitch = true;

        public void run() { 
            int q_len = 6; 
            int port = 5050;  //Listening at a different port number which is gievn 5050 for Admin client
            Socket socket;

            try {
                ServerSocket servsock = new ServerSocket(port, q_len);
                while (adminControlSwitch) { 
                    //wait for the the connection and accept
                    socket = servsock.accept();
                    new AdminWorker(socket).start(); //start a new admin worker thread
                }
            } catch (IOException e) { //throw input and output exceptions
                System.out.println(e);
            }
        }

    }

    class AdminWorker extends Thread // create admin worker class 
    {
        private Socket socket;

        AdminWorker(Socket s)
        {
            socket = s;
        }

        public void run()
        {
            PrintStream out = null;
            BufferedReader in = null;
            try
            {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintStream(socket.getOutputStream());
                //Note that this branch might not execute when expected
                System.out.println("Joke Admin Client connected.");

                //send serverstate to the server
                out.println(AsyncJokeServer.serverState);

                //read the number of the mode from the client
                int modeNumber = in.read();

                //call changestate() function to switch from defined three mode
                AsyncJokeServer.changeState(modeNumber);

                this.socket.close(); // close socket connection
            }
            catch(IOException x)
            {
                System.out.println("Server read error");
                x.printStackTrace();
            }
        }
    }