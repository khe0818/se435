/* MyListener

This is VERY quick and dirty code that leaves workers lying around. But you get the idea.

 */


import java.io.*;  // Get the Input Output libraries
import java.net.*; // Get the Java networking libraries
import java.util.*;

class ListenWorker extends Thread {    // Class definition
  Socket sock;                   // Class member, socket, local to ListnWorker.
  ListenWorker (Socket s) {sock = s;} // Constructor, assign arg s
                                      //to local sock
  static int flags = 0;
  public void run(){
    // Get I/O streams from the socket:
    //System.out.println("this is here"); 
    PrintStream out = null;
    BufferedReader in = null;
    String  fileName = null;
    String type = null;
    try {
      out = new PrintStream(sock.getOutputStream());
      in = new BufferedReader
        (new InputStreamReader(sock.getInputStream()));
      if(MyListener.controlSwitch != true){
        System.out.println("MyListener is shutting down by console");
      }
      else{
        String readFromStream = null;
        //System.out.println("this is here"); 
        try{
          readFromStream = in.readLine();
          System.out.println(readFromStream);
          String[] texts = readFromStream.split(" ");
         // for(int i = 0; i <texts.length; i++){
        //    System.out.println(texts[i]);
        //  }
          if(texts[0].equals("GET") == true){
            type = readFileName(texts[1], type);
            if(texts[1].contains("..") == true) { 
               throw new RuntimeException(); 
            }
            if(flags == 1){
              fileName = readFIle(texts[1],out, type,sock);    
            }
            else if(flags == 2){
            }
            else{
             // addNums(texts[1],out,type, sock);
            }
           // System.out.println("type is :" + type);
           // System.out.println("fileName is:" + fileName);
          }
          else{
            System.out.println("we don't get any file this time please try again");
            throw new RuntimeException();
          }


        }
        catch (IOException x) {
            System.out.println("Nothing we find");
         }
      }
      sock.close();
      //while (true) {
      //  sockdata = in.readLine ();  
     //   if (sockdata != null) System.out.println(sockdata);
    //    System.out.flush ();
    //  }
      //sock.close(); // close this connection, but not the server;
      

    } catch (IOException x) {
      System.out.println("Connetion reset. Listening again...");
   }
  }


  public static String readFileName(String name, String type){
      if(name.endsWith(".txt") == true) { 
          type = "text/plain"; 
          flags = 1;
        } else if(name.endsWith(".html") == true ) { 
          type = "text/html"; 
          flags = 1;
        } else if(name.endsWith("css") == true){
          type = "text/css";
          flags = 1;
        }
         else if(name.endsWith("fake-cgi") == true) { 
          type = "text/html"; 
          flags = 3;
        } else if(name.endsWith("/") == true) {
          type = "text/html";
          flags = 2; 
        } else { // 
          type = "text/plain";
          flags = 1;      
        }
        return type;
  }


   public static String readFIle(String name, PrintStream out, String type, Socket sock) throws IOException {
        String[] names = name.split("/");
      //  for(int i = 0; i <names.length; i++){
      //      System.out.println(names[i]);
      //      }
        String temp = null;
        InputStream in = null;
        temp = names[names.length - 1];
         in = new FileInputStream(name);  // open file

        File file = new File(name);
        out.print("HTTP/1.1 200 OK\n" + "Content-Length: " + file.length() + "\nContent-Type: " + type + "\r\n\r\n"); 
        System.out.println("my server file Name:" + file +"\nContent-Length:" +file.length() + "\nContent-Type" + type); 
        out.flush(); 
        in.close();

       
        
        return temp;
   }
    public static void addnums(String name, PrintStream out, String type, Socket sock) throws IOException{
          String[] temps = name.split("=|&|");
          String username = temps[2];
          int num1 = Integer.parseInt(temps[4]);
          int num2 = Integer.parseInt(temps[6]);
          int sum = num1 + num2;
          
            String result = "";
            result +=  "<html><head><TITLE> addnums sum </TITLE></head>\n<BODY>\n";
            result += "dear" + username + ", the sum of " + num1 + "and " + num2 + " is " +  sum + "\n";
            result += "</BODY></html";

            out.print("dear" + username + ", the sum of " + num1 + "and " + num2 + " is " +  sum);
         



    }   
     

 }


public class MyListener {

  public static boolean controlSwitch = true;

  public static void main(String a[]) throws IOException {
    int q_len = 6; /* Number of requests for OpSys to queue */
    int port = 2540;
    Socket sock;

    ServerSocket servsock = new ServerSocket(port, q_len);

    System.out.println("Kaijun he's listener work at port 2540  .\n");
    while (controlSwitch) {
      // wait for the next client connection:
      sock = servsock.accept();
      new ListenWorker (sock).start(); // Uncomment to see shutdown bug:
      // try{Thread.sleep(10000);} catch(InterruptedException ex) {}
    }
  }
} 