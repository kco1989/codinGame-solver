import java.util.*;

public class Player1 {
    public static final int p_play_and_win = 100;      // 下子就赢了
    public static final int p_play_and_not_lose = 60;  // 下子就不会输
    public static final int p_best_point = 40;         // 在没有赢或者输的情况,最好的位置
    public static final int p_better_point = 20;       // 在没有赢或者输的情况,较好的位置
    public static final int p_good_point = 0;         // 在没有赢或者输的情况,好的位置
    public static final int p_bed_point = -100;         // 在没有赢或者输的情况,好的位置
    public static final int p_step = 1;                 // 在没有赢或者输的情况,好的位置

    public static final int g_play = 1;
    public static final int g_opponent = 2;

    static class ProiortyAction implements Comparable<ProiortyAction> {
        int row = -1, col = -1;
        int ninePos;
        int priority;
        String message;

        public ProiortyAction(){
            this(0, -1, -1);
        }
        public ProiortyAction(int row, int col) {
            this(row / 3 * 3 + col / 3, row % 3, col % 3);
        }
        public ProiortyAction(int ninePos,int row, int col) {
            this.ninePos = ninePos;
            this.row = row ;
            this.col = col ;
            if (this.row == 1 && this.col == 1){
                this.priority = p_best_point;
            }else if(this.row < 0 && this.row < 0){
                this.priority = p_bed_point;
            }else if((this.row + this.col) % 2 == 0){
                this.priority = p_better_point;
            }else {
                this.priority = p_good_point;
            }
            this.message = "Hello boby!";
        }
        public void addPriority(PriorityQueue queue, int priority){
            if (queue.contains(this)){
                queue.remove(this);
                this.priority = this.priority - priority;
                queue.add(this);
            }
        }
        public void setPriority(PriorityQueue queue, int priority) {
            String message = this.message;
            switch (priority){
                case p_play_and_win: this.message = "Yeas, I'm win!"; break;
                case p_play_and_not_lose:this.message = "I'm so sorry";break;
            }
            if (queue.contains(this)){
                queue.remove(this);
                this.priority = priority;
                queue.add(this);
            }
        }

        public boolean isValid (){
            return this.row != -1 && this.col != -1;
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
            int r = this.ninePos / 3;
            int c = this.ninePos % 3;
            return String.format("%d %d %s", this.row + r * 3, this.col + c * 3, this.message);
        }

    }

    static PriorityQueue<ProiortyAction> initPriorityQueue(int k){
        PriorityQueue<ProiortyAction> queue = new PriorityQueue<>();
        for (int i = 0; i < 3; i ++){
            for (int j = 0; j < 3; j ++){
                if (i == j && i == 1){
                    queue.add(new ProiortyAction(k, i, j));
                }else if((i + j) % 2 == 0){
                    queue.add(new ProiortyAction(k, i, j));
                }else {
                    queue.add(new ProiortyAction(k, i, j));
                }
            }
        }
        return queue;
    }
    static boolean isLevelOne = System.getProperty("league.level", "1").equals("1");

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Random random = new Random(0);
        int[][] grid1 = new int[3][3];           // Level1 的棋面, level2 的大棋面
        List<int[][]> grid2 = new ArrayList<>();           // level2 的棋面
        PriorityQueue<ProiortyAction> queue1;        // level1 棋面各个位置的优先队列, Level2 大棋面各位置的优先队列
        List<PriorityQueue<ProiortyAction>> queue2 = new ArrayList<>();      // level2 棋面个位置的优先队列
        queue1 = initPriorityQueue(0);
        for (int k = 0; k < 9; k ++){
            queue2.add(initPriorityQueue(k));
            grid2.add(new int[3][3]);
        }

        while (true) {
            int opponentRow = in.nextInt();
            int opponentCol = in.nextInt();
            ProiortyAction opponentAction = new ProiortyAction(opponentRow, opponentCol);
            int validActionCount = in.nextInt();

            List<ProiortyAction> actions = new ArrayList<>(validActionCount);
            for (int i = 0; i < validActionCount; i++) {
                int row = in.nextInt();
                int col = in.nextInt();
                actions.add(new ProiortyAction(row, col));
            }

            if (isLevelOne){
                doLevelOne(opponentAction, actions, queue1, grid1, 0, null);
            }else{

                if (grid2.get(4)[1][1] == 0){
                    System.err.println("81 81 81 81");
                    ProiortyAction action = new ProiortyAction(4, 1, 1);
                    grid2.get(4)[1][1] = g_play;
                    queue2.get(4).remove(action);
                    System.out.println(action);
                    continue;
                }
                doLevelTwo(opponentAction, actions, queue2, grid2);
            }
        }
    }

    private static void doLevelTwo(ProiortyAction opponentAction, List<ProiortyAction> actions,
                                   List<PriorityQueue<ProiortyAction>> queue2, List<int[][]> grid2) {
        ProiortyAction action = actions.get(0);

        if (opponentAction.isValid()){
            grid2.get(opponentAction.ninePos)[opponentAction.row][opponentAction.col] = g_opponent;
            System.err.println("opponentAction" + opponentAction.ninePos + ", " + opponentAction.row + ", " + opponentAction.col);
            queue2.get(opponentAction.ninePos).remove(opponentAction);
        }
        doLevelOne(new ProiortyAction(), actions, queue2.get(action.ninePos), grid2.get(action.ninePos), action.ninePos, grid2);
    }

    private static void doLevelOne(ProiortyAction opponentAction, List<ProiortyAction> actions,
                                   PriorityQueue<ProiortyAction> queue, int[][] grid, int position,
                                   List<int[][]> grid2) {
        System.err.println("one position " + position);
        if (opponentAction.isValid()){
            grid[opponentAction.row][opponentAction.col] = g_opponent;
            queue.remove(opponentAction);
        }
        checkRowAndCol(grid, queue, position);
        checkOblique(grid, queue, new ProiortyAction(position,0, 0), new ProiortyAction(position,1, 1), new ProiortyAction(position,2, 2));
        checkOblique(grid, queue, new ProiortyAction(position,0, 2), new ProiortyAction(position,1, 1), new ProiortyAction(position,2, 0));
        ProiortyAction poll = queue.poll();
        System.out.println(poll);
        if (grid2 != null){
            grid2.get(poll.ninePos)[poll.row][poll.col] = g_play;
        }else{
            grid[poll.row][poll.col] = g_play;
        }

    }

    private static void checkOblique(int[][] grid, PriorityQueue<ProiortyAction> queue,
                                     ProiortyAction action1, ProiortyAction action2, ProiortyAction action3) {
        int playCount = 0;
        int opponentCount = 0;
        ProiortyAction action = new ProiortyAction();
        if (grid[action1.row][action1.col] == g_play){
            playCount ++;
        }else if(grid[action1.row][action1.col] == g_opponent){
            opponentCount ++;
        }else {
            action = action1;
        }

        if (grid[action2.row][action2.col] == g_play){
            playCount ++;
        }else if(grid[action2.row][action2.col] == g_opponent){
            opponentCount ++;
        }else {
            action = action2;
        }

        if (grid[action3.row][action3.col] == g_play){
            playCount ++;
        }else if(grid[action3.row][action3.col] == g_opponent){
            opponentCount ++;
        }else {
            action = action3;
        }

        if (playCount == 2 && action.isValid()){
            action.setPriority(queue, p_play_and_win);
            return;
        }
        if (opponentCount == 2 && action.isValid()){
            action.setPriority(queue, p_play_and_not_lose);
            return;
        }
    }

    private static void checkRowAndCol(int[][] grid, PriorityQueue<ProiortyAction> queue, int position) {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < 3; x ++){
            int rowPlayCount = 0;
            ProiortyAction rowAction = new ProiortyAction();
            int rowOpponentCount = 0;

            int colPlayCount = 0;
            ProiortyAction colAction = new ProiortyAction();
            int colOpponentCount = 0;
            for (int y = 0; y < 3; y++){
                sb.append(grid[x][y] + " ");
                if (grid[x][y] == g_play){
                    rowPlayCount ++;
                }else if(grid[x][y] == g_opponent){
                    rowOpponentCount ++;
                }else {
                    rowAction = new ProiortyAction(position, x, y);
                }

                if (grid[y][x] == g_play){
                    colPlayCount ++;
                }else if(grid[y][x] == g_opponent){
                    colOpponentCount ++;
                }else {
                    colAction = new ProiortyAction(position, y, x);
                }
            }
            sb.append("\n");
            System.err.println(x +  " rowPlayCount -> " + rowPlayCount + " colPlayCount -> " + colPlayCount);
            System.err.println(" rowOpponentCount -> " + rowOpponentCount + " colOpponentCount -> " + colOpponentCount);
            if (rowPlayCount == 2 && rowAction.isValid()){
                rowAction.setPriority(queue, p_play_and_win);
//                return;
            }
            if (colPlayCount == 2 && colAction.isValid()){
                colAction.setPriority(queue, p_play_and_win);
//                return;
            }
            if (rowOpponentCount == 2 && rowAction.isValid()){
                rowAction.setPriority(queue, p_play_and_not_lose);
//                return;
            }
            if (colOpponentCount == 2 && colAction.isValid()){
                colAction.setPriority(queue, p_play_and_not_lose);
//                return;
            }

            if (rowPlayCount == 1 && rowOpponentCount == 0 && rowAction.isValid()){
                rowAction.addPriority(queue, p_step * 2);
//                return;
            }
            if (colPlayCount == 1 && colOpponentCount == 0  && colAction.isValid()){
                colAction.addPriority(queue, p_step * 2);
//                return;
            }

            if (rowOpponentCount == 1 && rowPlayCount == 0 && rowAction.isValid()){
                rowAction.addPriority(queue, p_step);
//                return;
            }else if(rowOpponentCount == 1 && rowPlayCount != 0 && rowAction.isValid()){
                rowAction.addPriority(queue, - p_step * 2);
            }
            if (colOpponentCount == 1 && colPlayCount == 0 && colAction.isValid()){
                colAction.addPriority(queue, - p_step );
//                return;
            }else if(colOpponentCount == 1 && colPlayCount != 0 && colAction.isValid()){
                colAction.addPriority(queue, - p_step * 2);
            }
        }
        System.err.println(position + " --> " + sb.toString());
    }
}
