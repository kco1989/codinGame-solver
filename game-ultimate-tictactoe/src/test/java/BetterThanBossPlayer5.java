import java.util.*;

class BetterThanBossPlayer5 {

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

    public static final int G_PLAY = 1;
    public static final int G_OPPONENT = 2;

    static class ProiortyAction implements Comparable<ProiortyAction> {
        int row, col;
        int niceRow, niceCol;
        int nicePosition, position;
        int ninePos;
        int priority;
        String message;

        public ProiortyAction(){
            this(-1, -1);
        }
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

        public void addPriority(List<ProiortyAction> actions, int priority){
            ProiortyAction action = actions.stream().filter(item -> item.equals(this)).findFirst().orElse(null);
            if (action != null){
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

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Random random = new Random(0);
        Boolean isLevelOne = false;
        int[][] grid = new int[9][9];;

        while (true) {
            int opponentRow = in.nextInt();
            int opponentCol = in.nextInt();
            ProiortyAction opponentAction = new ProiortyAction(opponentRow, opponentCol);
            int validActionCount = in.nextInt();


            List<ProiortyAction> actions = new ArrayList<>();
            for (int i = 0; i < validActionCount; i++) {
                int row = in.nextInt();
                int col = in.nextInt();
                ProiortyAction proiortyAction = new ProiortyAction(row, col);
                actions.add(proiortyAction);
            }

            playGame(grid, actions, opponentAction);
        }
    }

    private static void playGame(int[][] grid, List<ProiortyAction> actions, ProiortyAction opponentAction) {
        if (opponentAction.row != -1){
            grid[opponentAction.row][opponentAction.col] = G_OPPONENT;
        }
        Set<Integer> positions = new HashSet<>();
        for (ProiortyAction action : actions){
            positions.add(action.ninePos);
        }
        System.err.println("positions positions positions --> " + positions + " size: " + actions.size());
        for (int postion : positions){
            checkRow(postion, grid, actions);
            checkLine(postion, grid, actions);
            checkRake(postion, grid, actions, 0, 0, 1, 1, 2, 2);
            checkRake(postion, grid, actions, 2, 0, 1, 1, 0, 2);
        }
        PriorityQueue<ProiortyAction> queue = new PriorityQueue<>(actions);
        ProiortyAction poll = queue.poll();
        System.out.println(poll);
        grid[poll.row][poll.col] = G_PLAY;
    }

    private static void checkLine(int postion, int[][] grid, List<ProiortyAction> actions) {
        int row = postion / 3;
        int col = postion % 3;
        for (int x = 0; x < 3; x++){
            int gCol = col * 3 + x;
            int playCount = 0;
            int oppCount = 0;
            List<ProiortyAction> emtryActions = new ArrayList<>();
            for (int y = 0; y < 3; y ++){
                int gRow = row * 3 + y;
                if (grid[gRow][gCol] == G_PLAY){
                    playCount ++;
                }else if(grid[gRow][gCol] == G_OPPONENT){
                    oppCount ++;
                }else{
                    emtryActions.add( new ProiortyAction(gRow, gCol));
                }
            }
            System.err.println("checkLine postion == " + postion);
            changeProiorty(playCount, oppCount, emtryActions, actions);
        }
    }

    private static void checkRake(int postion, int[][] grid, List<ProiortyAction> actions, int x1, int y1, int x2, int y2, int x3, int y3) {
        int row = postion / 3;
        int col = postion % 3;
        int playCount = 0;
        int oppCount = 0;
        List<ProiortyAction> emtryActions = new ArrayList<>();
        if (grid[row * 3 + x1][col * 3 + y1] == G_PLAY){
            playCount ++;
        }else if(grid[row * 3 + x1][col * 3 + y1] == G_OPPONENT){
            oppCount ++;
        }else{
            emtryActions.add(new ProiortyAction(row * 3 + x1, col * 3 + y1));
        }

        if (grid[row * 3 + x2][col * 3 + y2] == G_PLAY){
            playCount ++;
        }else if(grid[row * 3 + x2][col * 3 + y2] == G_OPPONENT){
            oppCount ++;
        }else{
            emtryActions.add(new ProiortyAction(row * 3 + x2, col * 3 + y2));
        }

        if (grid[row * 3 + x3][col * 3 + y3] == G_PLAY){
            playCount ++;
        }else if(grid[row * 3 + x3][col * 3 + y3] == G_OPPONENT){
            oppCount ++;
        }else{
            emtryActions.add(new ProiortyAction(row * 3 + x3, col * 3 + y3));
        }
        System.err.println("checkRake postion == " + postion);
        changeProiorty(playCount, oppCount, emtryActions, actions);

    }

    private static void changeProiorty(int playCount, int oppCount, List<ProiortyAction> emtryActions, List<ProiortyAction> actions) {

        if (!emtryActions.isEmpty()){
            if (playCount == 2){
                addWinPriority(actions, emtryActions);
            }else if(oppCount == 2){
                addLosePriority(actions, emtryActions);
            }else if (playCount == 1 && oppCount == 0){
                addStepPriority(actions, emtryActions, P_STEP * 5);
            }else if (playCount == 0 && oppCount == 1){
                addStepPriority(actions, emtryActions, P_STEP * 3);
            }else if (playCount == 0 && oppCount == 0){
                addStepPriority(actions, emtryActions, P_STEP);
            }else if (playCount == 1 && oppCount == 1){
                addStepPriority(actions, emtryActions, - P_STEP*5);
            }
        }
        System.err.println("playCount = " + playCount);
        System.err.println("oppCount = " + oppCount);
        System.err.println("emtryActions = " + emtryActions);
    }

    private static void checkRow(int postion, int[][] grid, List<ProiortyAction> actions) {
        int row = postion / 3;
        int col = postion % 3;
        for (int x = 0; x < 3; x++){
            int gRow = row * 3 + x;
            int playCount = 0;
            int oppCount = 0;
            List<ProiortyAction> emtryActions = new ArrayList<>();
            for (int y = 0; y < 3; y ++){
                int gCol = col * 3 + y;
                if (grid[gRow][gCol] == G_PLAY){
                    playCount ++;
                }else if(grid[gRow][gCol] == G_OPPONENT){
                    oppCount ++;
                }else{
                    emtryActions.add(new ProiortyAction(gRow, gCol));
                }
            }
            System.err.println("checkRow postion == " + postion);
            changeProiorty(playCount, oppCount, emtryActions, actions);
        }
    }

    private static void addStepPriority(List<ProiortyAction> actions, List<ProiortyAction> emtryActions, int step) {
        for (ProiortyAction action : emtryActions){
            action.addPriority(actions, step);
        }
    }

    private static void addLosePriority(List<ProiortyAction> actions, List<ProiortyAction> emtryActions) {
        for (ProiortyAction action : emtryActions){
            action.addPriority(actions, P_LOSE[action.nicePosition]);
        }
    }

    private static void addWinPriority(List<ProiortyAction> actions, List<ProiortyAction> emtryActions) {
        for (ProiortyAction action : emtryActions){
            action.addPriority(actions, P_WIN[action.nicePosition]);
        }
    }

    private static boolean setLevelOne(List<ProiortyAction> actions, int validActionCount, ProiortyAction opponentAction) {
        if (validActionCount > 9){
            return false;
        }
        if (opponentAction.ninePos > 0){
            return false;
        }
        for (ProiortyAction action : actions){
            if (action.ninePos > 0){
                return false;
            }
        }
        String property = System.getProperty("league.level");
        if (property != null && property.equals("2")){
            return false;
        }
        return true;
    }
}