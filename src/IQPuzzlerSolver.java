import java.io.*;
import java.util.*;


public class IQPuzzlerSolver {
    private static int rows, cols, pieceCount;
    private static char[][] board;
    private static List<char[][]> pieces = new ArrayList<>();
    private static long iterations = 0;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter test case file path: ");
        String filePath = scanner.nextLine();

        if (!readInputFile(filePath)) {
            System.out.println("Failed to read input file.");
            return;
        }
        
        System.out.println("Parsed pieces:");
        for (char[][] piece : pieces) {
            printMatrix(piece);
            System.out.println();
            System.out.println("Transformations:");
            for (char[][] transformation : generateTransformations(piece)) {
                printMatrix(transformation);
                System.out.println();
            }
        }
        
        long startTime = System.currentTimeMillis();
        boolean solved = solve(0);
        long endTime = System.currentTimeMillis();
        
        if (solved) {
            printBoard();
        } else {
            System.out.println("No solution found.");
        }
        
        System.out.println("Execution time: " + (endTime - startTime) + " ms");
        System.out.println("Total iterations: " + iterations);

        System.out.print("Save solution? (yes/no): ");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            saveSolution(filePath + "_solution.txt");
        }
    }
    
    private static boolean readInputFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String[] dimensions = br.readLine().split(" ");
            rows = Integer.parseInt(dimensions[0]);
            cols = Integer.parseInt(dimensions[1]);
            pieceCount = Integer.parseInt(dimensions[2]);
            
            String boardType = br.readLine(); // DEFAULT, CUSTOM, PYRAMID
            board = new char[rows][cols];
            for (char[] row : board) Arrays.fill(row, '.');
            
            int piecesRead = 0;
            String currentLetter = "";
            List<String> shapeLines = new ArrayList<>();
            
            String line;
            while ((line = br.readLine()) != null) {
                line = line.stripTrailing(); // Remove only trailing spaces
                if (line.isEmpty()) continue; // Skip empty lines but don’t reset parsing
                
                char firstChar = line.trim().charAt(0); // Detect first non-space character
                if (currentLetter.isEmpty() || firstChar == currentLetter.charAt(0)) {
                    shapeLines.add(line);
                } else {
                    // Store previous piece and start a new one
                    pieces.add(convertToMatrix(shapeLines, currentLetter.charAt(0)));
                    piecesRead++;
                    shapeLines.clear();
                    shapeLines.add(line);
                }
                currentLetter = String.valueOf(firstChar);
            }
            
            // Add last piece
            if (!shapeLines.isEmpty()) {
                pieces.add(convertToMatrix(shapeLines, currentLetter.charAt(0)));
                piecesRead++;
            }
            
            return piecesRead == pieceCount;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static void printMatrix(char[][] matrix) {
        for (char[] row : matrix) {
            for (char cell : row) {
                System.out.print(cell == ' ' ? '.' : cell);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    private static char[][] convertToMatrix(List<String> shapeLines, char letter) {
        int h = shapeLines.size();
        int w = shapeLines.stream().mapToInt(String::length).max().orElse(0);
        
        // Create matrix preserving all spaces
        char[][] shape = new char[h][w];
        for (int i = 0; i < h; i++) {
            Arrays.fill(shape[i], ' '); // Default fill with spaces
            String line = shapeLines.get(i);
            for (int j = 0; j < line.length(); j++) {
                if (line.charAt(j) == letter) {
                    shape[i][j] = letter; // Preserve character placement
                }
            }
        }
        return shape;
    }
    
    private static boolean solve(int pieceIndex) {
        if (pieceIndex >= pieces.size()) return true;
        System.out.println("Trying piece index: " + pieceIndex);
        
        char[][] piece = pieces.get(pieceIndex);
        List<char[][]> transformations = generateTransformations(piece);
        
        for (char[][] transformedPiece : transformations) {
            for (int r = 0; r <= rows - transformedPiece.length; r++) {
                for (int c = 0; c <= cols - transformedPiece[0].length; c++) {
                    if (canPlace(transformedPiece, r, c)) {
                        System.out.println("Placing piece " + piece[0][0] + " at (" + r + ", " + c + ")");
                        placePiece(transformedPiece, r, c, piece[0][0]);
                        iterations++;
                        printBoard();
                        System.out.println("Iteration: " + iterations);
                        
                        if (solve(pieceIndex + 1)) return true;
                        
                        System.out.println("Removing piece " + piece[0][0] + " from (" + r + ", " + c + ")");
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
                if (piece[i][j] != ' ' && board[r + i][c + j] != '.') {
                    return false;
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
    
    private static void printBoard() {
        for (char[] row : board) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    private static void saveSolution(String outputPath) {
        try (PrintWriter writer = new PrintWriter(outputPath)) {
            for (char[] row : board) {
                writer.println(new String(row));
            }
            System.out.println("Solution saved to " + outputPath);
        } catch (IOException e) {
            System.out.println("Failed to save solution.");
        }
    }
}
