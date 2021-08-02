
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


public class AsyncJokeClient {
    // define joke index, proverb index to represent which joke or proverb will be printout right now
    private static int userID = 0;
    private static int jokeIndex = 0;
    private static int proverbIndex = 0;
    private static int port = 2020;
    private static int jokeServerMode;
    public static String username;
    public static void main(String[] args) {
        String serverName;
        String username;
        // try to check the args input from command line, if no arguement recieved we suppose it's localhost
        if (args.length == 0) { 
            serverName = "localhost";
        } else {
            serverName = args[0]; 
        }
        // use scanner to get input from keyboard type in
        Scanner input = new Scanner(System.in);
        System.out.println("Please type in your username: "); 
        username = input.nextLine(); //read input from keyboard for username,
        System.out.println("\n This Is " + username + "'s Joke Client\n"); 
        System.out.println("Server: " + serverName + ", Port: " + port); //printout the server name and port number for this client
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); // using buffer to read system input
        try { // try and catch to throw exceptions
            String name;
            do { // do while loop to consider the quit condition
                System.out.println("Press enter of keyboard to print out jokes and proverbs or type q to quit : "); //print out to client window
                System.out.flush(); 

                name = in.readLine();
                if (name.indexOf("q") < 0) {  // when readed text is not q, then call getRemotedAddress function
                    getRemoteAddress(username, serverName); 
                }
            } while (name.indexOf("q") < 0);  
            System.out.println("user cancelled the request by himself."); //print out text to indicate that user canceled the operation in client         
        }
        catch (IOException e) { //throw an exception
            e.printStackTrace(); //print stacktrace
        }
    }

    static void getRemoteAddress (String username, String serverName) {
        Socket socket;
        BufferedReader fromServer;
        PrintStream toServer;
        boolean flag = false;
        try {
            //create a new type socket object socket
            socket = new Socket(serverName, port);
            flag = false;
           // System.out.println("it's connected or not " + socket.isConnected());

            //Create filter input and outpu streams for the socket
            fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream())); //get input stream from server by calling  getInputstream()
            toServer = new PrintStream(socket.getOutputStream()); //sending out output form stream to server by calling getOutputStream()

             int maintain = fromServer.read(); // set a integer flag for maintanance mode
             if(maintain > 0){ // if it's not in maintainance mode, then 
                toServer.println(username); // send username to server
                toServer.flush(); 
                toServer.write(userID); //write userid for my user in client
                userID = fromServer.read(); //wait for server give back userId
                toServer.write(jokeIndex); // send the index of jokes and proverb to system
                toServer.write(proverbIndex);
                System.out.println(fromServer.readLine()); // pritnout what recieved from server

                //get jokes and proverb index from server side
                jokeIndex = fromServer.read();
                proverbIndex = fromServer.read();

                //get joke server mode
                jokeServerMode = fromServer.read();
            }
            else{// when in maintannace mode, printout system message to notify that what joke server mode is
            	System.out.println("system is in maintanance mode");
            }

                // joke index is from 0 to 3 for 4 jokes, so when joke index go back to 0 again, printout we finish a joke cycle
                if (jokeIndex == 0 && jokeServerMode == 1) {
                    System.out.println("JOKE CYCLE COMPLETED"); 
                }
                // proverb index is from 0 to 3 for 4 proverbs, so when proverb index go back to 0 again, printout we finish a proverb cycle
                if (proverbIndex == 0 && jokeServerMode == 2) {
                    System.out.println("PROVERB CYCLE COMPLETED"); 
                }

            socket.close(); //close socket connection
        }
        catch (IOException x) { //throw out input and output exception and printout socket error message
            flag = true;
            while(flag){
                System.out.println("since connection is not connected");
                System.out.println("Enter numbers to sum:");
                try{
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
                String input = in.readLine();
                String[] numbers = input.split(" ");
                int sum = Integer.parseInt(numbers[0]) + Integer.parseInt(numbers[1]);
                System.out.println("Your sum is: " + sum);
              }catch(IOException e){
                break;
              }
            }
           // System.out.println("Socket error."); 
           // x.printStackTrace(); 
        }

    }
}