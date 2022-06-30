import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;

public class AudioUtils {
  public static <T> T getDataLine(Class<T> clazz) throws Exception {
    AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
    float rate = 44100.0f;
    int channels = 2;
    int sampleSize = 16;
    boolean bigEndian = false;

    AudioFormat format = new AudioFormat(
        encoding,
        rate,
        sampleSize,
        channels,
        (sampleSize / 8) * channels,
        rate,
        bigEndian);

    DataLine.Info info = new DataLine.Info(clazz, format);
    if (!AudioSystem.isLineSupported(info)) {
      throw new Exception("Line not supported: " + info);
    }

    return (T) AudioSystem.getLine(info);
  }
}
