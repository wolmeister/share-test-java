public class ByteUtils {
  public static byte[] intToBytes(int value) {
    return new byte[] {
        (byte) (value >>> 24),
        (byte) (value >>> 16),
        (byte) (value >>> 8),
        (byte) value };
  }

  public static int bytesToInt(byte[] value) {
    return ((value[0] & 0xFF) << 24) |
        ((value[1] & 0xFF) << 16) |
        ((value[2] & 0xFF) << 8) |
        ((value[3] & 0xFF) << 0);
  }
}
