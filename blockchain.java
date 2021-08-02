import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.StringWriter;
import java.io.StringReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.*;
import java.util.StringTokenizer;

/* CDE Some other uitilities: */

import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.text.*;

/*  Starting point for the BlockRecord:

<?xml version="1.0" encoding="UTF-8"?>      
<BlockRecord>
  <SIGNED-SHA256> [B@5f150435 </SIGNED-SHA256> <!-- Verification procees SignedSHA-256-String  -->
  <SHA-256-String> 63b95d9c17799463acb7d37c85f255a511f23d7588d871375d0119ba4a96a </SHA-256-String>
  <!-- Start SHA-256 Data that was hashed -->
  <VerificationProcessID> 1 </VerificationProcessID> <!-- Process that is verifying this block, for credit-->
  <PreviousHash> From the previous block in the chain </PreviousHash>
  <Seed> Your random 256 bit string </Seed> <!-- guess the value to complete the work-->
  <BlockNum> 1 </BlockNum> <!-- increment with each block prepended -->
  <BlockID> UUID </BlockID> <!-- Unique identifier for this block -->
  <SignedBlockID> BlockID signed by creating process </SignedBlockID> <!-- Creating process signature -->
  <CreatingProcessID> 0 </CreatingProcessID> <!-- Process that made the ledger entry -->
  <TimeStamp> 2017-09-01.10:26:35 </TimeStamp>
  <DataHash> The creating process SHA-256 hash of the input data </DataHash> <!-- for auditing if Secret Key exposed -->
  <FName> Joseph </FName>
  <LName> Ng </LName>
  <DOB> 1995.06.22 </DOB> <!-- date of birth -->
  <SSNUM> 987-65-4321 </SSNUM>
  <Diagnosis> Measels </Diagnosis>
  <Treatment> Bedrest </Treatment>
  <Rx> aspirin </Rx>
  <Notes> Use for debugging and extension </Notes>
<!-- End SHA-256 Data that was hashed -->
</BlockRecord>
------------------------------------------------------------------------*/

@XmlRootElement
class BlockRecord{
  /* Examples of block fields: */
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

  /* Examples of accessors for the BlockRecord fields. Note that the XML tools sort the fields alphabetically
     by name of accessors, so A=header, F=Indentification, G=Medical: */

  public String getASHA256String() {return SHA256String;}
  @XmlElement
    public void setASHA256String(String SH){this.SHA256String = SH;}

  public String getASignedSHA256() {return SignedSHA256;}
  @XmlElement
    public void setASignedSHA256(String SH){this.SignedSHA256 = SH;}

  public String getACreatingProcess() {return CreatingProcess;}
  @XmlElement
    public void setACreatingProcess(String CP){this.CreatingProcess = CP;}

  public String getAVerificationProcessID() {return VerificationProcessID;}
  @XmlElement
    public void setAVerificationProcessID(String VID){this.VerificationProcessID = VID;}

  public String getABlockID() {return BlockID;}
  @XmlElement
    public void setABlockID(String BID){this.BlockID = BID;}

  public String getFSSNum() {return SSNum;}
  @XmlElement
    public void setFSSNum(String SS){this.SSNum = SS;}

  public String getFFname() {return Fname;}
  @XmlElement
    public void setFFname(String FN){this.Fname = FN;}

  public String getFLname() {return Lname;}
  @XmlElement
    public void setFLname(String LN){this.Lname = LN;}

  public String getFDOB() {return DOB;}
  @XmlElement
    public void setFDOB(String DOB){this.DOB = DOB;}

  public String getGDiag() {return Diag;}
  @XmlElement
    public void setGDiag(String D){this.Diag = D;}

  public String getGTreat() {return Treat;}
  @XmlElement
    public void setGTreat(String D){this.Treat = D;}

  public String getGRx() {return Rx;}
  @XmlElement
    public void setGRx(String D){this.Rx = D;}

}


public class BlockInputE {

  private static String FILENAME;

  /* Token indexes for input: */
  private static final int iFNAME = 0;
  private static final int iLNAME = 1;
  private static final int iDOB = 2;
  private static final int iSSNUM = 3;
  private static final int iDIAG = 4;
  private static final int iTREAT = 5;
  private static final int iRX = 6;


  public static void main(String[] args) throws Exception {

    /* CDE: Process numbers and port numbers to be used: */
    int pnum;
    int UnverifiedBlockPort;
    int BlockChainPort;

    /* CDE If you want to trigger bragging rights functionality... */
    if (args.length > 1) System.out.println("Special functionality is present \n");

    if (args.length < 1) pnum = 0;
    else if (args[0].equals("0")) pnum = 0;
    else if (args[0].equals("1")) pnum = 1;
    else if (args[0].equals("2")) pnum = 2;
    else pnum = 0; /* Default for badly formed argument */
    UnverifiedBlockPort = 4710 + pnum;
    BlockChainPort = 4820 + pnum;

    System.out.println("Process number: " + pnum + " Ports: " + UnverifiedBlockPort + " " + 
		       BlockChainPort + "\n");

    switch(pnum){
    case 1: FILENAME = "BlockInput1.txt"; break;
    case 2: FILENAME = "BlockInput2.txt"; break;
    default: FILENAME= "BlockInput0.txt"; break;
    }

    System.out.println("Using input file: " + FILENAME);

    try {
      try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
	String[] tokens = new String[10];
	String stringXML;
	String InputLineStr;
        String suuid;
	UUID idA;

	BlockRecord[] blockArray = new BlockRecord[20];
	
	JAXBContext jaxbContext = JAXBContext.newInstance(BlockRecord.class);
	Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
	StringWriter sw = new StringWriter();
	
	// CDE Make the output pretty printed:
	jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      	
	int n = 0;

	while ((InputLineStr = br.readLine()) != null) {
	  blockArray[n] = new BlockRecord();

          blockArray[n].setASHA256String("SHA string goes here...");
          blockArray[n].setASignedSHA256("Signed SHA string goes here...");

	  /* CDE: Generate a unique blockID. This would also be signed by creating process: */
	  idA = UUID.randomUUID();
	  suuid = new String(UUID.randomUUID().toString());
	  blockArray[n].setABlockID(suuid);
	  blockArray[n].setACreatingProcess("Process" + Integer.toString(pnum));
	  blockArray[n].setAVerificationProcessID("To be set later...");
	  /* CDE put the file data into the block record: */
	  tokens = InputLineStr.split(" +"); // Tokenize the input
	  blockArray[n].setFSSNum(tokens[iSSNUM]);
	  blockArray[n].setFFname(tokens[iFNAME]);
	  blockArray[n].setFLname(tokens[iLNAME]);
	  blockArray[n].setFDOB(tokens[iDOB]);
	  blockArray[n].setGDiag(tokens[iDIAG]);
	  blockArray[n].setGTreat(tokens[iTREAT]);
	  blockArray[n].setGRx(tokens[iRX]);
	  n++;
	}
	System.out.println(n + " records read.");
	System.out.println("Names from input:");
	for(int i=0; i < n; i++){
	  System.out.println("  " + blockArray[i].getFFname() + " " +
			     blockArray[i].getFLname());
	}
	System.out.println("\n");

	stringXML = sw.toString();
	for(int i=0; i < n; i++){
	  jaxbMarshaller.marshal(blockArray[i], sw);
	}
	String fullBlock = sw.toString();
	String XMLHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	String cleanBlock = fullBlock.replace(XMLHeader, "");
	// Show the string of concatenated, individual XML blocks:
	String XMLBlock = XMLHeader + "\n<BlockLedger>" + cleanBlock + "</BlockLedger>";
	System.out.println(XMLBlock);
      } catch (IOException e) {e.printStackTrace();}
    } catch (Exception e) {e.printStackTrace();}
  }
}