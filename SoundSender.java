import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

public class SoundSender extends Thread {

  private final TargetDataLine line;
  private final DatagramSocket socket;
  private final InetAddress host;

  public SoundSender(InetAddress host) {
    try {
      AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
      line = AudioSystem.getTargetDataLine(format);
      line.open();
      line.start();

      this.socket = new DatagramSocket();
      this.host = host;

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
      DatagramPacket packet = new DatagramPacket(data, data.length, host, 9001);
      socket.send(packet);
    }
  }
}
