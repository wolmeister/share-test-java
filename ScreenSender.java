import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import javax.imageio.ImageIO;

public class ScreenSender extends Thread {
  private void shareTo(byte[] imageData, String IP, int PORT) throws Exception {
    // InetAddress internetAddress = InetAddress.getByName(IP);
    // DatagramSocket socket = new DatagramSocket();
    // DatagramPacket dp = new DatagramPacket(imageData, imageData.length,
    // internetAddress, PORT);
    // socket.send(dp);
  }

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

        System.out.println("last=" + last);

        // int size = (flags & FINISH_SESSION) != FINISH_SESSION ? MAX_DATAGRAM
        // : imageArrayBytes.length - i * MAX_DATAGRAM;
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
        // arrayBytes[1] = (byte) packages;
        // arrayBytes[2] = (byte) (MAX_DATAGRAM >> 8);
        // arrayBytes[3] = (byte) MAX_DATAGRAM;
        // arrayBytes[4] = (byte) i;
        // arrayBytes[5] = (byte) (size >> 8);
        // arrayBytes[6] = (byte) size;

        System.arraycopy(screenshot, i * Constants.MAX_DATAGRAM_SIZE, data, Constants.HEADER_SIZE, size);

        // myScreen.shareTo(arrayBytes, "127.0.0.1", 6000);

        // if ((flags & FINISH_SESSION) == FINISH_SESSION)
        // break;

        // Send the packet
        DatagramPacket packet = new DatagramPacket(data, data.length, localhost, 9000);
        socket.send(packet);
      }

      Thread.sleep(1600);
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

  public static void main(String[] args) throws Exception {
    // ScreenSender myScreen = new ScreenSender();

    // while (true) {
    // // BufferedImage optimizedImage = getOptimizedImage();
    // // byte[] imageArrayBytes = transformImageToBytes(optimizedImage);
    // byte[] imageArrayBytes = new ScreenSender().getScreenshot();

    // int packages = (int) Math.ceil(imageArrayBytes.length / (float)
    // MAX_DATAGRAM);

    // if (packages > MAX_PACKAGE_SIZE)
    // continue;

    // for (int i = 0; i <= packages; i++) {
    // int flags = 0;
    // flags = i == 0 ? flags | START_SESSION : flags;
    // flags = (i + 1) * MAX_DATAGRAM > imageArrayBytes.length ? flags |
    // FINISH_SESSION : flags;

    // int size = (flags & FINISH_SESSION) != FINISH_SESSION ? MAX_DATAGRAM
    // : imageArrayBytes.length - i * MAX_DATAGRAM;

    // byte[] arrayBytes = new byte[HEADER_SIZE + size];
    // arrayBytes[0] = (byte) flags;
    // arrayBytes[1] = (byte) packages;
    // arrayBytes[2] = (byte) (MAX_DATAGRAM >> 8);
    // arrayBytes[3] = (byte) MAX_DATAGRAM;
    // arrayBytes[4] = (byte) i;
    // arrayBytes[5] = (byte) (size >> 8);
    // arrayBytes[6] = (byte) size;

    // System.arraycopy(imageArrayBytes, i * MAX_DATAGRAM, arrayBytes, HEADER_SIZE,
    // size);

    // myScreen.shareTo(arrayBytes, "127.0.0.1", 6000);

    // if ((flags & FINISH_SESSION) == FINISH_SESSION)
    // break;
    // }

    // Thread.sleep(80);

    // }
  }

}
