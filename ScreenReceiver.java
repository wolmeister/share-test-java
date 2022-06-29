import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ScreenReceiver {

  private void imageReceive(String ipAddress, int port) {
    DatagramSocket socket = null;
    JLabel labelImage = new JLabel();
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(labelImage);
    frame.setSize(670, 670);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    try {
      socket = new DatagramSocket(9000);

      // int storage = 0;
      // int[] column = null;
      byte[] imageData = null;
      int copiedSize = 0;
      byte[] buffer = new byte[65507];

      while (true) {
        System.out.println("Sender");

        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
        socket.receive(dp);
        byte[] data = dp.getData();
        int flags = data[0];

        byte[] sizeInBytes = new byte[4];
        System.arraycopy(data, 1, sizeInBytes, 0, 4);
        int size = ByteUtils.bytesToInt(sizeInBytes);
        imageData = new byte[size];

        if ((flags & Constants.START_FLAG) == Constants.START_FLAG) {
          byte[] totalSizeInBytes = new byte[4];
          System.arraycopy(data, 5, totalSizeInBytes, 0, 4);
          int totalSize = ByteUtils.bytesToInt(totalSizeInBytes);
          imageData = new byte[totalSize];
          copiedSize = 0;
        }

        System.arraycopy(data, Constants.HEADER_SIZE, imageData, copiedSize, size);
        copiedSize += size;

        System.out.println(flags & Constants.END_FLAG);

        if ((flags & Constants.END_FLAG) == Constants.END_FLAG) {
          ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
          BufferedImage imagem = ImageIO.read(bis);
          labelImage.setIcon(new ImageIcon(imagem));
          frame.pack();
        }

        // if (last) {
        // flags = flags | Constants.END_FLAG;
        // }

        // short parts = (short) (data[1] & 0xff);
        // int maxPackageSize = (int) ((data[2] & 0xff) << 8 | (data[3] & 0xff));
        // short part = (short) (data[4] & 0xff);
        // int size = (int) ((data[5] & 0xff) << 8 | (data[6] & 0xff));

        // if ((data[0] & 128) == 128) {
        // storage = 0;
        // imageData = new byte[parts * maxPackageSize];
        // column = new int[parts];
        // }

        // if (column != null && column[part] == 0) {
        // column[part] = 1;
        // System.arraycopy(data, 8, imageData, part * maxPackageSize, size);
        // storage++;
        // }

        // if (storage == parts) {
        // ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
        // BufferedImage imagem = ImageIO.read(bis);
        // labelImage.setIcon(new ImageIcon(imagem));
        // frame.pack();
        // }

      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (socket != null) {
        socket.close();
      }
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    ScreenReceiver program = new ScreenReceiver();
    program.imageReceive("127.0.0.1", 6000);
  }

}
