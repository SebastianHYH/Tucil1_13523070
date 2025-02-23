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
        Color.ORANGE, 
        new Color(255,178,102), // Color.LIGHT_ORANGE,
        new Color(76,0,153), // Color.PURPLE,
        Color.PINK,
        new Color(102,255,102), // Color.BRIGHT_LIME_GREEN,
        new Color(153,204,255), // Color.SKY_BLUE,
        new Color(255, 215, 0), //Color.GOLD,
        new Color(102,51,0), // Color.BROWN,
        Color.LIGHT_GRAY,
        new Color(0,0,153), // Color.DEEP_BLUE,
        new Color(255,102,102), // Color.BRIGHT_RED,
        new Color(153,255,153), // Color.BRIGHT_GREEN,
        new Color(255,255,153), // Color.BRIGHT_YELLOW,
        new Color(204,229,255), // Color.BRIGHT_BLUE,
        new Color(229,204,255), // Color.BRIGHT_MAGENTA,
        new Color(204,255,255), // Color.BRIGHT_CYAN, 
        Color.DARK_GRAY, 
        Color.WHITE,
        new Color(153,0,0), // Color.DARK_RED, 
        new Color(0,102,0)// Color.DARK_GREEN,
    };

    public static void main(char[][] board, int rows, int cols, String testCasePath, String filename) {
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

        try {
            File testCaseDir = new File(testCasePath).getParentFile();
            File imagesDir = new File(testCaseDir, "images");
            if (!imagesDir.exists()) {
                imagesDir.mkdir();
            }

            File outputFile = new File(imagesDir, filename + ".png");
            ImageIO.write(image, "png", outputFile);
            System.out.println("Gambar berhasil disimpan di " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Gagal menyimpan gambar");
        }
    }

}
