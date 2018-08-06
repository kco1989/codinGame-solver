import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Main {
    public static void main(String[] args) {
        
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

//        gameRunner.addAgent(BetterThanBossPlayer5.class);
        gameRunner.addAgent(Player6.class);
        gameRunner.addAgent(Player6.class);
        System.setProperty("league.level", "2");
        // gameRunner.addAgent("python3 /home/user/player.py");
        gameRunner.start();
    }
}
