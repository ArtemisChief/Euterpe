package util;

public class MidiUtil {

    public static byte[] mergeByte(byte[] b1, byte[] b2) {
        byte[] result = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, result, 0, b1.length);
        System.arraycopy(b2, 0, result, b1.length, b2.length);
        return result;
    }

    public static byte[] mergeByte(byte[] b1, byte b2) {
        byte[] result = new byte[b1.length + 1];
        System.arraycopy(b1, 0, result, 0, b1.length);
        result[b1.length] = b2;
        return result;
    }

    public static int bpmToMpt(float bpm) {
        return (int) (60 / bpm * 1000000);
    }

    public static byte[] intToBytes(int val, int byteCount) {
        byte[] buffer = new byte[byteCount];

        int[] ints = new int[byteCount];

        for (int i = 0; i < byteCount; i++) {
            ints[i] = val & 0xFF;
            buffer[byteCount - i - 1] = (byte) ints[i];

            val = val >> 8;

            if (val == 0)
                break;
        }
        return buffer;
    }

    private static final String HEX = "0123456789ABCDEF";

    public static String byteToHex(byte b) {
        int high = (b & 0xF0) >> 4;
        int low = (b & 0x0F);

        return "" + HEX.charAt(high) + HEX.charAt(low);
    }

    public static String bytesToHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            sb.append(byteToHex(b[i])).append(" ");
        }
        return sb.toString();
    }

    public static byte[] buildBytes(int mValue) {
        byte[] mBytes;
        int mSizeInBytes;

        if (mValue == 0) {
            mBytes = new byte[1];
            mBytes[0] = 0x00;
            mSizeInBytes = 1;
            return mBytes;
        }

        mSizeInBytes = 0;
        int[] vals = new int[4];
        int tmpVal = mValue;

        while (mSizeInBytes < 4 && tmpVal > 0) {
            vals[mSizeInBytes] = tmpVal & 0x7F;

            mSizeInBytes++;
            tmpVal = tmpVal >> 7;
        }

        for (int i = 1; i < mSizeInBytes; i++) {
            vals[i] |= 0x80;
        }

        mBytes = new byte[mSizeInBytes];
        for (int i = 0; i < mSizeInBytes; i++) {
            mBytes[i] = (byte) vals[mSizeInBytes - i - 1];
        }
        return mBytes;
    }

}