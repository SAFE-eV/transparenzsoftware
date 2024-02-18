package de.safe_ev.transparenzsoftware.verification.format.pcdf;

import java.math.BigInteger;

import de.safe_ev.transparenzsoftware.verification.format.pcdf.PcdfDCMeterType;
import de.safe_ev.transparenzsoftware.verification.format.pcdf.PcdfIdTagType;

public class PcdfChargingData {
	private double consumption;
	
	private String sign;
	
	private String pbKey;
	
	private String startTime;
	
	private String currentTime;
	
	private boolean validTime;
	
	private boolean stopInfo;
	
	private boolean billValid;
	
	private int CSC;
	
	private String idTag;
	
	private String rd;
	
	private long duration;
	
	private String txId;
	
	private BigInteger txIdInt;
	
	private String SWCRC;
	
	private String HWSN;
	
	private PcdfIdTagType idTagType;
	
	private PcdfDCMeterType dcMT;
	
	private String chData;
	
	public PcdfChargingData()
	{
		startTime = "";
		currentTime = "";
		validTime = false;
		stopInfo = true;
		idTagType = PcdfIdTagType.ITT_UNKNOWN;
		txId = "";
		txIdInt = null;
		idTag = "";
		sign = "";
		HWSN = "";
		SWCRC = "";
		pbKey = "";
		duration = -1;
		consumption = 0.0;
	}

	public double getConsumption() {
		return consumption;
	}

	public void setConsumption(double consumption) {
		this.consumption = consumption;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getPbKey() {
		return pbKey;
	}

	public void setPbKey(String pbKey) {
		this.pbKey = pbKey;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}

	public boolean isValidTime() {
		return validTime;
	}

	public void setValidTime(boolean validTime) {
		this.validTime = validTime;
	}

	public boolean isStopInfo() {
		return stopInfo;
	}

	public void setStopInfo(boolean stopInfo) {
		this.stopInfo = stopInfo;
	}

	public boolean isBillValid() {
		return billValid;
	}

	public void setBillValid(boolean billValid) {
		this.billValid = billValid;
	}

	public int getCSC() {
		return CSC;
	}

	public void setCSC(int cSC) {
		CSC = cSC;
	}

	public String getIdTag() {
		return idTag;
	}

	public void setIdTag(String idTag) {
		this.idTag = idTag;
	}

	public String getRd() {
		return rd;
	}

	public void setRd(String rd) {
		this.rd = rd;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
		try
		{
			this.txIdInt = new BigInteger(txId);
		}
		catch (NumberFormatException e)
		{
			this.txIdInt = new BigInteger("0");
		}
	}

	public BigInteger getTxIdInt() {
		return txIdInt;
	}

	public String getSWCRC() {
		return SWCRC;
	}

	public void setSWCRC(String sWCRC) {
		SWCRC = sWCRC;
	}

	public String getHWSN() {
		return HWSN;
	}

	public void setHWSN(String hWSN) {
		HWSN = hWSN;
	}

	public PcdfIdTagType getIdTagType() {
		return idTagType;
	}

	public void setIdTagType(PcdfIdTagType idTagType) {
		this.idTagType = idTagType;
	}

	public PcdfDCMeterType getDcMT() {
		return dcMT;
	}

	public void setDcMT(PcdfDCMeterType dcMT) {
		this.dcMT = dcMT;
	}

	public String getChData() {
		return chData;
	}

	public void setChData(String chData) {
		this.chData = chData;
	}
	
	
}
