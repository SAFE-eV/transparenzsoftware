package com.hastobe.transparenzsoftware.verification;

import java.util.logging.Logger;

/**
 * Utility class to log the verification parameters and the result
 */
public class VerificationLogger {

	private static Logger LOG = Logger.getLogger("VERIFICATIONLOG");
	
	public static void log()
	{
		
	}

	public static void log(String format, String algo, byte[] publicKey, byte[] hashData, byte[] derSignature, boolean verify) {
		String lastMethod = "";
		String lastClass = "";
		for (StackTraceElement t : Thread.currentThread().getStackTrace()) {
			if (t.getMethodName().equals("invoke0")) {
				break;
			}
			lastMethod = t.getMethodName();
			lastClass = t.getClassName();
		}
		String msg = ""+lastClass+"."+lastMethod+"(): "+format+"/"+algo+" pubkey:"+publicKey.length*8+" hash:"+hashData.length*8+" sign:"+derSignature.length*8+" => "+verify;
		
		LOG.info(msg);
		System.out.println("OUT: "+msg);
		// System.err.println("ERR: "+msg);
	}
	
}
