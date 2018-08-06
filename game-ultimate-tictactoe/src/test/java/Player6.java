import java.util.*;
import java.util.stream.Collectors;

class Player6 {

    public static final int P_ALL_WIN = 100000;         // 全局赢了
    public static final int P_ALL_LOST = 50000;         // 全局要输了

    public static final int P_WIN = 10000;               // 会赢
    public static final int P_LOSE = 5000;               // 将要输


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

    // 权限分组
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

    // 动作
    static class PriorityAction implements Comparable<PriorityAction> {
        private int row, col;               // 行列
        private int niceRow, niceCol;
        private int gridPosition, position;
        private int nineNumber;
        private int priority;

        public static Map<String, PriorityAction> newPool(){
            return new HashMap<>();
        }

        public static PriorityAction getPriorityAction(Map<String, PriorityAction> actionPool, int row, int col){
            String key = String.format("(%d, %d)", row, col);
            return actionPool.computeIfAbsent(key, k -> new PriorityAction(row, col));
        }

        private PriorityAction(int row, int col) {
            this.nineNumber = row / 3 * 3 + col / 3;
            this.niceRow = this.nineNumber / 3;
            this.niceCol = this.nineNumber % 3;
            this.row = row;
            this.col = col;
            if (this.niceRow == this.niceCol && this.niceRow == 1){
                this.gridPosition = POS_CENTER;
            }else if((this.niceRow + this.niceCol) % 2 == 0){
                this.gridPosition = POS_ANGLE;
            }else {
                this.gridPosition = POS_SIDE;
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
            this.priority = PROIORTY[this.gridPosition][this.position];
        }

        public void addPriority(int priority){
            this.priority += priority;
        }

        public void addWinOrLosePriority(int priority){
            if (priority > 0 && this.priority < priority){
                this.priority += priority;
            }else if(priority < 0 && this.priority > -priority){
                this.priority += priority;
            }
        }

        @Override
        public int compareTo(PriorityAction o) {
            return o.priority - this.priority;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PriorityAction action = (PriorityAction) o;

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

    static class GameInfo{
        int[][] ticTacToe = new int[9][9];
        int[][] ticTacToeGrid = new int[3][3];
        ArrayList<PriorityAction> allValidAction = new ArrayList<>();
        Map<String, PriorityAction> poolAction = PriorityAction.newPool();
    }

    // 统计
    static class GridSum{
        int sum;
        ArrayList<PriorityAction> actions;
        public GridSum(int sum, ArrayList<PriorityAction> actions) {
            this.sum = sum;
            this.actions = actions;
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        GameInfo gameInfo = new GameInfo();

        while (true) {
            int opponentRow = in.nextInt();
            int opponentCol = in.nextInt();
            PriorityAction opponentAction = PriorityAction.getPriorityAction(gameInfo.poolAction, opponentRow, opponentCol);

            int validActionCount = in.nextInt();
            ArrayList<PriorityAction> currentValidAction = new ArrayList<>();
            for (int i = 0; i < validActionCount; i++) {
                int row = in.nextInt();
                int col = in.nextInt();
                PriorityAction priorityAction = PriorityAction.getPriorityAction(gameInfo.poolAction, row, col);
                if (! gameInfo.allValidAction.contains(priorityAction)){
                    gameInfo.allValidAction.add(priorityAction);
                }
                currentValidAction.add(priorityAction);
            }

            // 判断对手当前操作是否有效
            if (opponentAction.row != -1){
                gameInfo.ticTacToe[opponentAction.row][opponentAction.col] = G_OPPONENT;
                checkProiorty(opponentAction, gameInfo);
                checkWinner(opponentAction, false, gameInfo);
            }

            PriorityQueue<PriorityAction> queue = new PriorityQueue<>(currentValidAction);
            PriorityAction poll = queue.poll();
            gameInfo.allValidAction.remove(poll);
            System.out.println(poll);
            gameInfo.ticTacToe[poll.row][poll.col] = G_PLAY;
            checkProiorty(poll, gameInfo);
            checkWinner(poll, true, gameInfo);
            StringBuilder sb = new StringBuilder();
            gameInfo.allValidAction.stream().forEach(item -> {
                sb.append("( " + item.row + " , " + item.col + " ) = " + item.priority + "\n");
            });
            System.err.println(sb.toString());
        }
    }

    /**
     * 判断小格子上是否已经有胜利者出现
     * 只有level2级别才会使用这个函数
     */
    private static void checkWinner(PriorityAction action, boolean isPlay, GameInfo gameInfo) {
        int gValue = isPlay ? G_PLAY : G_OPPONENT;
        List<GridSum> gridSumList = checkGrid(action.nineNumber, gameInfo.ticTacToe, gameInfo);
        long count = gridSumList.stream().filter(item -> item.sum == gValue * 3).count();
        if (count == 0){
            return;
        }


        // 删除出现胜利者的其他还未使用的格子
        List<PriorityAction> collect = gameInfo.allValidAction.stream().filter(item -> item.nineNumber == action.nineNumber).collect(Collectors.toList());
        gameInfo.allValidAction.removeAll(collect);

        gameInfo.ticTacToeGrid[action.niceRow][action.niceCol] = gValue;
        List<GridSum> gridAllSumList = checkGrid(0, gameInfo.ticTacToeGrid, gameInfo);
        for (GridSum gridSum : gridAllSumList){
            if (gridSum.sum == 2 * G_PLAY){
                PriorityAction action1 = gridSum.actions.get(0);
                long winCount = checkGrid(action1.nineNumber, gameInfo.ticTacToe, gameInfo)
                        .stream()
                        .filter(item -> item.sum == 0 || item.sum == G_PLAY || item.sum == 2 * G_PLAY)
                        .count();
                if (winCount == 0){
                    gameInfo.allValidAction.stream()
                            .filter(item -> item.nineNumber == action1.nineNumber)
                            .forEach(item -> item.addWinOrLosePriority(-P_ALL_WIN));
                }else{
                    gameInfo.allValidAction.stream()
                            .filter(item -> item.nineNumber == action1.nineNumber)
                            .forEach(item -> item.addWinOrLosePriority(P_ALL_WIN));
                }
            }else if (gridSum.sum == 2 * G_OPPONENT){
                PriorityAction action2 = gridSum.actions.get(0);
                long loseCount = checkGrid(action2.nineNumber, gameInfo.ticTacToe, gameInfo).stream()
                        .filter(item -> item.sum == 0 || item.sum == G_OPPONENT || item.sum == 2 * G_OPPONENT)
                        .count();

                if (loseCount == 0){
                    gameInfo.allValidAction.stream()
                            .filter(item -> item.nineNumber == action2.nineNumber)
                            .forEach(item -> item.addWinOrLosePriority(- P_ALL_LOST));
                }else {
                    gameInfo.allValidAction.stream()
                            .filter(item -> item.nineNumber == action2.nineNumber)
                            .forEach(item -> item.addWinOrLosePriority(P_ALL_LOST));
                }
            }
        }
    }

    /**
     *  检查权限
     */
    private static void checkProiorty(PriorityAction action, GameInfo gameInfo) {
        // 统计当前操作格子棋面情况
        List<GridSum> gridSumList = checkGrid(action.nineNumber, gameInfo.ticTacToe, gameInfo);
        for (GridSum gridSum : gridSumList){
            changePriority(gridSum);
        }
    }

    private static void changePriority(GridSum gridSum) {
        if (gridSum.actions.isEmpty()){
            return;
        }
        if (gridSum.sum == G_PLAY * 2){
            gridSum.actions.forEach(item -> item.addWinOrLosePriority(P_WIN));
        }else if(gridSum.sum == G_OPPONENT * 2){
            gridSum.actions.forEach(item -> item.addWinOrLosePriority(P_LOSE));
        }else if (gridSum.sum == G_PLAY){
            gridSum.actions.forEach(item -> item.addPriority(P_STEP * 5));
        }else if (gridSum.sum == G_OPPONENT){
            gridSum.actions.forEach(item -> item.addPriority(P_STEP * 3));
        }else if (gridSum.sum == G_PLAY + G_OPPONENT){
            gridSum.actions.forEach(item -> item.addPriority(- P_STEP * 3));
        }
    }

    /**
     * 统计格子 横三竖三斜二的得分情况
     */
    private static List<GridSum> checkGrid(int nineNumber, int[][] grid, GameInfo gameInfo) {
        int startRow = nineNumber / 3 * 3;
        int startCol = nineNumber / 3 * 3;
        List<GridSum> gridSumList = new ArrayList<>();
        for (int i = 0; i < 3; i ++){
            ArrayList<PriorityAction> rowAction = new ArrayList<>();
            int rowSum = grid[startRow + i][startCol] + grid[startRow + i][startCol + 1] + grid[startRow + i][startCol + 2];
            if (grid[startRow + i][startCol] == G_ENTMP){
                rowAction.add(PriorityAction.getPriorityAction(gameInfo.poolAction, startRow + i, startCol));
            }
            if (grid[startRow + i][startCol + 1] == G_ENTMP){
                rowAction.add(PriorityAction.getPriorityAction(gameInfo.poolAction, startRow + i, startCol + 1));
            }
            if (grid[startRow + i][startCol + 2] == G_ENTMP){
                rowAction.add(PriorityAction.getPriorityAction(gameInfo.poolAction, startRow + i, startCol + 2));
            }
            gridSumList.add(new GridSum(rowSum, rowAction));

            ArrayList<PriorityAction> colAction = new ArrayList<>();
            int colSum = grid[startRow][startCol + i] + grid[startRow + 1][startCol + i] + grid[startRow + 2][startCol + i];
            if (grid[startRow][startCol + i] == G_ENTMP){
                colAction.add(PriorityAction.getPriorityAction(gameInfo.poolAction, startRow, startCol + i));
            }
            if (grid[startRow + 1][startCol + i] == G_ENTMP){
                colAction.add(PriorityAction.getPriorityAction(gameInfo.poolAction, startRow + 1, startCol + i));
            }
            if (grid[startRow + 2][startCol + i] == G_ENTMP){
                colAction.add(PriorityAction.getPriorityAction(gameInfo.poolAction, startRow + 2, startCol + i));
            }
            gridSumList.add(new GridSum(colSum, colAction));
        }

        int rake1Sum = grid[startRow][startCol] + grid[startRow + 1][startCol + 1] + grid[startRow + 2][startCol + 2];
        ArrayList<PriorityAction> rake1Action = new ArrayList<>();
        if (grid[startRow][startCol] == G_ENTMP){
            rake1Action.add(PriorityAction.getPriorityAction(gameInfo.poolAction, startRow, startCol));
        }
        if (grid[startRow + 1][startCol + 1] == G_ENTMP){
            rake1Action.add(PriorityAction.getPriorityAction(gameInfo.poolAction, startRow + 1, startCol + 1));
        }
        if (grid[startRow + 2][startCol + 2] == G_ENTMP){
            rake1Action.add(PriorityAction.getPriorityAction(gameInfo.poolAction, startRow + 2, startCol + 2));
        }
        gridSumList.add(new GridSum(rake1Sum, rake1Action));

        int rake2Sum = grid[startRow + 2][startCol] + grid[startRow + 1][startCol + 1] + grid[startRow][startCol + 2];
        ArrayList<PriorityAction> rake2Action = new ArrayList<>();
        if (grid[startRow + 2][startCol] == G_ENTMP){
            rake2Action.add(PriorityAction.getPriorityAction(gameInfo.poolAction, startRow + 2, startCol));
        }
        if (grid[startRow + 1][startCol + 1] == G_ENTMP){
            rake2Action.add(PriorityAction.getPriorityAction(gameInfo.poolAction, startRow + 1, startCol + 1));
        }
        if (grid[startRow][startCol + 2] == G_ENTMP){
            rake2Action.add(PriorityAction.getPriorityAction(gameInfo.poolAction, startRow, startCol + 2));
        }
        gridSumList.add(new GridSum(rake2Sum, rake2Action));
        return gridSumList;
    }
}