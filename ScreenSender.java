import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.imageio.ImageIO;

public class ScreenSender extends Thread {
  private final Robot robot;
  private final Rectangle screenSize;
  private final Rectangle sendSize;
  private final DatagramSocket socket;
  private final InetAddress localhost;

  public ScreenSender() {
    try {
      this.robot = new Robot();
      this.screenSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

      int scaledWidth = (int) (screenSize.width * 0.5);
      int scaledHeight = (int) (screenSize.height * 0.5);
      this.sendSize = new Rectangle(scaledWidth, scaledHeight);

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
      // Captures the screen and check if the size is valid
      byte[] screenshot = getScreenshot();
      int packets = (int) Math.ceil(screenshot.length / (float) Constants.MAX_DATAGRAM_SIZE);

      if (packets > Constants.MAX_PACKETS) {
        continue;
      }

      byte[] totalSizeInBytes = ByteUtils.intToBytes(screenshot.length);

      // Send all the packets
      for (int i = 0; i < packets; i++) {
        boolean first = i == 0;
        boolean last = i == packets - 1;
        int flags = 0;

        if (first) {
          flags = flags | Constants.START_FLAG;
        }
        if (last) {
          flags = flags | Constants.END_FLAG;
        }

        int size = Constants.MAX_DATAGRAM_SIZE;
        if (first && last) {
          size = screenshot.length;
        } else if (last) {
          size = screenshot.length - i * Constants.MAX_DATAGRAM_SIZE;
        }

        byte[] data = new byte[Constants.HEADER_SIZE + size];
        data[0] = (byte) flags;

        byte[] sizeInBytes = ByteUtils.intToBytes(size);
        System.arraycopy(sizeInBytes, 0, data, 1, sizeInBytes.length);
        System.arraycopy(totalSizeInBytes, 0, data, 5, totalSizeInBytes.length);
        System.arraycopy(screenshot, i * Constants.MAX_DATAGRAM_SIZE, data, Constants.HEADER_SIZE, size);

        // Send the packet
        DatagramPacket packet = new DatagramPacket(data, data.length, localhost, 9000);
        socket.send(packet);
      }

      Thread.sleep(60);
    }
  }

  private byte[] getScreenshot() throws Exception {
    // Captures the screen
    BufferedImage image = robot.createScreenCapture(screenSize);

    // Resize the image
    BufferedImage resizedImage = new BufferedImage(sendSize.width, sendSize.height, image.getType());
    Graphics2D g2d = resizedImage.createGraphics();
    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2d.drawImage(image, 0, 0, sendSize.width, sendSize.height, 0, 0, image.getWidth(), image.getHeight(), null);
    g2d.dispose();

    // Convert to bytes
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(resizedImage, "jpg", baos);
    return baos.toByteArray();
  }
}
