package de.safe_ev.transparenzsoftware.hexutil;

public class ByteIntegerConversion {
	/**
	 * read a 32 bit big-endian long integer value from a byte array. using basic
	 * conversion facilities instead of the less reliable
	 * ByteBuffer-double-conversion.
	 *
	 * @param input  byte array to read from
	 * @param offset offset in the byte array to start from
	 * @return the integer value found there.
	 */
	public static long read32BitBigendianFromByteArray(byte[] input, int offset) {
		/*
		 * this depends on the buf signedness. ByteBuffer buf = ByteBuffer.wrap(input,
		 * offset, 4)buf.s int intvalue = buf.getInt();
		 */
		// this is safely big-endian.
		long tmp = 0;
		tmp |= (input[offset++]) & 0xFF;
		tmp <<= 8;
		tmp |= (input[offset++]) & 0xFF;
		tmp <<= 8;
		tmp |= (input[offset++]) & 0xFF;
		tmp <<= 8;
		tmp |= (input[offset]) & 0xFF;
		return tmp;
	}

	public static long readBigEndianIntegerFromByteArray(final byte[] input, int offset, int sizeInBytes) {
		assert (sizeInBytes <= 8); // and 8 is pushing it! watch out for signedness issues with Java
		long tmp = 0;
		while (sizeInBytes > 0) {
			tmp <<= 8;
			tmp |= (input[offset++]) & 0xFF;
			sizeInBytes--;
		}
		return tmp;
	}

	public static void writeBigEndianIntegerToByteArray(long value, byte[] output, int offset, int sizeInBytes) {
		while (sizeInBytes > 0) {
/// make a top-mask, shift it... don't recalculate all the time.

		}
		throw new UnsupportedOperationException("not implemented yet.");
	}

	// TO DELETE
	private void writeIntegerToBuffer(Integer value, int representationBytes, byte[] targetBuffer, int offsetInBuffer) {
		// optimised for 1-3 byte java ints. for 4-7 byte, use java long; for larger,
		// use BigInteger.
		switch (representationBytes) {
		default:
			throw new IllegalArgumentException("integer representation size not supported");
		// duffs device, intentional fallthrough.
		case 3:
			targetBuffer[offsetInBuffer++] = (byte) ((value >> 16) & 0xFF);
		case 2:
			targetBuffer[offsetInBuffer++] = (byte) ((value >> 8) & 0xFF);
		case 1:
			targetBuffer[offsetInBuffer] = (byte) ((value) & 0xFF);
		}
		return;
	}

}
