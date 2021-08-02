/*--------------------------------------------------------

1. Name / Date: Kaijun He 2/17/2018

2. java version "1.8.0_144"
Java(TM) SE Runtime Environment (build 1.8.0_144-b01)
Java HotSpot(TM) 64-Bit Server VM (build 25.144-b01, mixed mode)

3. 

> javac Blockchain.java


4. 

> java Blockchain 0
>java Blockchain 1
>java Blockchain 2


5. 
hecklist-block.html
Blockchain.java
BlockchainLog.txt
BlockchainLedgerSample.xml
BlockInput0.txt, BlockInput1.txt, BlockInput2.txt

5. Notes:

I only few functions of this labs and i still a bit confused about the blockchain output, reading instruction of
programming assignments, but i still can't get what i should do. in this assignment, i only achieve run individual process to generate
block chian, not three process corporate.
in this assignment i also use a lot code from professor's resources.
http://condor.depaul.edu/elliott/435/hw/programs/Blockchain/BlockH.java
http://condor.depaul.edu/elliott/435/hw/programs/Blockchain/WorkA.java
http://condor.depaul.edu/elliott/435/hw/programs/Blockchain/BlockInputE.java

----------------------------------------------------------*/


import java.sql.Timestamp;
import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.omg.CORBA.portable.InputStream;

import java.io.StringWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.print.DocFlavor.URL;

import java.security.*;
import java.util.*;
import java.text.*;
import java.security.MessageDigest; 


@XmlRootElement
class BlockRecord{
	
	// declare a few fields for blockrecord which will be used below for creating block chain
	String SHA256String;
	  String SignedSHA256;
	  String BlockID;
	  String VerificationProcessID;
	  String CreatingProcess;
	  String PreviousHash;
	  String Fname;
	  String Lname;
	  String SSNum;
	  String DOB;
	  String Diag;
	  String Treat;
	  String Rx;
	  String hash;
	  String previousHash;
	  String data; 
	  long timeStamp;
	  String randomKey;
	  
	//get random keys from print work part which decide when first 16 bits convert to decimal, compare to 20000 value
	// then we could find a random seed which will be used to generate SHA256 String
	  public String getRandomKey() {return randomKey;}  
	  @XmlElement
	  public void setRandomKey(String RK) {this.randomKey = RK;}
	  // getPreviousHash is used to store the value of previoushash which should be same as digital signature of last block
	  public String getPreviousHash() {return previousHash; }
	  @XmlElement
	  public void setPreviousHash(String PH) {this.previousHash = PH; }
	  public String getHash() {return hash;}
	  @XmlElement
	  public void setHash(String HH) {this.hash = HH;}
	  // used to store each line of data which i read from the given txt file
	  public String getData() {return data;}
	  @XmlElement
	  public void setData(String DA) {this.data = DA;}
	  // this function is to get the current system time to generate my sha256 key value
	  public Date getTimeStamp() {return new Timestamp(System.currentTimeMillis());
}
	  @XmlElement
	  public void setTimeStamp(long TS) {this.timeStamp = TS;}  
	  // this function is used to find out digital signature which i will use three item to generate this timestamp, data in file and previoushash
	  public String getSHA256String() {return SHA256String;}
	  @XmlElement
	    public void setSHA256String(String SS){this.SHA256String = SS;}
	  // this is the signed sha256 value i'm not sure it's correct or not, i check the output file, it looks like hex bits
	  public String getSignedSHA256() {return SignedSHA256;}
	  @XmlElement
	    public void setSignedSHA256(String SH){this.SignedSHA256 = SH;}
	    // used to create process for mutiple process corporate
	  public String getCreatingProcess() {return CreatingProcess;}
	  @XmlElement
	    public void setCreatingProcess(String CP){this.CreatingProcess = CP;}
	   // this is used to get verificationed ID after my work verify the blocks in each process and print it out into xml file to show that the process blocks
	    // has been verificated
	  public String getVerificationProcessID() {return VerificationProcessID;}
	  @XmlElement
	    public void setVerificationProcessID(String VID){this.VerificationProcessID = VID;}
	    // this is used to generate a random Block ID
	  public String getBlockID() {return BlockID;}
	  @XmlElement
	    public void setBlockID(String BID){this.BlockID = BID;}
	    // this is used to read file and store social society number 
	  public String getSSNum() {return SSNum;}
	  @XmlElement
	    public void setSSNum(String SS){this.SSNum = SS;}
	    // same as above it's used to sotre first name by reading files
	  public String getFname() {return Fname;}
	  @XmlElement
	    public void setFname(String FN){this.Fname = FN;}
	    // this is used to store last name from files by reading files in samples .txt files
	  public String getLname() {return Lname;}
	  @XmlElement
	    public void setLname(String LN){this.Lname = LN;}
	    // this is date of birthday which to store in my blockRecord
	  public String getFDOB() {return DOB;}
	  @XmlElement
	    public void setFDOB(String DOB){this.DOB = DOB;}
	    // this is used to store diagnosis of this person from files given in samples
	  public String getGDiag() {return Diag;}
	  @XmlElement
	    public void setGDiag(String D){this.Diag = D;}
	    // this is used to store the treat plan from the files given in samples
	  public String getGTreat() {return Treat;}
	  @XmlElement
	    public void setGTreat(String D){this.Treat = D;}
	  public String getGRx() {return Rx;}
	  @XmlElement
	    public void setGRx(String D){this.Rx = D;}
	  
	 

	  

	}

// this is main blockchain file to do all the steps below like apply alogrithm of SHA-256, generate random blockId, verify whether block chain is builded
	// generally i use this block's previoushash compare with last block's digital signatural, if it's same, then i will decide the blockchain is valid

public class Blockchain {
	 // declare few variables which will be use in following programs.
	 private static String FILENAME;
	  private static final int iFNAME = 0;
	  private static final int iLNAME = 1;
	  private static final int iDOB = 2;
	  private static final int iSSNUM = 3;
	  private static final int iDIAG = 4;
	  private static final int iTREAT = 5;
	  private static final int iRX = 6;
	  private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	  static int processOne = 0;
	  static int processTwo = 0;
	  static int processThree = 0;
	  static boolean flag = false;
	  
	  static BlockRecord[] t;
	  
	  public void loadAll(BlockRecord[] total) {
		  total  = new BlockRecord[100];	  
	  }
// this is use SHA-256 alogrithm to get my digital signature, hash and so on.
  public static String applySha256(String input){		
		try {
			// this is from sample code which is given by professor to use SHA-256 alogrithm to my input
			MessageDigest digest = MessageDigest.getInstance("SHA-256");	        
			byte[] hash = digest.digest(input.getBytes("UTF-8"));	        
			StringBuffer hexString = new StringBuffer(); //this is string buffer to generate hex bits long string
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0'); 
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
  // generate random string with alpha letter and numbers, which i will use it to conscat my data from files
	// and this random generated alpha numeric 
  public static String randomAlphaNumeric(int count) {
	    StringBuilder builder = new StringBuilder();
	    while (count-- != 0) {
	      int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
	      builder.append(ALPHA_NUMERIC_STRING.charAt(character));
	    }
	    return builder.toString();
	  }
  
   public static void addData(File input, File output) throws JAXBException, IOException {
   			FileInputStream instream = null;
			FileOutputStream outstream = null;
 
    	try{
    	    F
 
    	    instream = new FileInputStream(input);
    	    outstream = new FileOutputStream(output);
 
    	    byte[] buffer = new byte[1024];
 
    	    int length;

    	    while ((length = instream.read(buffer)) > 0){
    	    	outstream.write(buffer, 0, length);
    	    }
    	    instream.close();
    	    outstream.close();

    	    System.out.println("File copied successfully!!");
 
    	}catch(IOException ioe){
    		ioe.printStackTrace();
    	 }
	    
		  
   }
  
  
  
  // this is main files to run the whole programs
  public static void main(String[] args) throws Exception {
    // define some integer variables will be used later
    int pnum;
    int UnverifiedBlockPort;
    int BlockChainPort;
    int PublicKeyPort;
    char state;

  // this is used to run code when java blockchian with 0, 1, 2 which means to run file 0, file 1 or file 2
    if (args.length < 1) pnum = 0;
    else if (args[0].equals("0")) pnum = 0;
    else if (args[0].equals("1")) pnum = 1;
    else if (args[0].equals("2")) pnum = 2;
    else pnum = 0; 
    
    if(pnum == 0) {
    		flag = false;
    		processOne = 0;
    		processTwo = 0;
    		processThree = 0;
    }
    else if(pnum == 1) {
    		processTwo = 1;
    		flag = false;
    }
    else if (pnum == 2) {
    		processThree = 2;
    		flag = true;
    }
    
    // as meantioned in the files in programming assignments, public key port will use 4710,4711,4712
    //unverifiedBlockport will use 4820,4821,4822 , the blockchian port will be 4930,4931,4932. to avoid 
    // conflite of using same port for different processes.
    PublicKeyPort = 4710 + pnum;
    UnverifiedBlockPort = 4820 + pnum;
    BlockChainPort = 4930 + pnum;
    // system printout the porcess number with three different port numbers.
    System.out.println("Process number: " + pnum + " Ports: " + PublicKeyPort + " " + UnverifiedBlockPort + " " + 
		       BlockChainPort + "\n");
  //. switch condition of file names to decide which file will be used to read
    switch(pnum){
    case 1: FILENAME = "BlockInput1.txt"; break;
    case 2: FILENAME = "BlockInput2.txt"; break;
    default: FILENAME= "BlockInput0.txt"; break;
    }
    // printing out which file has been used to read data
    System.out.println("Using input file: " + FILENAME);
    // create a new file in same directory to load output xml into created new file
    File newTextFile = new File("BlockchainLedgerSample.xml");
    // use file writer function from java given library to write this file
    FileWriter fw = new FileWriter(newTextFile);
    // use buffer reader to read files line by line
    try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
    	// declare tokens for reading one line data which is used to genereate one block
    	String[] tokens = new String[10];
    	// define some varibles to store given string and id
    	String stringXML;
    	String InputLineStr;
        String suuid;
    	UUID idA;
    	// create a new block records with 20 limit
    	BlockRecord[] blockArray = new BlockRecord[20];
    	//it's used to access JAXB API whcih could allow to use marshal, unmarshal and so on
    	JAXBContext jaxbContext = JAXBContext.newInstance(BlockRecord.class);
    	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    	StringWriter sw = new StringWriter();
    	
    	// used to print out put clearly and functionaly
    	jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
          	
    	int n = 0;
    	int m = 0;
    	// read each line from file, if null then break and stop it
    	while ((InputLineStr = br.readLine()) != null) {
    		   if(n == 0) {
    			   m = 0;
    		   }
    		   else {
    			   m = n -1;
    		   }

    		   // each file create new block record data
    	         blockArray[n] = new BlockRecord();
    	         // first set data of each line by changing array index of blockarray
    	         blockArray[n].setData(InputLineStr);
    	         // generate my own digital signatural in long hex string
              blockArray[n].setSHA256String(applySha256(blockArray[n].getPreviousHash() + blockArray[n].getTimeStamp() + blockArray[n].getData()) );
              if(n == 0) {
              	// as we know that first block doesn't has previoushash according to its properties
              blockArray[n].setPreviousHash("0");
              }
              else {
              	//set previoushash of current block is last block's digital signature, which is hash value
            	  blockArray[n].setPreviousHash(blockArray[m].getSHA256String());
              }
              //this is my work fucntion which insert one block array            
              printWork(blockArray[n]);
               // generate signed SHA256 value after we do print work function we just convert first 16 bits hex value to decimal which will be number
              // from 0000 to 65535. if less than 20000, then we record down this token and make it as sign SHA256 value as output of samples
              blockArray[n].setSignedSHA256(blockArray[n].getRandomKey());
              System.out.println("RandomKey >:" + blockArray[n].getRandomKey());
              System.out.println("signed >:" + blockArray[n].getSHA256String());
              System.out.println("previousehash:" + blockArray[n].getPreviousHash());


            // System.out.println("data is :"+ blockArray[n].getData());
           //this is used to create process which is used to create xml with all properties I assigned below in order
    	  idA = UUID.randomUUID();
    	  suuid = new String(UUID.randomUUID().toString());
    	  blockArray[n].setBlockID(suuid);
    	  blockArray[n].setCreatingProcess("Process" + Integer.toString(pnum));
    	  blockArray[n].setVerificationProcessID("Process: " + Integer.toString(pnum));
    	  tokens = InputLineStr.split(" +"); 
    	  blockArray[n].setSSNum(tokens[iSSNUM]);
    	  blockArray[n].setFname(tokens[iFNAME]);
    	  blockArray[n].setLname(tokens[iLNAME]);
    	  blockArray[n].setFDOB(tokens[iDOB]);
    	  blockArray[n].setGDiag(tokens[iDIAG]);
    	  blockArray[n].setGTreat(tokens[iTREAT]);
    	  blockArray[n].setGRx(tokens[iRX]);
    	  // increase n value when reach end of line.
    	      	  addAll(n,blockArray[n],flag);

    	  n++;
    	}
   		    	

    	System.out.println(n + " how many recors has been read.");
    	System.out.println(" full names:");
    	// print out first name and last name of each line data of one block
    	for(int i=0; i < n; i++){
    	  System.out.println("  " + blockArray[i].getFname() + " " +
    			     blockArray[i].getLname());
    	}
    	System.out.println("\n");
    	stringXML = sw.toString();
    	// using JAXB api method marshal to print blockarrays's infomation stored
    	for(int i=0; i < n; i++){
    	  jaxbMarshaller.marshal(blockArray[i], sw);
    	}

    	// create xml files as rules
    	String fullBlock = sw.toString();
    	String XMLHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
    	String cleanBlock = fullBlock.replace(XMLHeader, "");
    	
    	String XMLBlock = XMLHeader + "\n<BlockLedger>" + cleanBlock + "</BlockLedger>";
    	System.out.println(XMLBlock);
    	fw.write(XMLBlock);
          } catch (IOException e) {e.printStackTrace();}
          // close file writer after we finish all the lines read
    	fw.close();
	
    
  
  }
  
  // work fucntion which is used to concatentate data and randome gerneated alpha letter and numbers combined togethers
  public static void printWork(BlockRecord b) {
  	  // for each block record data, we store digtial signature and real data from each line
	  String hashs = b.getSHA256String();
	  String data = b.getData();
	  //declare randome stirng variable
	  String randString = null;
	  int workNumber = 0;
	  // declare stringout string as null
	  String stringOut = null;
	    try {
	    	
	        for(int i=1; i<20; i++){ 
	        	// generate random seed string by call randomALphaNumeric function which will gerneat alpha letter and numbers combined togethers
			  	randString = randomAlphaNumeric(8); 
			  	// combine given data from real file and my seed behind 
			  	String concatString = data + randString; 
			  	// then printout the concatstring string
			  	System.out.println("Concatenated: " + concatString);
			  	// by using SHA-256 algorithem gerenate new hash value
			  	MessageDigest MD = MessageDigest.getInstance("SHA-256");
			  	byte[] bytesHash = MD.digest(concatString.getBytes("UTF-8")); 
			  	stringOut = DatatypeConverter.printHexBinary(bytesHash);
			  	System.out.println("Hash is: " + stringOut);
			  	workNumber = Integer.parseInt(stringOut.substring(0,4),16); 
			  	//convert number of first 16 bits into integers which will be used to compare to we defined work number, to decide it's valid or not for block chain
			  	System.out.println("First 16 bits " + stringOut.substring(0,4) +": " + workNumber + "\n");
			  	if (workNumber < 20000){ 
			  	  System.out.println("Puzzle solved!");
			  	  System.out.println("The seed was: " + randString);
			  	  // set random seed value and store into blockRecord.
			  	  b.setRandomKey(randString);
			  	  break;
			  	}
	        }
	      }catch(Exception ex) {ex.printStackTrace();}
	  
	  
  }
 
  
  
}