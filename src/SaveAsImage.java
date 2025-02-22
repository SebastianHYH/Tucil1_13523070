import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class SaveAsImage {
    private static final Color[] IMAGE_COLORS = {
        Color.RED, 
        Color.GREEN, 
        Color.YELLOW, 
        Color.BLUE, 
        Color.MAGENTA, 
        Color.CYAN,
        Color.PINK, 
        Color.ORANGE, 
        Color.LIGHT_GRAY, 
        Color.DARK_GRAY, 
        Color.WHITE, 
        Color.BLACK,
        new Color(255, 140, 0), 
        new Color(255, 215, 0), 
        new Color(128, 0, 128),
        new Color(255, 105, 180), 
        new Color(144, 238, 144), 
        new Color(0, 191, 255),
        new Color(255, 223, 0), 
        new Color(139, 69, 19), 
        new Color(173, 216, 230),
        new Color(85, 107, 47), 
        new Color(255, 99, 71), 
        new Color(70, 130, 180),
        new Color(138, 43, 226)
    };

    public static void main(char[][] board, int rows, int cols, String filename) {
        int tileSize = 50;
        int width = cols * tileSize;
        int height = rows * tileSize;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        Map<Character, Color> colorMap = new HashMap<>();
        int colorIndex = 0;

        for (char[] row : board) {
            for (char tile : row) {
                if (tile != '.' && !colorMap.containsKey(tile)) {
                    colorMap.put(tile, IMAGE_COLORS[colorIndex % IMAGE_COLORS.length]);
                    colorIndex++;
                }
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char tile = board[r][c];
                if (tile != '.') {
                    graphics.setColor(colorMap.get(tile));
                    graphics.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);

                    graphics.setColor(Color.BLACK);
                    graphics.drawRect(c * tileSize, r * tileSize, tileSize, tileSize);
                    graphics.setColor(Color.WHITE);
                    graphics.drawString(String.valueOf(tile), c * tileSize + 20, r * tileSize + 30);
                }
            }
        }

        graphics.dispose();

        try{
            ImageIO.write(image, "png", new File(filename+".png"));
            System.out.println("Gambar berhasil disimpan dengan nama " + filename + ".png");
        } catch (IOException e) {
            System.out.println("Gagal menyimpan gambar");
        }
    }

}
