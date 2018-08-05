import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player4 {

public static int[][] grid = new int[3][3];
    public static int myId = 1;
    public static int opponentId = 2;
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            int opponentRow = in.nextInt();
            int opponentCol = in.nextInt();
            int validActionCount = in.nextInt();
            for (int i = 0; i < validActionCount; i++) {
                int row = in.nextInt();
                int col = in.nextInt();
                System.err.println(row + "," + col);
            }

            if (opponentRow != -1 && opponentCol != -1){
                grid[opponentRow][opponentCol] = opponentId;
            }

            if (checkRowOrLine(grid, myId)){
                continue;
            }
            if (checkOblique(grid, myId, 0, 0, 1, 1, 2, 2)){
                continue;
            }
            if (checkOblique(grid, myId, 0, 2, 1, 1, 2, 0)){
                continue;
            }

            if (bestPosition(grid)){
                continue;
            }

            if (otherPosition(grid)) {
                continue;
            }
        }
    }
    private static boolean otherPosition(int[][] grid) {
        for (int r = 0; r < 3; r ++){
            for (int c = 0; c < 3; c ++){
                if (grid[r][c] == 0){
                    grid[r][c] = myId;
                    System.out.println(r + " " + c);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean bestPosition(int[][] grid) {
        if (grid[1][1] == 0){
            grid[1][1] = myId;
            System.out.println("1 1");
            return true;
        }

        if (grid[0][0] == 0){
            grid[0][0] = myId;
            System.out.println("0 0");
            return true;
        }

        if (grid[0][2] == 0){
            grid[0][2] = myId;
            System.out.println("0 2");
            return true;
        }

        if (grid[2][0] == 0){
            grid[2][0] = myId;
            System.out.println("2 0");
            return true;
        }

        if (grid[2][2] == 0){
            grid[2][2] = myId;
            System.out.println("2 2");
            return true;
        }
        return false;
    }

    private static boolean checkOblique(int[][] grid, int myId, int x1, int y1, int x2, int y2, int x3, int y3) {
        int outputR = -1;
        int outputC = -1;
        int playCount = 0;
        int opponentCount = 0;
        if (grid[x1][y1] == myId){
            playCount ++;
        }else if(grid[x1][y1] == 0){
            outputR = x1;
            outputC = y1;
        }else {
            opponentCount ++;
        }
        if (grid[x2][y2] == myId){
            playCount ++;
        }else if(grid[x2][y2] == 0){
            outputR = x2;
            outputC = y2;
        }else {
            opponentCount ++;
        }
        if (grid[x3][y3] == myId){
            playCount ++;
        }else if(grid[x3][y3] == 0){
            outputR = x3;
            outputC = y3;
        }else {
            opponentCount ++;
        }
        if (playCount == 2 && outputR != -1 && outputC != -1){
            grid[outputR][outputC] = myId;
            System.out.println(outputR +" " + outputC);
            return true;
        }
        if (opponentCount == 2 && outputR != -1 && outputC != -1){
            grid[outputR][outputC] = myId;
            System.out.println(outputR +" " + outputC);
            return true;
        }
        return false;
    }

    private static boolean checkRowOrLine(int[][] grid, int myId) {
        for (int r = 0; r < 3; r++){
            int rowR = -1;
            int rowC = -1;
            int rowOpponentCount = 0;
            int rowPlayCount = 0;
            int lineR = -1;
            int lineC = -1;
            int lineOpponentCount = 0;
            int linePlayCount = 0;
            for(int c = 0; c < 3; c++){
                if (grid[r][c] == myId){
                    rowPlayCount ++;
                }else if(grid[r][c] == 0){
                    rowR = r;
                    rowC = c;
                }else{
                    rowOpponentCount ++;
                }

                if (grid[c][r] == myId){
                    linePlayCount ++;
                }else if(grid[c][r] == 0){
                    lineR = c;
                    lineC = r;
                }else {
                    lineOpponentCount ++;
                }
            }
            if (rowPlayCount == 2 && rowR != -1 && rowC != -1){
                grid[rowR][rowC] = myId;
                System.out.println(String.format("%d %d", rowR, rowC));
                return true;
            }
            if (linePlayCount == 2 && lineC != -1 && lineR != -1){
                grid[lineR][lineC] = myId;
                System.out.println(String.format("%d %d", lineR, lineC));
                return true;
            }
            if (rowOpponentCount == 2 && rowR != -1 && rowC != -1){
                grid[rowR][rowC] = myId;
                System.out.println(String.format("%d %d", rowR, rowC));
                return true;
            }
            if (lineOpponentCount == 2 && lineC != -1 && lineR != -1){
                grid[lineR][lineC] = myId;
                System.out.println(String.format("%d %d", lineR, lineC));
                return true;
            }
        }
        return false;
    }
}