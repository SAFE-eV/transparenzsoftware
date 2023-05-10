package de.safe_ev.transparenzsoftware.verification.format.pcdf;

public class PcdfAttribute {
	private String attrName;
	
	private String attrVal;
	
	private int err;
	
	public PcdfAttribute()
	{
		attrName = "";
		attrVal = "";
		err = -1;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public String getAttrVal() {
		return attrVal;
	}

	public void setAttrVal(String attrVal) {
		this.attrVal = attrVal;
	}

	public int getErr() 
	{
		return err;
	}

	public void setErr(int err) {
		this.err = err;
	}
	
	
}
