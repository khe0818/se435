
import java.io.*;  // Get the Input Output libraries
import java.net.*; // Get the Java networking libraries
import java.lang.Object;
import java.nio.file.spi.FileTypeDetector;
import java.util.* ;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

class ListenWorker extends Thread {    // Class definition
  Socket sock;                   // Class member, socket, local to ListnWorker.
  ListenWorker (Socket s) {sock = s;} // Constructor, assign arg s
                                      //to local sock
  static final byte[] EOL = {(byte) '\r', (byte) '\n'};

  public void run(){
    // Get I/O streams from the socket:
    BufferedReader in = null;
    String ct = "";
    String searchString = "";
    File thedata = null;
    Boolean found = false;

    try {

      //out = new PrintStream(sock.getOutputStream());
      in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      OutputStream out = new BufferedOutputStream(sock.getOutputStream());
      PrintWriter prntout = new PrintWriter(new OutputStreamWriter(out), true);
      FileWriter fWriter = null;
      BufferedWriter writer = null;
      String sockdata;
      String ss[] = null;
      myTests.createAddNums();
      Boolean send = false;

      // Display dir contents on server--------------------
      //MyWebServer.getRootDir();
      //MyWebServer.listFiles();
      // Display dir contents on server--------------------

      while (true) {
        sockdata = in.readLine();
        if (sockdata != null)
        {
          send = false;
          found = false;
          System.out.println("User sent: " + sockdata);
          System.out.flush ();
          if (sockdata != null) ss = sockdata.split(" ");

            // Set variables
            if (sockdata.contains(".txt"))//txt return
            {
              for (int i = 0; i < ss.length; i++)
              {
                if (ss[i].contains(".txt"))
                {
                  searchString = ss[i];
                  found = true;
                  send = true;
                }

              }
            }
            else if (sockdata.contains(".java")) // .java case
            {
              for (int i = 0; i < ss.length; i++)
              {
                if (ss[i].contains(".java"))
                {
                  searchString = ss[i];
                  found = true;
                  send = true;
                }

              }
            }
            else if (sockdata.contains(".html"))//.html return
            {
              for (int i = 0; i < ss.length; i++)
              {
                if (ss[i].contains(".html"))
                {
                  searchString = ss[i];
                  found = true;
                  send = true;
                }
              }
            }
            else if (sockdata.contains(".fake-cgi"))// .process input from addNums
            {
              // Process sockdata username and numbers
              String[] inputs = sockdata.split("=|&| ");

              String name = inputs[2];
              int num1 = Integer.parseInt(inputs[4]);
              int num2 = Integer.parseInt(inputs[6]);

              // Run add nums with input
              File returnFile = myTests.addNums(name,num1,num2);
              // Return output
              thedata = returnFile;

              found = false;
              send = true;
            }
            else if (sockdata.contains("GET") && sockdata.contains("HTTP/1.1") && !sockdata.contains(".txt") && !sockdata.contains("html") && !sockdata.contains(".java")) // complete directory listing
            {
              //parse the dir requests
              String[] tokens = sockdata.split(" ");
              String path = (System.getProperty("user.dir") + tokens[1]);
              //File currentDir = new File("").getAbsoluteFile();
              String dir = myTests.printDir(path);
              thedata = myTests.writeHTMLFile(dir);
              found = false;
              send = true;
            }
            else
            {
              found = false;
              send = false;
            }

            if (send)
            {
                if (found) {thedata = myTests.getFile(searchString);}
                // RETURN CONTENT
                myTests.sendHdrFile(thedata, out);
                prntout.flush();
                out.flush();
              //  MyWebServer.sendAllFiles(out);
              //  prntout.flush();
              //  out.flush();
            }

        }
        else if (sockdata == null)
        {


        }


        sockdata = in.readLine();


      }
      //sock.close(); // close this connection, but not the server;
    } catch (IOException x) {
      System.out.println("Connetion reset. Listening again...");
    }
  }
}

public class myTests {

  public static boolean controlSwitch = true;

  public static void main(String a[]) throws IOException {
    int q_len = 6; /* Number of requests for OpSys to queue */
    int port = 2540;
    Socket sock;

    ServerSocket servsock = new ServerSocket(port, q_len);

    System.out.println("Elliot Trapp's MyWebServer listening at port 2540. Based on: Clark Elliott's Port listener.\n");
    while (controlSwitch) {
      // wait for the next client connection:
      sock = servsock.accept();
      new ListenWorker (sock).start(); // Uncomment to see shutdown bug:
      // try{Thread.sleep(10000);} catch(InterruptedException ex) {}
    }
  }

public static void createAddNums()
{
  String returnStr = "";

  returnStr += "<html>";
  returnStr += "<head><TITLE> sample </TITLE></head>";
  returnStr += "\n";
  returnStr += "<BODY>";
  returnStr += "\n";
  returnStr += "<H1> Addnum </H1>";
  returnStr += "\n";
  returnStr += "\n";
  returnStr += "<FORM method=\"GET\" action=\"http://localhost:2540/cgi/addnums.fake-cgi\">";
  returnStr += "\n";
  returnStr += "\n";
  returnStr += "Enter your name and two numbers:";
  returnStr += "\n";
  returnStr += "\n";
  returnStr += "<INPUT TYPE=\"text\" NAME=\"person\" size=20 value=\"YourName\"><P>";
  returnStr += "\n";
  returnStr += "\n";
  returnStr += "<INPUT TYPE=\"text\" NAME=\"num1\" size=5 value=\"4\"> <br>";
  returnStr += "\n";
  returnStr += "<INPUT TYPE=\"text\" NAME=\"num2\" size=5 value=\"5\"> <br>";
  returnStr += "\n";
  returnStr += "\n";
  returnStr += "<INPUT TYPE=\"submit\" VALUE=\"Submit Numbers\">";
  returnStr += "\n";
  returnStr += "</FORM> </BODY></html>";

  FileWriter fWriter = null;
  BufferedWriter writer = null;
  File file = new File(System.getProperty("user.dir"));
  File newHtmlFile = new File("./addnums.html");
  try
  {
      fWriter = new FileWriter(newHtmlFile);
      writer = new BufferedWriter(fWriter);
      writer.write(returnStr);
      writer.close();
  } catch (Exception e) {

}
}

public static File addNums(String name, int num1, int num2)
{
  int answer = num1 + num2;
  String returnStr = "";

  returnStr += "<html><head><TITLE> AddNum Results </TITLE></head>\n<BODY>\n";
  returnStr += name + " the answer to " + num1 + " + " + num2 + " is " + answer +"\n";
  returnStr += "</BODY></html";

  File returnFile = writeHTMLFile(returnStr);
  return returnFile;

}


public static void getRootDir() {
  File f1 = new File("").getAbsoluteFile();
    try{
      String directoryRoot = f1.getCanonicalPath();
      System.out.print("Directory root is: " + directoryRoot);
    }catch (Throwable e){e.printStackTrace();}
  }


// Simply prints out the directory on the current system
public static void listFiles() {

    String filedir ;
    // Create a file object for your root directory

    // E.g. For windows:    File f1 = new File ( "C:\\temp" ) ;

    // For Unix:
    File f1 = new File("").getAbsoluteFile();

    // Get all the files and directory under your diretcory
    File[] strFilesDirs = f1.listFiles ( );

    for ( int i = 0 ; i < strFilesDirs.length ; i ++ ) {
      if ( strFilesDirs[i].isDirectory ( ) )
	System.out.println ( "Directory: " + strFilesDirs[i] ) ;
      else if ( strFilesDirs[i].isFile ( ) )
	System.out.println ( "File: " + strFilesDirs[i] +
			     " (" + strFilesDirs[i].length ( ) + ")" ) ;
    }
  }

public static void sendAllFiles(OutputStream out)
{
  String filedir;
  // Create a file object for your root directory

  // E.g. For windows:    File f1 = new File ( "C:\\temp" ) ;

  // For Unix:
  File f1 = new File(System.getProperty("user.dir"));

  // Get all the files and directory under your diretcory
  File[] strFilesDirs = f1.listFiles ( );

  for ( int i = 0 ; i < strFilesDirs.length ; i ++ ) {
    if ( (strFilesDirs[i].isFile ( ) ) && (strFilesDirs[i].getName().contains(".html") || strFilesDirs[i].getName().contains(".txt") || strFilesDirs[i].getName().contains(".java")) )
     {
       sendHdrFile(strFilesDirs[i],out);
     }
     else if (strFilesDirs[i].isDirectory())
     {
       sendHdrFile(strFilesDirs[i],out);
     }
  }
}

// Utility to format and print the current directory
public static String printDir(String path)
{
  File directory = null;
  File file = null;
  String returnStr = "";

  File f1 = new File(path);//.getAbsoluteFile(); //currentDir;
    try{
      String directoryRoot = f1.getCanonicalPath();
    }catch (Throwable e){e.printStackTrace();}

    // Get all the files and directory under your diretcory
    File[] strFilesDirs = f1.listFiles ( );

    //File f1 = new File(".");
    //(System.getProperty("user.dir"))
    //File xyz = new File("").getAbsoluteFile()

  // Print HTML header
  returnStr +=("<pre>");
  returnStr +=("\n");
  returnStr +=("\n");

  returnStr +="<h1>Index of /" + f1.getName() + "/" +  " </h1>";
  returnStr +=("\n");
  //returnStr +=("<a href=" + "\""+ "http://localhost:2540/" + f1.getName() + "\"" +  ">" + " Parent Directory " + "</a> <br>");
  returnStr +=("<a href=" + "\""+ f1.getParentFile().getName() + "\"" +  ">" + " Parent Directory " + "</a> <br>");


  returnStr +=("\n");

  // FILES
  for ( int i = 0 ; i < strFilesDirs.length ; i ++ ) {
    if ( (strFilesDirs[i].isFile ( ) ) &   (strFilesDirs[i].getName().contains(".html") || strFilesDirs[i].getName().contains(".txt") || strFilesDirs[i].getName().contains(".java")) )
    {
      // Format a file
      file = strFilesDirs[i];
      //returnStr +=("\n");
      returnStr +=("<a href=\"" + file.getName() + "\">" +file.getName() + "</a> <br>");
      returnStr +=("\n");

    }
  }

  // SUBDIRECTORIES
  for ( int i = 0 ; i < strFilesDirs.length ; i ++ ) {
    if ( strFilesDirs[i].isDirectory ( ) )
    {
      // Format a subdirectory
      directory = strFilesDirs[i];
      //returnStr += "\n";
      returnStr +=("\n");
      returnStr +=("<a href=" + "\"" + directory.getName() + "\"" +  ">" +  directory.getName()  + "</a> <br>");
      //returnStr +=("<a href=" + "\""+ "http://localhost:2540/" + directory.getName() + "\"" + ">" + directory.getName() + "/</a> <br>");
      returnStr +=("\n");
    }
  }

  return returnStr;
/* Template
<pre>
<h1>Index of /elliott/435/.xyz</h1>
<a href="/elliott/435/">Parent Directory</a> <br>
<a href="dog.txt">dog.txt</a> <br>
<a href="cat.html">cat.html </a><br>
<a href="MyWebServer.class">MyWebServer.class</a><br>
<a href="z-directory/">z-directory/</a><br>
*/
}

// Parse headers
public static String parseHeader(String input)
{
  return null;
}

// Dynamically construct MIME headers
public static void sendHdrFile(File file, OutputStream out)
{
  PrintWriter prntout = new PrintWriter(new OutputStreamWriter(out), true);
  //static final byte[] EOL = {(byte) '\r', (byte) '\n'};
  long length = 0;
  String type = "";
  String returnStr = "";
  Boolean dir = false;
  if (file.isDirectory()) {dir = true;}
  if (!dir) {length = file.length() + 1;}

if (!dir)
{
  if (file.getName().contains(".html")) {type = "text/html";}
  else if (file.getName().contains(".txt") || file.getName().contains(".java")) {type = "text/plain";}
  else {System.out.println("ERROR: Bad file input");}
}
else if (dir)
{
  type = "text/html";
}

  returnStr += "HTTP/1.1 200 OK";
  returnStr += "\n";
  returnStr += "Content-Length: " + length;
  returnStr += "\n";
  returnStr += "Content-Type: " + type;
  returnStr += "\n";

  try {
  prntout.println(returnStr);
  out.write(ListenWorker.EOL);

  InputStream send = new FileInputStream(file);
  sendFile(send, out);

} catch (IOException e) {System.out.println("ERROR: IOException caught.");}
}

// Utility to easily send the directory to the client
public static void sendDir(OutputStream out)
{
  // Display dir contents on client-----------------
  // print directory
  FileWriter fWriter = null;
  BufferedWriter writer = null;
  File file = new File(System.getProperty("user.dir"));
  String dir = myTests.printDir("");

  File newHtmlFile = writeHTMLFile(dir);

  try
  {
  InputStream dirFile = new FileInputStream(newHtmlFile);
  myTests.sendFile(dirFile,out);
} catch (FileNotFoundException e) {}
  // Display dir contents on client-----------------
}

// Gets a specific file by searching through the current working directory
public static File getFile (String fileName)
  {
    File f1 = new File (System.getProperty("user.dir"));
    File returnFile = null;

    // Get all the files and directory under your diretcory
    File[] strFilesDirs = f1.listFiles ( );

    for ( int i = 0 ; i < strFilesDirs.length ; i ++ )
    {
      if ( strFilesDirs[i].isFile() )
      {
        if (strFilesDirs[i].toString().contains(fileName)) {returnFile = strFilesDirs[i];}
      }
    }
    return returnFile;
  }


// Utility to just send a file
public static void sendFile(InputStream file, OutputStream out)
    {
        try {
            byte[] buffer = new byte[1000];
            while (file.available()>0)
                out.write(buffer, 0, file.read(buffer));
        } catch (IOException e) { System.err.println(e); }
    }

// Utility just to turn a string into a file
public static File writeHTMLFile(String input)
{
  FileWriter fWriter = null;
  BufferedWriter writer = null;
  File newHtmlFile = new File("./new.html");
  try
  {
      fWriter = new FileWriter(newHtmlFile);
      writer = new BufferedWriter(fWriter);
      writer.write(input);
      writer.close();
  } catch (Exception e) {
    //catch any exceptions here
  }

  return newHtmlFile;
}

}



