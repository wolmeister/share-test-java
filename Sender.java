import java.net.InetAddress;

public class Sender {
  public static void main(String[] args) throws Exception {
    InetAddress host = InetAddress.getLocalHost();

    ScreenSender screenSender = new ScreenSender(host);
    screenSender.start();

    SoundSender soundSender = new SoundSender(host);
    soundSender.start();
  }
}
