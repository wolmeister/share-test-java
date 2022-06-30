import java.net.InetAddress;

public class Test {
  public static void main(String[] args) throws Exception {
    InetAddress host = InetAddress.getLocalHost();

    ScreenSender screenSender = new ScreenSender(host);
    screenSender.start();
    SoundSender soundSender = new SoundSender(host);
    soundSender.start();

    ScreenReceiver screenReceiver = new ScreenReceiver();
    screenReceiver.start();
    SoundReceiver soundReceiver = new SoundReceiver();
    soundReceiver.start();
  }
}
