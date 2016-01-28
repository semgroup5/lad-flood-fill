import java.awt.*;
import java.awt.image.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

@SuppressWarnings("serial")

/** A small application that displays an image in black and white,
 *  and fills contiguous black areas red if you click on them. */
public class FloodFiller extends JPanel implements MouseListener {

    private BufferedImage img;

    public void turnBlacknWhite() {
        int x, y;
        int w = img.getWidth();
        int h = img.getHeight();
        // first compute the mean intensity
        int totintensity = 0;
        for (y = 0; y < h; y++) {
            for (x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                totintensity += (rgb >> 16) & 0xFF + (rgb >> 8) & 0xFF + rgb & 0xFF;
            }
        }
        int meanintensity = totintensity / (w * h);
        for (y = 0; y < h; y++) {
            for (x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int intensity = (rgb >> 16) & 0xFF + (rgb >> 8) & 0xFF + rgb & 0xFF;
                if (intensity < meanintensity) {  // it's darker than mean intensity
                    img.setRGB(x, y, Color.BLACK.getRGB());  // turn black
                } else {  // it's lighter
                    img.setRGB(x, y, Color.WHITE.getRGB());  // turn white
                }
            }
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
        floodFill(e.getX(), e.getY(), Color.RED.getRGB());
        this.repaint();
    }

    /**
     * Paint the black area including and around (x,y) in the specified color.
     * If (x,y) is not black, do nothing.
     */
    public void floodFill(int x, int y, int color) {
        if((x > 0 && x < img.getWidth()) && (y > 0 && y < img.getHeight())){
            if(img.getRGB(x,y) == Color.BLACK.getRGB()){
                img.setRGB(x, y, color);
                floodFill(x+1, y, color);
                floodFill(x-1, y, color);
                floodFill(x, y+1, color);
                floodFill(x, y-1, color);
            }
        }
    }

    private class Pixel{
        public int x;
        public int y;

        public Pixel(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }

    java.util.LinkedList<Pixel> pixelList;

    public void floodFillIt(int x, int y, int color) {
        pixelList = new LinkedList<>();
        pixelList.add(new Pixel(x, y));
        while (!pixelList.isEmpty()) {
            x = pixelList.peek().x;
            y = pixelList.peek().y;
            if (inBounds(x,y)) {
                setPixel(pixelList.pop(), color);
                if (isBlack(x + 1, y))
                    pixelList.push(new Pixel(x + 1, y));
                if (isBlack(x - 1, y))
                    pixelList.push(new Pixel(x - 1, y));
                if (isBlack(x, y + 1))
                    pixelList.push(new Pixel(x, y + 1));
                if (isBlack(x, y - 1))
                    pixelList.push(new Pixel(x, y - 1));
            }
        }
    }


    public void setPixel(Pixel p, int color)
    {
        img.setRGB(p.x, p.y, color);
    }

    public boolean inBounds(int x, int y)
    {
        return (x > 0 && x < img.getWidth()) && (y > 0 && y < img.getHeight());
    }

    public boolean isBlack(int x,int y)
    {
        if(!inBounds(x,y)) return false;
        return img.getRGB(x,y) == Color.BLACK.getRGB();
    }

    public FloodFiller(String fileName) {
        try {
            img = ImageIO.read(new File(fileName));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        turnBlacknWhite();
        setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
        this.addMouseListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);
        g.drawImage(img, 0, 0, null);
    }

    public static void main(String[] args) {
        final String fileName = args[0];
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Flood Filler");
                frame.setContentPane(new FloodFiller(fileName));
                frame.pack();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
