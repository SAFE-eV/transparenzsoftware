package de.safe_ev.transparenzsoftware.hexutil;

public class HexDump
{
    static final String   HEXES  = "0123456789ABCDEF";
    /** the simple, low-effort bit
     * hex print byte input into String.
     *
     * @param raw raw input bytes to print
     * @param separator      the separator between the byte chars. May be an empty string.
     * @param entriesPerLine how many bytes may fit into a single line until we add a line break
     * @return the formatted string
     */
    public static String bytesToHexString(byte[] raw, String separator, int entriesPerLine)
        {
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        int                 pos = 0;

        for (final byte b : raw) // this seems to over-extend in buffers.
            {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
            hex.append(separator);
            pos++;
            if ((entriesPerLine > 0) && (pos >= entriesPerLine))
                {
                pos = 0;
                hex.append(System.lineSeparator());
                }
            }
        return hex.toString();
        }
}
