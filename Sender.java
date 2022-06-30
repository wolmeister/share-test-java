public class Sender {
  public static void main(String[] args) {
    ScreenSender screenSender = new ScreenSender();
    screenSender.start();

    SoundSender soundSender = new SoundSender();
    soundSender.start();
  }
}
