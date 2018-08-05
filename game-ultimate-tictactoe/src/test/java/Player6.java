import java.util.*;

class Player6 {

    public static final int P_ALL_WIN = 100000;         // 全局赢了
    public static final int P_ALL_LOST = 50000;         // 全局要输了


    public static final int P_CENTER_WIN = 11000;           // 中心的胜利
    public static final int P_ANGLE_WIN = 9000;            // 角的胜利
    public static final int P_SIDE_WIN = 7000;             // 边的胜利
    public static final int P_CENTER_LOSE = 5000;          // 中心的防止输
    public static final int P_ANGLE_LOSE = 3000;           // 角的防止输
    public static final int P_SIDE_LOSE = 1000;            // 边的泛指输

    public static final int P_CENTRE_CENTRE = 170;        // 中心的中心
    public static final int P_CENTRE_ANGLE = 150;         // 中心的角
    public static final int P_ANGLE_CENTRE = 130;         // 角的中心
    public static final int P_ANGLE_ANGLE = 110;          // 角的角
    public static final int P_SIDE_CENTRE = 90;          // 边的中心
    public static final int P_CENTRE_SIDE = 70;          // 中心的边
    public static final int P_ANGLE_SIDE = 50;           // 角的边
    public static final int P_SIDE_ANGLE = 30;           // 边的角
    public static final int P_SIDE_SIDE = 10;            // 边的边
    public static final int P_STEP = 1;                 // 在没有赢或者输的情况,好的位置

    public static final int[] P_WIN = {
            P_CENTER_WIN, P_ANGLE_WIN, P_SIDE_WIN
    };

    public static final int[] P_LOSE = {
            P_CENTER_LOSE, P_ANGLE_LOSE, P_SIDE_LOSE
    };
    public static final int[][] PROIORTY = {
            {P_CENTRE_CENTRE, P_CENTRE_ANGLE, P_CENTRE_SIDE},
            {P_ANGLE_CENTRE, P_ANGLE_ANGLE, P_ANGLE_SIDE},
            {P_SIDE_CENTRE, P_SIDE_ANGLE, P_SIDE_SIDE}
    };

    public static final int POS_CENTER = 0;
    public static final int POS_ANGLE = 1;
    public static final int POS_SIDE = 2;

    public static final int G_ENTMP = 0;
    public static final int G_PLAY = 10;
    public static final int G_OPPONENT = 200;
    static class ProiortyAction implements Comparable<ProiortyAction> {
        int row, col;
        int niceRow, niceCol;
        int nicePosition, position;
        int ninePos;
        int priority;

        public ProiortyAction(int row, int col) {
            this.ninePos = row / 3 * 3 + col / 3;
            this.niceRow = this.ninePos / 3;
            this.niceCol = this.ninePos % 3;
            this.row = row;
            this.col = col;
            if (this.niceRow == this.niceCol && this.niceRow == 1){
                this.nicePosition = POS_CENTER;
            }else if((this.niceRow + this.niceCol) % 2 == 0){
                this.nicePosition = POS_ANGLE;
            }else {
                this.nicePosition = POS_SIDE;
            }
            int r = this.row - this.niceRow * 3;
            int c = this.col - this.niceCol * 3;
            if (r == c && r == 1){
                this.position = POS_CENTER;
            }else if((r + c) % 2 == 0){
                this.position = POS_ANGLE;
            }else {
                this.position = POS_SIDE;
            }
            this.priority = PROIORTY[this.nicePosition][this.position];
        }

        public void addPriority(ArrayList<ProiortyAction> actions, int priority){
            int index = actions.indexOf(this);
            if (index != -1){
                ProiortyAction action = actions.get(index);
                actions.remove(action);
                action.priority = action.priority + priority;
                actions.add(action);
            }
        }

        @Override
        public int compareTo(ProiortyAction o) {
            return o.priority - this.priority;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ProiortyAction action = (ProiortyAction) o;

            if (row != action.row) return false;
            return col == action.col;
        }

        @Override
        public int hashCode() {
            int result = row;
            result = 31 * result + col;
            return result;
        }

        @Override
        public String toString() {
            return String.format("%d %d %s", this.row, this.col, "Come on " + this.priority);
        }

    }

    static class GridSum{
        int sum;
        ArrayList<ProiortyAction> actions;

        public GridSum(int sum, ArrayList<ProiortyAction> actions) {
            this.sum = sum;
            this.actions = actions;
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int[][] grid = new int[9][9];;
        int[][] grid2 = new int[3][3];
        ArrayList<ProiortyAction> allAction = new ArrayList<>();

        while (true) {
            int opponentRow = in.nextInt();
            int opponentCol = in.nextInt();
            ProiortyAction opponentAction = new ProiortyAction(opponentRow, opponentCol);
            int validActionCount = in.nextInt();


            ArrayList<ProiortyAction> actions = new ArrayList<>();
            for (int i = 0; i < validActionCount; i++) {
                int row = in.nextInt();
                int col = in.nextInt();
                ProiortyAction proiortyAction = new ProiortyAction(row, col);
                if (!allAction.contains(proiortyAction)){
                    allAction.add(proiortyAction);
                }
                actions.add(proiortyAction);
            }

            if (opponentAction.row != -1){
                grid[opponentAction.row][opponentAction.col] = G_OPPONENT;
                playGame(grid, opponentAction, allAction);
                checkWinner(grid, grid2, opponentAction, allAction, false);
            }

            List<ProiortyAction> otherAction = new ArrayList<>(allAction);
            otherAction.removeAll(actions);

            List<ProiortyAction> vaildAction = new ArrayList<>(allAction);
            vaildAction.removeAll(otherAction);

            PriorityQueue<ProiortyAction> queue = new PriorityQueue<>(vaildAction);
            ProiortyAction poll = queue.poll();
            allAction.remove(poll);
            System.out.println(poll);
            grid[poll.row][poll.col] = G_PLAY;
            playGame(grid, poll, allAction);
            checkWinner(grid, grid2, poll, allAction, true);
        }
    }

    private static void checkWinner(int[][] grid, int[][] grid2, ProiortyAction action, ArrayList<ProiortyAction> allAction, boolean isPlay) {
        int gValue = isPlay ? G_PLAY : G_OPPONENT;
        boolean isWin = false;
        List<GridSum> gridSumList = checkGrid(action.ninePos, grid);
        for (GridSum gridSum : gridSumList){
            if (gridSum.sum == gValue * 3) {
                isWin = true;
                break;
            }
        }
        if (!isWin){
            return;
        }

        grid2[action.niceRow][action.niceCol] = gValue;
        List<GridSum> gridAllSumList = checkGrid(0, grid2);
        for (GridSum gridSum : gridAllSumList){
            if (gridSum.sum == 2 * G_PLAY){
                ProiortyAction action1 = gridSum.actions.get(0);
                if(canWin(action1.ninePos, grid)){
                    addAllPriority(allAction, action1.row, action1.col,P_ALL_WIN);
                }else{
                    addAllPriority(allAction, action1.row, action1.col,- P_ALL_WIN);
                }
                System.err.println("canWin --> " + allAction);
            }else if (gridSum.sum == 2 * G_OPPONENT){
                ProiortyAction action2 = gridSum.actions.get(0);
                if(canLose(action2.ninePos, grid)){
                    addAllPriority(allAction, action2.row, action2.col,P_ALL_LOST);
                }else{
                    addAllPriority(allAction, action2.row, action2.col,- P_ALL_LOST);
                }
                System.err.println("canLose --> " + allAction);
            }
        }
    }

    private static boolean canWin(int ninePos, int[][] grid) {
        List<GridSum> gridSumList = checkGrid(ninePos, grid);
        for (GridSum gridSum : gridSumList){
            if (gridSum.sum <= 2*G_PLAY){
                return true;
            }
        }
        return false;
    }

    private static boolean canLose(int ninePos, int[][] grid) {
        List<GridSum> gridSumList = checkGrid(ninePos, grid);
        for (GridSum gridSum : gridSumList){
            if (gridSum.sum <= 2*G_PLAY){
                return false;
            }
        }
        return true;
    }

    private static void addAllPriority(ArrayList<ProiortyAction> allAction, int row, int col, int priority) {
        for (int x = 0; x < 3; x++){
            for (int y = 0; y < 3; y ++){
                new ProiortyAction(row * 3 + x, col * 3 + y).addPriority(allAction, priority);
            }
        }
    }

    private static void playGame(int[][] grid, ProiortyAction action, ArrayList<ProiortyAction> allAction) {
        List<GridSum> gridSumList = checkGrid(action.ninePos, grid);
        for (GridSum gridSum : gridSumList){
            changeProiorty(gridSum, allAction);
        }
    }

    private static void changeProiorty(GridSum gridSum, ArrayList<ProiortyAction> actions) {
        if (gridSum.actions.isEmpty()){
            return;
        }
        if (gridSum.sum == G_PLAY * 2){
            addWinPriority(actions, gridSum.actions);
        }else if(gridSum.sum == G_OPPONENT * 2){
            addLosePriority(actions, gridSum.actions);
        }else if (gridSum.sum == G_PLAY){
            addStepPriority(actions, gridSum.actions, P_STEP * 5);
        }else if (gridSum.sum == G_OPPONENT){
            addStepPriority(actions, gridSum.actions, P_STEP * 3);
        }else if (gridSum.sum == 0){
            addStepPriority(actions, gridSum.actions, P_STEP);
        }else if (gridSum.sum == G_PLAY + G_OPPONENT){
            addStepPriority(actions, gridSum.actions, - P_STEP*5);
        }
        System.err.println("changeProiorty --> " + actions);
    }

    private static List<GridSum> checkGrid(int ninePos, int[][] grid) {
        int startRow = ninePos / 3 * 3;
        int startCol = ninePos % 3 * 3;
        List<GridSum> gridSumList = new ArrayList<>();
        for (int i = 0; i < 3; i ++){
            ArrayList<ProiortyAction> rowAction = new ArrayList<>();
            int rowSum = grid[startRow][startCol] + grid[startRow][startCol + 1] + grid[startRow][startCol + 2];
            if (grid[startRow][startCol] == G_ENTMP){
                rowAction.add(new ProiortyAction(startRow, startCol));
            }
            if (grid[startRow][startCol + 1] == G_ENTMP){
                rowAction.add(new ProiortyAction(startRow, startCol + 1));
            }
            if (grid[startRow][startCol + 2] == G_ENTMP){
                rowAction.add(new ProiortyAction(startRow, startCol + 2));
            }
            gridSumList.add(new GridSum(rowSum, rowAction));

            ArrayList<ProiortyAction> colAction = new ArrayList<>();
            int colSum = grid[startRow][startCol] + grid[startRow + 1][startCol] + grid[startRow + 2][startCol];
            if (grid[startRow][startCol] == G_ENTMP){
                colAction.add(new ProiortyAction(startRow, startCol));
            }
            if (grid[startRow + 1][startCol] == G_ENTMP){
                colAction.add(new ProiortyAction(startRow + 1, startCol));
            }
            if (grid[startRow + 2][startCol] == G_ENTMP){
                colAction.add(new ProiortyAction(startRow + 2, startCol));
            }
            gridSumList.add(new GridSum(colSum, colAction));
        }
        int rake1Sum = grid[startRow][startCol] + grid[startRow + 1][startCol + 1] + grid[startRow + 2][startCol + 2];
        ArrayList<ProiortyAction> rake1Action = new ArrayList<>();
        if (grid[startRow][startCol] == G_ENTMP){
            rake1Action.add(new ProiortyAction(startRow, startCol));
        }
        if (grid[startRow + 1][startCol + 1] == G_ENTMP){
            rake1Action.add(new ProiortyAction(startRow + 1, startCol + 1));
        }
        if (grid[startRow + 2][startCol + 2] == G_ENTMP){
            rake1Action.add(new ProiortyAction(startRow + 2, startCol + 2));
        }
        gridSumList.add(new GridSum(rake1Sum, rake1Action));

        int rake2Sum = grid[startRow + 2][startCol] + grid[startRow + 1][startCol + 1] + grid[startRow][startCol + 2];
        ArrayList<ProiortyAction> rake2Action = new ArrayList<>();
        if (grid[startRow + 2][startCol] == G_ENTMP){
            rake2Action.add(new ProiortyAction(startRow + 2, startCol));
        }
        if (grid[startRow + 1][startCol + 1] == G_ENTMP){
            rake2Action.add(new ProiortyAction(startRow + 1, startCol + 1));
        }
        if (grid[startRow][startCol + 2] == G_ENTMP){
            rake2Action.add(new ProiortyAction(startRow, startCol + 2));
        }
        gridSumList.add(new GridSum(rake2Sum, rake2Action));
        return gridSumList;
    }

    private static void addStepPriority(ArrayList<ProiortyAction> actions, ArrayList<ProiortyAction> emtryActions, int step) {
        for (ProiortyAction action : emtryActions){
            action.addPriority(actions, step);
        }
    }

    private static void addLosePriority(ArrayList<ProiortyAction> actions, List<ProiortyAction> emtryActions) {
        for (ProiortyAction action : emtryActions){
            action.addPriority(actions, P_LOSE[action.nicePosition]);
        }
    }

    private static void addWinPriority(ArrayList<ProiortyAction> actions, List<ProiortyAction> emtryActions) {
        for (ProiortyAction action : emtryActions){
            action.addPriority(actions, P_WIN[action.nicePosition]);
        }
    }

}