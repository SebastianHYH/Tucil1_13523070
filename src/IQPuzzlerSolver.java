import java.io.*;
import java.util.*;


public class IQPuzzlerSolver {
    private static int rows, cols, pieceCount;
    private static char[][] board;
    private static char[][] maskBoard; // For Masking the board
    private static List<char[][]> pieces = new ArrayList<>();
    private static long iterations = 0;
    // COLORS //
    private static final String[] COLORS = {
        "\u001B[31m", // Red
        "\u001B[32m", // Green
        "\u001B[33m", // Yellow
        "\u001B[34m", // Blue
        "\u001B[35m", // Magenta
        "\u001B[36m", // Cyan
        "\u001B[38;5;208m", // Orange
        "\u001B[38;5;214m", // Light Orange
        "\u001B[38;5;165m", // Purple
        "\u001B[38;5;200m", // Pink
        "\u001B[38;5;118m", // Bright Lime Green
        "\u001B[38;5;75m",  // Sky Blue
        "\u001B[38;5;220m", // Gold
        "\u001B[38;5;130m", // Brown
        "\u001B[38;5;255m", // Light Gray
        "\u001B[38;5;21m",  // Deep Blue
        "\u001B[91m", // Bright Red
        "\u001B[92m", // Bright Green
        "\u001B[93m", // Bright Yellow
        "\u001B[94m", // Bright Blue
        "\u001B[95m", // Bright Magenta
        "\u001B[96m", // Bright Cyan
        "\u001B[90m", // Dark Gray
        "\u001B[97m",  // White
        "\u001B[38;5;196m", // Dark Red
        "\u001B[38;5;46m",  // Dark Green
    };
    private static final String RESET = "\u001B[0m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan path test case(.txt): ");
        String filePath = scanner.nextLine();
        System.out.println("");
        if (!readInputFile(filePath)) {
            System.out.println("Gagal membaca file test case atau file tidak ada.\n");
            scanner.close();
            return;
        }
        // PIECE DEBUGGING //
        
        // System.out.println("Pieces:");
        // for (char[][] piece : pieces) {
        //     printMatrix(piece);
        //     System.out.println();
        //     System.out.println("Transformasi:");
        //     for (char[][] transformation : generateTransformations(piece)) {
        //         printMatrix(transformation);
        //         System.out.println();
        //     }
        // }
        
        long startTime = System.currentTimeMillis();
        boolean solved = solve(0);
        long endTime = System.currentTimeMillis();
        
        if (solved) {
            printBoard();
        } else {
            System.out.println("Tidak ditemukan solusi.\n");
        }
        
        System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms\n");
        System.out.println("Banyak kasus yang ditinjau: " + iterations +"\n");

        System.out.print("Apakah anda ingin menyimpan solusi sebagai txt? (ya/tidak): ");
        if (scanner.nextLine().equalsIgnoreCase("ya")) {
            saveSolution(filePath + "_solution.txt");
        }
        System.out.print("Apakah anda ingin menyimpan solusi sebagai gambar? (ya/tidak): ");
        if (scanner.nextLine().equalsIgnoreCase("ya")) {
            System.out.print("Masukkan nama file: ");
            String filename = scanner.nextLine();
            SaveAsImage.main(board, rows, cols, filename);
        }
        scanner.close();
    }
    
    private static boolean readInputFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String[] dimensions = br.readLine().split(" ");
            rows = Integer.parseInt(dimensions[0]);
            cols = Integer.parseInt(dimensions[1]);
            pieceCount = Integer.parseInt(dimensions[2]);
            maskBoard = new char[rows][cols];
            board = new char[rows][cols];
            String boardType = br.readLine(); // DEFAULT, CUSTOM, PYRAMID
            if (boardType.equals("DEFAULT")) {
                for (char[] row : maskBoard) Arrays.fill(row, 'X');
            } else if (boardType.equals("CUSTOM")) {
                for (int i = 0; i < rows; i++) {
                    maskBoard[i] = br.readLine().toCharArray();
                }
            }
            for (char[] row : board) Arrays.fill(row, '.');
            
            int piecesRead = 0;
            String currentLetter = "";
            List<String> shapeLines = new ArrayList<>();
            String line;

            while ((line = br.readLine()) != null) {
                line = line.stripTrailing(); // Menghapus space tambahan
                if (line.isEmpty()) continue; // Melewati barisan kosong
                
                char firstChar = line.trim().charAt(0); // Memeriksa huruf
                if (currentLetter.isEmpty() || firstChar == currentLetter.charAt(0)) {
                    shapeLines.add(line);
                } else {
                    // Simpan piece sebelum, tambah piece lain
                    pieces.add(convertToMatrix(shapeLines, currentLetter.charAt(0)));
                    piecesRead++;
                    shapeLines.clear();
                    shapeLines.add(line);
                }
                currentLetter = String.valueOf(firstChar);
            }
            
            // Piece terakhir
            if (!shapeLines.isEmpty()) {
                pieces.add(convertToMatrix(shapeLines, currentLetter.charAt(0)));
                piecesRead++;
            }
            
            return piecesRead == pieceCount;
        } catch (Exception e) {
            // e.printStackTrace();
            // System.out.println("Gagal membaca file atau file tidak ada.\n");
            return false;
        }
    }
    
    // For printing each piece (DEBUGGING)
    // private static void printMatrix(char[][] matrix) {
    //     for (char[] row : matrix) {
    //         for (char cell : row) {
    //             System.out.print(cell == ' ' ? '.' : cell);
    //             System.out.print(" ");
    //         }
    //         System.out.println();
    //     }
    // }

    private static char[][] convertToMatrix(List<String> shapeLines, char letter) {
        int h = shapeLines.size();
        int w = shapeLines.stream().mapToInt(String::length).max().orElse(0);
        
        // Membuat matrix kosong
        char[][] shape = new char[h][w];
        for (int i = 0; i < h; i++) {
            Arrays.fill(shape[i], ' ');
            String line = shapeLines.get(i);
            for (int j = 0; j < line.length(); j++) {
                if (line.charAt(j) == letter) {
                    shape[i][j] = letter; // Matrix diisi dengan huruf
                }
            }
        }
        return shape;
    }
    
    private static boolean solve(int pieceIndex) {
        if (pieceIndex >= pieces.size()) return true;
        // System.out.println("Mencoba piece: " + pieceIndex); // DEBUGGING
        
        char[][] piece = pieces.get(pieceIndex);
        List<char[][]> transformations = generateTransformations(piece);
        
        for (char[][] transformedPiece : transformations) {
            for (int r = 0; r <= rows - transformedPiece.length; r++) {
                for (int c = 0; c <= cols - transformedPiece[0].length; c++) {
                    if (canPlace(transformedPiece, r, c)) {
                        char shapeSymbol = piece[0][0];
                        int symbolPicker = 1;
                        while (shapeSymbol == ' ') { //              Kalau tile atas kiri kosong, cari tile lain
                            shapeSymbol = piece[0][symbolPicker]; // e.g: .A
                            symbolPicker++;                       //      AA
                        }                                         // where dot = empty space
                        // System.out.println("Place " + shapeSymbol + " at (" + r + ", " + c + ")"); DEBUGGING
                        placePiece(transformedPiece, r, c, shapeSymbol);
                        iterations++;
                        // printBoard(); // DEBUGGING
                        // System.out.println("Iterasi: " + iterations); //DEBUGGING
                        
                        if (solve(pieceIndex + 1)) return true;
                        
                        // System.out.println("Remove " + piece[0][0] + " from (" + r + ", " + c + ")"); // DEBUGGING
                        removePiece(transformedPiece, r, c);
                    }
                }
            }
        }
        return false;
    }
    
    private static List<char[][]> generateTransformations(char[][] piece) {
        Set<String> uniqueTransformations = new HashSet<>();
        List<char[][]> transformations = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            piece = rotate90(piece);
            if (uniqueTransformations.add(Arrays.deepToString(piece))) {
                transformations.add(copyMatrix(piece));
            }
            char[][] mirrored = mirror(piece);
            if (uniqueTransformations.add(Arrays.deepToString(mirrored))) {
                transformations.add(copyMatrix(mirrored));
            }
        }
        return transformations;
    }

    private static char[][] copyMatrix(char[][] matrix) {
        char[][] copy = new char[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, matrix[i].length);
        }
        return copy;
    } 
    // Modify pieces
    private static char[][] rotate90(char[][] piece) {
        int h = piece.length, w = piece[0].length;
        char[][] rotated = new char[w][h];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                rotated[j][h - 1 - i] = piece[i][j];
            }
        }
        return rotated;
    }
    
    private static char[][] mirror(char[][] piece) {
        int h = piece.length, w = piece[0].length;
        char[][] mirrored = new char[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                mirrored[i][w - 1 - j] = piece[i][j];
            }
        }
        return mirrored;
    }
    
    private static boolean canPlace(char[][] piece, int r, int c) {
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                if (piece[i][j] != ' ') {
                    if (r + i >= rows || c + j >= cols || board[r + i][c + j] != '.' || maskBoard[r + i][c + j] != 'X') {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void placePiece(char[][] piece, int r, int c, char symbol) {
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                if (piece[i][j] != ' ') {
                    board[r + i][c + j] = symbol;
                }
            }
        }
    }
    
    private static void removePiece(char[][] piece, int r, int c) {
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                if (piece[i][j] != ' ') {
                    board[r + i][c + j] = '.';
                }
            }
        }
    }
    
    private static void printBoard() { // Print board
        Map<Character, String> colorMap = new HashMap<>();
        
        for (char c = 'A'; c <= 'Z'; c++) {
            colorMap.put(c, COLORS[(c - 'A') % COLORS.length]);
        }
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char cell = board[i][j];

                if (cell == '.') { 
                    System.out.print((maskBoard[i][j] == 'X' ? "." : " ") + " "); // Menyembunyikan . dan/atau X pada board
                } else {
                    System.out.print(colorMap.get(cell) + cell + RESET + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void saveSolution(String outputPath) { // File .txt
        try (PrintWriter writer = new PrintWriter(outputPath)) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    char cell = board[i][j];
                    writer.print(cell == '.' ? " " : cell); // Mengubah bentuk . ke spasi untuk board custom
                    writer.print(" ");
                }
                writer.println();
            }
            System.out.println("File berhasil disimpan di " + outputPath);
        } catch (IOException e) {
            System.out.println("Gagal menyimpan solusi.");
        }
    }
}
