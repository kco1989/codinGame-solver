package com.codingame.game;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.Curve;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Sprite;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {
    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;
    private int[][] grid = new int[3][3];

    private static final int CELL_SIZE = 250;
    private static final int LINE_WIDTH = 10;
    private static final int LINE_COLOR = 0xff0000;
    private static final int GRID_ORIGIN_Y = (int) Math.round(1080 / 2 - CELL_SIZE);
    private static final int GRID_ORIGIN_X = (int) Math.round(1920 / 2 - CELL_SIZE);

    @Override
    public void init() {
        // Display the background image. The asset image must be in the directory src/main/resources/view/assets
        graphicEntityModule.createSprite()
                .setImage("Background.jpg")
                .setAnchor(0);

        for (Player player : gameManager.getPlayers()) {
            player.sendInputLine(String.format("%d", player.getIndex() + 1));
            graphicEntityModule.createText(player.getNicknameToken())
                    .setX(180 + (player.getIndex() % 2) * 1400)
                    .setY(50 + 100 * (player.getIndex() / 2))
                    .setZIndex(20)
                    .setFontSize(90)
                    .setFillColor(player.getColorToken())
                    .setAnchor(0);

            graphicEntityModule.createSprite()
                    .setX(100 + (player.getIndex() % 2) * 1400)
                    .setY(90 + 100 * (player.getIndex() / 2))
                    .setZIndex(20)
                    .setImage(player.getAvatarToken())
                    .setAnchor(0.5);

        }

        gameManager.setFrameDuration(500);

        drawGrid();
    }

    private int convertX(double unit) {
        return (int) (GRID_ORIGIN_X + unit * CELL_SIZE);
    }

    private int convertY(double unit) {
        return (int) (GRID_ORIGIN_Y + unit * CELL_SIZE);
    }

    private void drawGrid() {
        double xs[] = new double[] { 0, 0, 1, 2 };
        double x2s[] = new double[] { 2, 2, 0, 1 };
        double ys[] = new double[] { 1, 2, 0, 0 };
        double y2s[] = new double[] { 0, 1, 2, 2 };

        for (int i = 0; i < 4; ++i) {
            graphicEntityModule.createLine()
                    .setX(convertX(xs[i] - 0.5))
                    .setX2(convertX(x2s[i] + 0.5))
                    .setY(convertY(ys[i] - 0.5))
                    .setY2(convertY(y2s[i] + 0.5))
                    .setLineWidth(LINE_WIDTH)
                    .setLineColor(LINE_COLOR);
        }

    }

    private void drawVictoryLine(int row1, int col1, int row2, int col2, Player winner) {
        graphicEntityModule.createLine()
                .setX(convertX(col1))
                .setY(convertY(row1))
                .setX2(convertX(col2))
                .setY2(convertY(row2))
                .setLineWidth(LINE_WIDTH)
                .setLineColor(winner.getColorToken())
                .setZIndex(30);
    }

    private int checkWinner() {
        for (int i = 0; i < 3; i++) {
            // check rows
            if (grid[i][0] > 0 && grid[i][0] == grid[i][1] && grid[i][0] == grid[i][2]) {
                drawVictoryLine(i, 0, i, 2, gameManager.getPlayer(grid[i][0] - 1));
                return grid[i][0];
            }

            // check cols
            if (grid[0][i] > 0 && grid[0][i] == grid[1][i] && grid[0][i] == grid[2][i]) {
                drawVictoryLine(0, i, 2, i, gameManager.getPlayer(grid[0][i] - 1));
                return grid[0][i];
            }
        }

        // check diags
        if (grid[0][0] > 0 && grid[0][0] == grid[1][1] && grid[0][0] == grid[2][2]) {
            drawVictoryLine(0, 0, 2, 2, gameManager.getPlayer(grid[0][0] - 1));
            return grid[0][0];
        }
        if (grid[2][0] > 0 && grid[2][0] == grid[1][1] && grid[2][0] == grid[0][2]) {
            drawVictoryLine(2, 0, 0, 2, gameManager.getPlayer(grid[1][1] - 1));
            return grid[2][0];
        }

        return 0;
    }

    @Override
    public void gameTurn(int turn) {
        Player player = gameManager.getPlayer(turn % gameManager.getPlayerCount());

        // Send inputs
        for (int l = 0; l < 3; l++) {
            player.sendInputLine(String.format("%d %d %d", grid[l][0], grid[l][1], grid[l][2]));
        }
        player.execute();

        // Read inputs
        try {
            String[] output = player.getOutputs().get(0).split(" ");
            int targetRow = Integer.parseInt(output[0]);
            int targetCol = Integer.parseInt(output[1]);

            if (targetRow < 0 || targetRow >= 3 || targetCol < 0 || targetCol >= 3 || grid[targetRow][targetCol] != 0) {
                player.deactivate("Invalid action.");
                player.setScore(-1);
                gameManager.endGame();
            } else {
                Sprite avatar = graphicEntityModule.createSprite()
                        .setX(convertX(targetCol))
                        .setY(convertY(targetRow))
                        .setImage(player.getAvatarToken())
                        .setAnchor(0.5);
                
                // Animate arrival
                avatar.setScale(0);
                graphicEntityModule.commitEntityState(0, avatar);
                avatar.setScale(1, Curve.ELASTIC);
                graphicEntityModule.commitEntityState(1, avatar);
                
            }

            gameManager.addToGameSummary(String.format("Player %s played (%d %d)", player.getNicknameToken(), targetRow, targetCol));

            // update grid
            grid[targetRow][targetCol] = player.getIndex() + 1;

        } catch (NumberFormatException e) {
            player.deactivate("Wrong output!");
            player.setScore(-1);
            gameManager.endGame();
        } catch (TimeoutException e) {
            gameManager.addToGameSummary(GameManager.formatErrorMessage(player.getNicknameToken() + " timeout!"));
            player.deactivate(player.getNicknameToken() + " timeout!");
            player.setScore(-1);
            gameManager.endGame();
        }

        // check winner
        int winner = checkWinner();
        if (winner > 0) {
            gameManager.addToGameSummary(GameManager.formatSuccessMessage(player.getNicknameToken() + " won!"));

            gameManager.getPlayer(winner - 1).setScore(1);
            gameManager.endGame();
        }
    }
}
