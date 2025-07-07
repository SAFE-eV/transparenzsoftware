package de.safe_ev.transparenzsoftware.verification.format.ocmf;

public class OBISCode {

    // Register OBIS-Kennzahl 1.8.0.*100 (Anzeige „E“), Energiezählwerk (Liefermenge
    // des aktuellen Ladevorgangs)

    private int A, B, C, D, E, F;

    public int getA() {
	return A;
    }

    public int getB() {
	return B;
    }

    public int getC() {
	return C;
    }

    public int getD() {
	return D;
    }

    public int getE() {
	return E;
    }

    public int getF() {
	return F;
    }

    private boolean isHex(char ch) {
	return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f');
    }

    private int handleHex(char ch, int before) {
	return (before << 4) | (((ch >= 'a' ? ch - 'a' + 10 : ch - '0')) & 0x0F);
    }

    public OBISCode(String code) {
	code = code.toLowerCase().trim();
	int state = 0;
	for (int i = 0; i < code.length(); i++) {
	    final char ch = code.charAt(i);
	    switch (state) {
	    case 0:
		if (ch == '-') {
		    state = 1;
		} else if (isHex(ch)) {
		    A = handleHex(ch, A);
		} else {
		    throw new IllegalArgumentException("OBIS code " + code + " can not be parsed.");
		}
		break;
	    case 1:
		if (ch == ':') {
		    state = 2;
		} else if (isHex(ch)) {
		    B = handleHex(ch, B);
		} else {
		    throw new IllegalArgumentException("OBIS code " + code + " can not be parsed.");
		}
		break;
	    case 2:
		if (ch == '.') {
		    state = 3;
		} else if (isHex(ch)) {
		    C = handleHex(ch, C);
		} else {
		    throw new IllegalArgumentException("OBIS code " + code + " can not be parsed.");
		}
		break;
	    case 3:
		if (ch == '.') {
		    state = 4;
		} else if (isHex(ch)) {
		    D = handleHex(ch, D);
		} else {
		    throw new IllegalArgumentException("OBIS code " + code + " can not be parsed.");
		}
		break;
	    case 4:
		if (ch == '.' || ch == '*') {
		    state = 5;
		} else if (isHex(ch)) {
		    E = handleHex(ch, E);
		} else {
		    throw new IllegalArgumentException("OBIS code " + code + " can not be parsed.");
		}
		break;
	    case 5:
		if (isHex(ch)) {
		    F = handleHex(ch, F);
		} else {
		    throw new IllegalArgumentException("OBIS code " + code + " can not be parsed.");
		}
		break;
	    }
	}
    }

    @Override
    public String toString() {
	return "" + A + "-" + B + ":" + C + "." + D + "." + E + "." + F;
    }

}
