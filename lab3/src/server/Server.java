/*
* Authors: 
* Victor Imark, vicim994
* Gabriel Cederqvist, gabce093
*/
package server;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.security.*;
import java.util.StringTokenizer;


public class Server {
	private int port;
	// This is not a reserved port number
	static final int DEFAULT_PORT = 8189;
	static final String KEYSTORE = "SERVkeystore.ks";
	static final String TRUSTSTORE = "SERVtruststore.ks";
	static final String KEYSTOREPASS = "123456";
	static final String TRUSTSTOREPASS = "123456";
	
	/** Constructor
	 * @param port The port where the server
	 *    will listen for requests
	 */
	Server( int port ) {
		this.port = port;
	}
	
	/** The method that does the work for the class */
	public void run() {
		try {
			KeyStore ks = KeyStore.getInstance( "JCEKS" );
			ks.load( new FileInputStream( KEYSTORE ), KEYSTOREPASS.toCharArray() );
			
			KeyStore ts = KeyStore.getInstance( "JCEKS" );
			ts.load( new FileInputStream( TRUSTSTORE ), TRUSTSTOREPASS.toCharArray() );
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init( ks, KEYSTOREPASS.toCharArray() );
			
			TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
			tmf.init( ts );
			
			SSLContext sslContext = SSLContext.getInstance( "TLS" );
			sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );
			
			SSLServerSocketFactory sslServerFactory = sslContext.getServerSocketFactory();
			SSLServerSocket sss = (SSLServerSocket) sslServerFactory.createServerSocket( port );
			sss.setNeedClientAuth(true);
			sss.setEnabledCipherSuites( sss.getSupportedCipherSuites() );	
			System.out.println("\n>>>> SecureAdditionServer: active ");
			SSLSocket incoming = (SSLSocket)sss.accept();

			BufferedReader in = new BufferedReader( new InputStreamReader( incoming.getInputStream() ) );
			PrintWriter out = new PrintWriter( incoming.getOutputStream(), true );		
			
			String option;
			String fileName;
			Boolean running = true;
			
			while(running) {
				option = in.readLine();
				switch(Integer.parseInt(option)) {
					case 1:
						fileName = in.readLine();
						String data = readFile(fileName);
						out.println(data);			
						break;
					case 2:
						fileName = in.readLine();
						Boolean uploadedFile = uploadFile(fileName, in);
						
						if (uploadedFile) out.println("Upload complete!");
						else out.println("Upload failed :(");
						break;
					case 3:
						fileName = in.readLine();
						Boolean deletedFile = deleteFile(fileName);
						
						if (deletedFile) out.println("File deleted!");
						else out.println("Delete failed :(");
						break;
					case 4:
						running = false;
						break;
				}
			}
			
			incoming.close();
		}
		catch( Exception x ) {
			System.out.println( x );
			x.printStackTrace();
		}
	}
	
	//Function for reading a file which returns a string with the contents of the file
	  private String readFile(String fileName)
	    {
	        try
	        {
	        	
	            BufferedReader fileReader = new BufferedReader(new FileReader("src/server/" + fileName));
	            StringBuilder builder = new StringBuilder();
	            //read file line by line
	            String line = fileReader.readLine();
	            while(line!=null)
	            {
	                builder.append(line);
	                builder.append(System.lineSeparator());
	                line = fileReader.readLine();
	            }
	            fileReader.close();
	            return builder.toString();
	        }
	        catch (Exception e)
	        {
	            System.out.println("Failed when reading file: " + fileName + " Exception: " + e.toString());
	            e.printStackTrace();
	            return "";
	        }
	    }
	  
	  //Function for receiving a file from the client
	  //and creating it on the server.
	  private Boolean uploadFile(String fileName, BufferedReader in)
	    {
	        try
	        {
	        	StringBuilder builder = new StringBuilder();
	            String line = in.readLine();
	            //read file
	            while (!line.equals("")) {
	                builder.append(line);
	                builder.append(System.lineSeparator());
	                line = in.readLine();
	            }
	            String data = builder.toString();
	            PrintWriter writer;
	            //create file
	            writer = new PrintWriter("src/server/" + fileName);
	            writer.print(data);
	            writer.close();
	            return true;
	        }
	        catch (Exception e)
	        {
	            System.out.println("Failed when reading file: " + fileName + " Exception: " + e.toString());
	            e.printStackTrace();
	            return false;
	        }
	    }
	  
	  private Boolean deleteFile(String fileName)
	    {
	        try
	        {
	        	File fileToDelete = new File("src/server/" + fileName);
	        	fileToDelete.delete();
	            return true;
	        }
	        catch (Exception e)
	        {
	            System.out.println("Failed when reading file: " + fileName + " Exception: " + e.toString());
	            e.printStackTrace();
	            return false;
	        }
	    }
	
	
	/** The test method for the class
	 * @param args[0] Optional port number in place of
	 *        the default
	 */
	public static void main( String[] args ) {
		int port = DEFAULT_PORT;
		if (args.length > 0 ) {
			port = Integer.parseInt( args[0] );
		}
		Server addServe = new Server( port );
		addServe.run();
	}
}
