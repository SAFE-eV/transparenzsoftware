package com.hastobe.transparenzsoftware.verification.format.pcdf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hastobe.transparenzsoftware.verification.xml.PublicKey;
import com.hastobe.transparenzsoftware.verification.xml.SignedData;
import com.hastobe.transparenzsoftware.verification.xml.Value;
import com.hastobe.transparenzsoftware.verification.xml.Values;
import com.hastobe.transparenzsoftware.verification.ValidationException;
import com.hastobe.transparenzsoftware.verification.format.pcdf.PcdfAttribute;
import com.hastobe.transparenzsoftware.verification.format.pcdf.PcdfDCMeterType;
import com.hastobe.transparenzsoftware.verification.format.pcdf.PcdfIdTagType;
//import helpers.ListPrintingHelper;
import com.hastobe.transparenzsoftware.verification.input.InvalidInputException;

public class PcdfReader {
	private PcdfChargingData cd;
	
	private static HashMap<String, String> attributes; 

	public PcdfReader()
	{
		cd = new PcdfChargingData();
		attributes = new HashMap<String, String>();
		
		attributes.put("ST", "parseSTAttrib");
		attributes.put("CT", "parseCTAttrib");
		attributes.put("CD", "parseCDAttrib");
		attributes.put("TV", "parseTVAttrib");
		attributes.put("SP", "parseSPAttrib");
		attributes.put("RV", "parseRVAttrib");
		attributes.put("SI", "parseSIAttrib");
		attributes.put("CS", "parseCSAttrib");
		attributes.put("HW", "parseHWAttrib");
		attributes.put("DT", "parseDTAttrib");
		attributes.put("PK", "parsePKAttrib");
		attributes.put("SG", "parseSGAttrib");
		attributes.put("BV", "parseBVAttrib");
		attributes.put("CSC", "parseCSCAttrib");
	}
	
	private String parseSTAttrib(String val) throws ValidationException
	{
		String res  = convertTime(val);
		if (res.indexOf("TSW") == -1)
		{
			cd.setStartTime(res);
			res = "";
		}
		return res;
	}
	
	private String parseCTAttrib(String val) throws ValidationException
	{
		String res = convertTime(val);
		if (res.indexOf("TSW") == -1)
		{
			cd.setCurrentTime(res);
			res = "";
		}
		return res;
	}
	
	private String parseCDAttrib(String val) throws ValidationException
	{
		String res = convertDuration(val);
		if (res.indexOf("TSW") == -1)
		{
			res = "";
		}
		return res;
	}
	
	private String parseTVAttrib(String val) throws ValidationException
	{
		String res = "";
		if (val.equals("0"))
			cd.setValidTime(false);
		else
		{
			if (val.equals("1"))
				cd.setValidTime(true);
			else
				throw new ValidationException("Time validity bit is invalid", "error.pcdf.validation.time.validity.invalid");
			
		}
		return res;
	}
	
	private String parseSPAttrib(String val) throws ValidationException
	{
		String res = "";
		if (val.equals("0"))
		{
			cd.setStopInfo(false);
			throw new ValidationException("Charge session does not include the last data", "error.pcdf.validation.stoptime.invalid");
		}
		else
		{
			if (val.equals("1"))
				cd.setStopInfo(true);
			else
				throw new ValidationException("Charge session does not include the last data", "error.pcdf.validation.stoptime.invalid");
		}
		return res;
	}
	
	private PcdfIdTagType getTagType(int intType)
	{
		PcdfIdTagType idTT = PcdfIdTagType.ITT_UNKNOWN;
		if (intType == 1)
			idTT = PcdfIdTagType.ITT_RFID;
		if (intType == 2)
			idTT = PcdfIdTagType.ITT_eMAID;
		if (intType == 3)
			idTT = PcdfIdTagType.ITT_CREDITCARD;
		if (intType == 4)
			idTT = PcdfIdTagType.ITT_REMOTE;
		if (intType == 5)
			idTT = PcdfIdTagType.ITT_NFC;
		return idTT;
	}
	
	private String parseRVAttrib(String val) throws ValidationException
	{
		String res = "";
		
		String[] vll = val.split("\\*");
		if (vll.length != 2)
			throw new ValidationException("Session information is invalid", "error.pcdf.validation.consumption.invalid");
		else
		{
			String cons = vll[0];
			if (cons.length() != 8)
				throw new ValidationException("Session information is invalid", "error.pcdf.validation.consumption.invalid");
			else
			{
				try
				{
					cd.setConsumption(Double.parseDouble(cons));
				}
				catch (NumberFormatException e)
				{
					throw new ValidationException("Session information is invalid", "error.pcdf.validation.consumption.invalid");
				}	
			}
			String unit = vll[1];
			if (!unit.equals("kWh"))
				throw new ValidationException("Session information is invalid", "error.pcdf.validation.consumption.invalid");
		}
		return res;
	}
	
	private String parseSIAttrib(String val) throws ValidationException
	{
		String res = "";
		String[] vll = val.split("\\*");
		if (vll.length != 3)
			throw new ValidationException("Session information is invalid", "error.pcdf.validation.session.invalid");
		else
		{
			cd.setIdTag(vll[0]);
			cd.setTxId(vll[2]);
			try
			{
				cd.setIdTagType(getTagType(Integer.parseInt(vll[1])));
			}
			catch (NumberFormatException e)
			{
				throw new ValidationException("Session information is invalid", "error.pcdf.validation.session.invalid");
			}
		}
		
		return res;
	}
	
	private String parseCSAttrib(String val)
	{
		String res = "";
		cd.setSWCRC(val);
		return res;
	}
	
	private String parseHWAttrib(String val)
	{
		String res = "";
		cd.setHWSN(val);
		return res;
	}
	
	private PcdfDCMeterType getDCMeterType(int intType)
	{
		PcdfDCMeterType dcMeterType = PcdfDCMeterType.DCMT_UNKNOWN;
		if (intType == 0)
			dcMeterType = PcdfDCMeterType.DCMT_PES_DCMETER_EU;
		return dcMeterType;
	}
	
	private String parseDTAttrib(String val) throws ValidationException
	{
		String res = "";
		try
		{
			cd.setDcMT(getDCMeterType(Integer.parseInt(val)));
		}
		catch (NumberFormatException e)
		{
			throw new ValidationException("DCMeter Type information is invalid", "error.pcdf.validation.dcmeter.type.invalid");
		}
		return res;
	}
	
	private String parsePKAttrib(String val)
	{
		String res = "";
		cd.setPbKey(val);
		return res;
	}
	
	private String parseSGAttrib(String val)
	{
		String res = "";
		cd.setSign(val);
		return res;
	}
	
	private String parseBVAttrib(String val) throws ValidationException
	{
		String res = "";
		if (val.equals("0"))
		{
			cd.setBillValid(false);
			throw new ValidationException("Billing is not possible. DCMeter error", "error.pcdf.validation.billing.invalid");
		}
		else
		{
			if (val.equals("1"))
				cd.setBillValid(true);
			else
				throw new ValidationException("Billing information is invalid", "error.pcdf.validation.billing.invalid");
		}
		return res;
	}
	
	private String parseCSCAttrib(String val) throws ValidationException
	{
		String res = "";
		try
		{
			cd.setCSC(Integer.parseInt(val));
		}
		catch (NumberFormatException e)
		{
			cd.setCSC(0);
			throw new ValidationException("Charging counter is invalid", "error.pcdf.validation.charging.counter.invalid");
		}
		return res;
	}
	
	private String convertTime(String ts) throws ValidationException
	{
		String tsd = "";
		int y = Integer.parseInt(ts.substring(0, 2));
		int m = Integer.parseInt(ts.substring(2, 4));
		int d = Integer.parseInt(ts.substring(4, 6));
		int h = Integer.parseInt(ts.substring(6, 8));
		int mn = Integer.parseInt(ts.substring(8, 10));
		int sec = Integer.parseInt(ts.substring(10));
		
		long secCount = h * 3600 + mn * 60 + sec;
		/*if (duration == -1)
		{
			duration = secCount;
			System.out.println("Duration is set " + Long.toString(duration));
		}
		else
		{
			duration = secCount - duration;
			System.out.println("Duration is calc " + Long.toString(duration));
		}*/
		if (y < 19)
			throw new ValidationException("Corrupt time information", "error.pcdf.validation.time.invalid");
		else
			if ((m == 0) || (m > 12))
				throw new ValidationException("Corrupt time information", "error.pcdf.validation.time.invalid");
			else
				if ((d == 0) || (d > 31))
					throw new ValidationException("Corrupt time information", "error.pcdf.validation.time.invalid");
				else
					if ((h > 23))
						throw new ValidationException("Corrupt time information", "error.pcdf.validation.time.invalid");
					else
						if ((mn > 59))
							throw new ValidationException("Corrupt time information", "error.pcdf.validation.time.invalid");
						else
							if ((sec > 59))
								throw new ValidationException("Corrupt time information", "error.pcdf.validation.time.invalid");
							else
							{
								tsd = "20" + ts.substring(0,2) + "-" + ts.substring(2,4) + "-" + ts.substring(4,6) + "T" + ts.substring(6,8) + ":" + ts.substring(8,10) + ":" + ts.substring(10);
							}	
		return tsd;
	}
	
	private String convertDuration(String v) throws ValidationException
	{
		String res = "";
		if (v.length() != 6)
		{
			throw new ValidationException("Charging duration is invalid", "error.pcdf.validation.charging.duration.invalid");
		}
		String hStr = v.substring(0, 2);
		String mStr = v.substring(2, 4);
		String sStr = v.substring(4, 6);
		int hInt = Integer.parseInt(hStr);
		int mInt = Integer.parseInt(mStr);
		int sInt = Integer.parseInt(sStr);
		if ((hInt < 0) || (hInt > 99))
		{
			throw new ValidationException("Charging duration is invalid", "error.pcdf.validation.charging.duration.invalid");
		}
		if ((mInt < 0) || (mInt > 59))
		{
			throw new ValidationException("Charging duration is invalid", "error.pcdf.validation.charging.duration.invalid");
		}
		if ((sInt < 0) || (sInt > 59))
		{
			throw new ValidationException("Charging duration is invalid", "error.pcdf.validation.charging.duration.invalid");
		}
		cd.setDuration(hInt * 3600 + mInt * 60 + sInt);
		return res;
	}
	
	public Values readPcdfFile(String filename) throws ValidationException, InvalidInputException
	{
		Values vals = new Values();
		List<Value> listVals = new ArrayList<>();
		String content = readFileContents(filename);
		if (!content.equals(""))
		{
			String result = parsePcdfFile(content);
			if (result.equals(""))
			{
				Value val = new Value();
				
				PublicKey pbKey = new PublicKey();
				pbKey.setValue(cd.getPbKey());
				pbKey.setEncoding("secp256r1");
				val.setPublicKey(pbKey);
				
				val.setContext(cd.getChData());
				
				val.setTransactionId(cd.getTxIdInt());
				
				SignedData sd = new SignedData();
				sd.setFormat("PCDF");
				sd.setEncoding("SHA256withECDSA");
				sd.setValue(cd.getChData());
				val.setSignedData(sd);
				
				listVals.add(val);
			}
		}
		
		vals.setValues(listVals);
		return vals;
	}
	
	public Values readPCDFString(String content) throws ValidationException
	{
		Values vals = new Values();
		List<Value> listVals = new ArrayList<>();
		String result = parsePcdfFile(content);
		if (result.equals(""))
		{
			Value val = new Value();
			
			PublicKey pbKey = new PublicKey();
			pbKey.setValue(cd.getPbKey());
			pbKey.setEncoding("secp256r1");
			val.setPublicKey(pbKey);
			
			val.setContext(cd.getChData());
			
			val.setTransactionId(cd.getTxIdInt());
			
			SignedData sd = new SignedData();
			sd.setFormat("PCDF");
			sd.setEncoding("SHA256withECDSA");
			sd.setValue(cd.getChData());
			val.setSignedData(sd);
			
			listVals.add(val);
		}
	
		vals.setValues(listVals);
		return vals;
	}
	
	private String readFileContents(String filename) throws InvalidInputException
	{
		FileReader fr;
		try 
		{
			fr = new FileReader(filename);
			BufferedReader reader = new BufferedReader(fr);
		    String comp = "";
		    String textLine;
		    try 
		    {
				while((textLine=reader.readLine()) != null) 
				{
				    comp = comp + textLine;
				}
				fr.close();
				return comp;
			} 
		    catch (IOException e) 
		    {
		    	e.printStackTrace();
		    	throw new InvalidInputException("File cannot be read", "error.pcdf.file.not.readable", e);
			}
		    
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			throw new InvalidInputException("Input is not a file or the file cannot be found", "error.path.not.a.file", e);
		}
	}
	
	private String removeSTXETX(String input)
	{
		String result = "";
		if (input.indexOf("\\u0002") == 0)
		{
			input = input.substring(6);
		}
		else
		{
			if (input.charAt(0) == 2)
			{
				input = input.substring(1);
			}
		}
		int ps = input.lastIndexOf(")");
		if (ps != -1)
		{
			//input = input.replaceAll("\\\\u0003", "");
			input = input.substring(0, ps + 1);
		}
		return input;
	}
	
	private String removeSTXETX2(String input)
	{
		int pss = input.indexOf("128.8.0");
		if (pss != -1)
		{
			input = input.substring(pss);
			int ps = input.lastIndexOf(")");
			if (ps != -1)
			{
				//input = input.replaceAll("\\\\u0003", "");
				input = input.substring(0, ps + 1);
			}
		}
		else
		{
			input = "TSW0007";
		}
		return input;
	}
	
	private String checkAndRemoveSTXETX(String input) throws ValidationException
	{
		String result = "";
		if (input.indexOf("\\u0002") == 0)
		{
			input = input.replaceAll("\\\\u0002", "");
			int ps = input.indexOf("\\u0003");
			if (ps != -1)
			{
				//input = input.replaceAll("\\\\u0003", "");
				result = input.substring(0, ps);
			}
			else
				throw new ValidationException("No ETX Marker at the end of the data", "error.pcdf.validation.etx.missing");
		}
		else
		{
			if (input.getBytes()[0] == 2)
			{
				if (input.getBytes()[input.length() - 1] == 3)
				{
					result = input.substring(1, input.length() - 1);
				}
				else
					throw new ValidationException("No ETX Marker at the end of the data", "error.pcdf.validation.etx.missing");
			}
			else
			{
				throw new ValidationException("No STX Marker at the end of the data", "error.pcdf.validation.stx.missing");
			}
		}
		return result;
	}
	
	private PcdfAttribute detectAttribute(String s)
	{
		PcdfAttribute a = new PcdfAttribute();
		s = s.replace("(", "");
		String[] attrs = s.split(":");
		if (attrs.length != 2)
		{
			a.setErr(1);//not correctly formatted
		}
		else
		{
			if (attributes.containsKey(attrs[0]))
			{
				a.setAttrName(attrs[0]);
				a.setAttrVal(attrs[1]);
				a.setErr(0);
			}
		}
		return a;
	}
	
	private String parsePcdfFile(String input) throws ValidationException
	{
		String res = "";
		input = input.substring(0, input.length() - 1);
		//input = checkAndRemoveSTXETX(input);
		input = removeSTXETX2(input);
		if (input.indexOf("TSW0") != 0)
		{
			if (input.indexOf("128.8.0") == 0)
			{
				input = input.replaceAll("128.8.0", "");
					
				String[] lst = input.split("\\)");
				for(String s : lst)
				{
					PcdfAttribute a = detectAttribute(s);
					//System.out.println(a.getAttrName());
					if (a.getErr() == 0)
					{
						String funcName = attributes.get(a.getAttrName());
						Method meth;
						try {
							meth = this.getClass().getDeclaredMethod(funcName, String.class);
							
							try {
								String err = (String) meth.invoke(this, a.getAttrVal());
								if (!err.isEmpty())
									return err;
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						}
					}
					else
					{
						if (a.getErr() > 0)
						{
							throw new ValidationException("Charging data is not valid", "error.pcdf.validation.format");
						}
					}
				}
				
				int pos = input.indexOf("(SG:");
				cd.setChData("128.8.0" + input);
			}
			else
				throw new ValidationException("Charging data is not valid", "error.pcdf.validation.obis.invalid");
		}
		else
			res = input;

		return res;
	}
}
