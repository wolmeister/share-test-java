import java.net.DatagramPacket;
import java.net.DatagramSocket;
import javax.sound.sampled.SourceDataLine;

public class SoundReceiver extends Thread {

  private final SourceDataLine line;
  private final DatagramSocket socket;

  public SoundReceiver() {
    try {
      line = AudioUtils.getDataLine(SourceDataLine.class);
      line.start();

      this.socket = new DatagramSocket(9001);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    try {
      doRun();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void doRun() throws Exception {
    byte[] buffer = new byte[4096];

    while (true) {
      DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
      socket.receive(dp);

      byte[] data = dp.getData();
      line.write(data, 0, data.length);
    }
  }
}
