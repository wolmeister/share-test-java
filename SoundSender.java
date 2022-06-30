import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SoundSender extends Thread {

  private final TargetDataLine line;
  private final DatagramSocket socket;
  private final InetAddress localhost;

  public SoundSender() {
    try {
      AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
      line = AudioSystem.getTargetDataLine(format);
      line.open();

      this.socket = new DatagramSocket();
      this.localhost = InetAddress.getLocalHost();

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
    while (true) {
      byte[] data = new byte[4096];
      line.read(data, 0, data.length);
      DatagramPacket packet = new DatagramPacket(data, data.length, localhost, 9001);
      socket.send(packet);
    }
  }
}
