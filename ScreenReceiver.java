import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ScreenReceiver extends Thread {

  private final JFrame frame;
  private final JLabel labelImage;

  private final DatagramSocket socket;

  public ScreenReceiver() {
    try {
      this.socket = new DatagramSocket(9000);

      labelImage = new JLabel();
      frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().add(labelImage);
      frame.setSize(670, 670);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
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
    byte[] buffer = new byte[Constants.MAX_DATAGRAM_SIZE + Constants.HEADER_SIZE];
    byte[] imageData = null;
    int copiedSize = 0;

    while (true) {
      DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
      socket.receive(dp);
      byte[] data = dp.getData();
      int flags = data[0];

      byte[] sizeInBytes = new byte[4];
      System.arraycopy(data, 1, sizeInBytes, 0, 4);
      int size = ByteUtils.bytesToInt(sizeInBytes);

      if ((flags & Constants.START_FLAG) == Constants.START_FLAG) {
        byte[] totalSizeInBytes = new byte[4];
        System.arraycopy(data, 5, totalSizeInBytes, 0, 4);
        int totalSize = ByteUtils.bytesToInt(totalSizeInBytes);
        imageData = new byte[totalSize];
        copiedSize = 0;
      }

      System.arraycopy(data, Constants.HEADER_SIZE, imageData, copiedSize, size);
      copiedSize += size;

      if ((flags & Constants.END_FLAG) == Constants.END_FLAG) {
        ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
        BufferedImage imagem = ImageIO.read(bis);
        labelImage.setIcon(new ImageIcon(imagem));
        frame.pack();
      }
    }
  }
}
