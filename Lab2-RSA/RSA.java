package lab2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {
	//Function that generates a random large prime number
	private BigInteger largePrimeNumber() {
		SecureRandom random = new SecureRandom();
		return BigInteger.probablePrime(1024, random);
	};
	
	//Function that reads input from user and converts the string to a BigInteger
	private BigInteger readInput() {
		System.out.println("Enter message: ");
		String message = "";
		try {
			message = (new BufferedReader(new InputStreamReader(System.in))).readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return new BigInteger(message.getBytes());
	}
	
	public static void main(String[] args) {
		RSA operations = new RSA();
		
		//Generate private keys p and q
		BigInteger p = operations.largePrimeNumber(),
		q = operations.largePrimeNumber(),
		
		//Generate public key n = p*q
		n = p.multiply(q),
		
		//Generate public key e, the next prime number after the smallest of p and q.
		e = p.min(q).nextProbablePrime(),
		
		//(p-1)(q-1)
		phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)),
		
		//Private key d, de = 1 mod((p-1)(q-1))
		d = e.modInverse(phi);
		
		//Print private keys
		System.out.println("Private keys:");
		System.out.println("p: " + p);
		System.out.println("q: " + q);
		System.out.println("d: " + d);
		
		//Print public keys
		System.out.println("Public keys:");
		System.out.println("n: " + n);
		System.out.println("e: " + e);
		
		//Read message from user
	    BigInteger m = operations.readInput();
		
	    //Encrypt message, c = m^e mod n
	    BigInteger c = m.modPow(e, n);
	    System.out.println("Encrypted message: " + c);
	    
	    //Decrypt message, m = c^d mod n
	    BigInteger decryptedValue = c.modPow(d, n);
	    System.out.println("Decrypted value: " + decryptedValue);
	    
	    //Change value to string
	    String decryptedMessage = new String(decryptedValue.toByteArray());
	    
	    //Display decrypted message
	    System.out.println("Decrypted message: " + decryptedMessage);
	}

}
