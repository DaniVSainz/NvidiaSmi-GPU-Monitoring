import java.awt.AWTException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;


import com.twilio.Twilio; 
import com.twilio.rest.api.v2010.account.Message; 
import com.twilio.rest.api.v2010.account.MessageCreator; 
import com.twilio.type.PhoneNumber; 
 
import java.math.BigDecimal; 
import java.net.URI; 
import java.util.ArrayList; 
import java.util.List; 

//The point of this programs is to navigate to nvida-smi(nvidia's api tool)
//Check the status of our gpu, if the usage of the gpu is X<90% its going to mean the miner is paused.
//Then use this info to send out post's to a messaging service and blow up my phone with text's so i wake up and fix the issue if its not network related.
public class TerminalBotv3 {
    private final static String ACCOUNT_SID = ""; 
    private final static String AUTH_TOKEN = ""; 
	public int counter = 0;
	
//	Creates a terminal for us to use with our other methods
	public Process startTerminal() {
        ProcessBuilder builder = new ProcessBuilder( "cmd" );
        Process p=null;
        try {
            p = builder.start();
        }
        catch (IOException e) {
            System.out.println(e);
        }
        counter ++;
        System.out.println("Terminal :" + counter +  " Created" );
        return p;
	}
	
	public void runProcess(Process terminalInstance, String command) throws InterruptedException {
		BufferedWriter p_stdin = 
		          new BufferedWriter(new OutputStreamWriter(terminalInstance.getOutputStream()));
		try {
			p_stdin.write(command);
			p_stdin.newLine();
			p_stdin.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1);
		}
	}
	
	public static void sendMessage() { 
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN); 
         
		Message message = Message.creator(
			    new PhoneNumber("+17867809774"),  // To number
			    new PhoneNumber("+16028332791"),  // From number
			    "GPU's DOWN!!!!!!!!!!"                    // SMS body
			).create();
    } 
	
	
	//good output starts at 24
	public void getOutput(Process newP) throws InterruptedException {
	    Scanner s = new Scanner( newP.getInputStream() );
//	    while (s.hasNext())
//	    {
//	        System.out.println( s.next() );
//	    }
//	       s.close();
//	       System.out.println("**** STREAM CLOSED ****");
	    for(int i=1; i<26; i++){
	    	if(i==23 || i==24 ||i==25 ) {
	    		int util = s.nextInt();
	    		System.out.println(util);
	    		if(util <=80) {
	    			sendMessage();
	    			Thread.sleep(3000);
	    		}
	    	}
	    	s.next();
	    }
    }
	
//	nvidia-smi --query-gpu=utilization.gpu --format=csv

	public static void main(String[] args) throws InterruptedException, AWTException {
		TerminalBotv3 terminal = new TerminalBotv3();

		while(true) {
			Process newP = terminal.startTerminal();
			terminal.runProcess(newP, "cd C:\\Program Files\\NVIDIA Corporation\\NVSMI");
			terminal.runProcess(newP, "nvidia-smi --query-gpu=utilization.gpu --format=csv");
			terminal.getOutput(newP);
			Thread.sleep(4500);
		}
		
	}
}
