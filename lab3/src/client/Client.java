/*
* Authors: 
* Victor Imark, vicim994
* Gabriel Cederqvist, gabce093
*/
package client;

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import javax.net.ssl.*;

public class Client {
	private InetAddress host;
	private int port;
	// This is not a reserved port number 
	static final int DEFAULT_PORT = 8189;
	static final String KEYSTORE = "GCVIkeystore.ks";
	static final String TRUSTSTORE = "GCVItruststore.ks";
	static final String KEYSTOREPASS = "123456789";
	static final String TRUSTSTOREPASS = "123456789";
  
	
	// Constructor @param host Internet address of the host where the server is located
	// @param port Port number on the host where the server is listening
	public Client( InetAddress host, int port ) {
		this.host = host;
		this.port = port;
	}
	
  // The method used to start a client object
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
			SSLSocketFactory sslFact = sslContext.getSocketFactory();      	
			SSLSocket client =  (SSLSocket)sslFact.createSocket(host, port);
			client.setEnabledCipherSuites( client.getSupportedCipherSuites() );
			System.out.println("\n>>>> SSL/TLS handshake completed");

			
			BufferedReader socketIn;
			socketIn = new BufferedReader( new InputStreamReader( client.getInputStream() ) );
			PrintWriter socketOut = new PrintWriter( client.getOutputStream(), true );
			
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			String option;
			String fileName;
			Boolean running = true;
			
			//while loop that makes the menu reappear after an option has been performed
			while (running) {
				//Option menu for choosing 
				System.out.println("Menu");
				System.out.println("Press 1 to download");
				System.out.println("Press 2 to upload");
				System.out.println("Press 3 to delete");
				System.out.println("Press 4 to terminate");
				
				//Read and send option to server
				option = input.readLine();
				socketOut.println(option);
				//Switch case for the different options
				switch(Integer.parseInt(option)) {
				case 1: 
					System.out.println("Enter the file name: ");
					fileName = input.readLine();
					socketOut.println(fileName);
					downloadFile(fileName, socketIn);
					break;
				case 2:
					System.out.println("Enter the file name of the file you want to upload: ");
					fileName = input.readLine();
					uploadFile(fileName, socketOut);
					System.out.println(socketIn.readLine());
					break;
				case 3:
					System.out.println("Enter the file name of the file you want to delete: ");
					fileName = input.readLine();
					socketOut.println(fileName);
					System.out.println(socketIn.readLine());
					break;
				case 4:
					System.out.println("Terminating...");
					running = false;
					break;
				default:
					System.out.println("Default");
					break;
			}
			}
			
			socketOut.println ( "" );
		}
		catch( Exception x ) {
			System.out.println( x );
			x.printStackTrace();
		}
	}
	
	//Function used to download a specific file from the server
	private void downloadFile(String fileName,BufferedReader socketIn) {
		 try {
	            StringBuilder builder = new StringBuilder();
	            String line = socketIn.readLine();
	            //Read lines in file
	            while (!line.equals("")) {
	            	builder.append(line);
	            	builder.append(System.lineSeparator());
	                line = socketIn.readLine();
	            }
	            String data = builder.toString();
	            PrintWriter writer;
	            //Creates a new file with the read lines
	            writer = new PrintWriter("src/client/" + fileName);
	            writer.print(data);
	            writer.close();
	            System.out.println("Download completed!");
	            
	        }
	        catch (Exception e)
	        {
	            System.out.println ("Error when downloading: " + e.toString());
	            e.printStackTrace();
	           
	        }
	}
	
	//Function for uploading a specific file to the server
	private void uploadFile(String fileName, PrintWriter socketOut) {
		 try {
			 //First read the file
			 BufferedReader fileReader = new BufferedReader(new FileReader("src/client/" + fileName));
			 StringBuilder builder = new StringBuilder();
			 String line = fileReader.readLine();
			 while (line != null) {
	                builder.append(line);
	                builder.append(System.lineSeparator());
	                line = fileReader.readLine();
	            }
			 fileReader.close();
			 String fileData = builder.toString();
			 //Send file name and data to the server
			 socketOut.println(fileName);
			 socketOut.println(fileData);
	        }
	        catch (Exception e)
	        {
	            System.out.println ("Error when uploading: " + e.toString());
	            e.printStackTrace();
	           
	        }
	}
	
	
	
	// The test method for the class @param args Optional port number and host name
	public static void main( String[] args ) {
		try {
			InetAddress host = InetAddress.getLocalHost();
			int port = DEFAULT_PORT;
			if ( args.length > 0 ) {
				port = Integer.parseInt( args[0] );
			}
			if ( args.length > 1 ) {
				host = InetAddress.getByName( args[1] );
			}
			Client addClient = new Client( host, port );
			addClient.run();
		}
		catch ( UnknownHostException uhx ) {
			System.out.println( uhx );
			uhx.printStackTrace();
		}
	}
}
