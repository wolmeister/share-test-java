public class Receiver {
  public static void main(String[] args) {
    ScreenReceiver screenReceiver = new ScreenReceiver();
    screenReceiver.start();

    SoundReceiver soundReceiver = new SoundReceiver();
    soundReceiver.start();
  }
}
