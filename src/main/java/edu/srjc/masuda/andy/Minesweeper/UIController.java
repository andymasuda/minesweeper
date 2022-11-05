/*
Name: Andy Masuda
Email: andyxmasuda@gmail.com
Date: 12/9/2021
Project Name: Final Project - Minesweeper
Course: CS 17.11
Description: This program runs the game Minesweeper. In this version,
    there are three difficulties: Beginner, Intermediate, and Advanced.
    Left-clicking reveals tiles and right-clicking places/removes flags.
    The game ends if a mine is revealed or if every non-mine is revealed.
    The first tile clicked will always be a blank tile. Left-clicking on
    a numbered tile will reveal all tiles around it, but only if there
    is the correct number of flags around it. If there are too many flags
    around a numbered tile, the numbered tile will turn red. Click the
    "new game" button to reset the board. Clicking in the "difficulty"
    menu will also reset the board. The label under the board displays a
    timer that begins after the first tile is opened and ends when the
    game ends. It also displays the amount of flags placed and the amount
    of bombs on the board. Clicking the "solve game" button implements a
    single point algorithm(which is unable to solve every solvable board,
    but can detect any 50/50s), which attempts to solve the board and outputs
    a new board with relocated mines in the console(if the algorithm is
    unable to solve it) as well as the result of the algorithm. Unfortunately,
    I was unable to figure out how to implement the features so that after
    the first click, the program solves the board, relocates the mines,
    and continues the board.
 */
package edu.srjc.masuda.andy.Minesweeper;

import edu.srjc.masuda.andy.Minesweeper.Alerts.Alerts;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.List;

public class UIController implements Initializable
{
    private static final int TILE_SIZE = 35;
    private static int ROWS = 9;
    private static int COLS = 9;
    private static int WIDTH = TILE_SIZE * COLS;
    private static int HEIGHT = TILE_SIZE * ROWS;
    private static int NUM_MINES = 10;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label titleLabel;

    @FXML
    private Pane paneGame;

    @FXML
    private Label timerLabel;

    @FXML
    private Label gameStatus;

    @FXML
    private Button newGameButton;

    @FXML
    private Button solveGameButton;

    @FXML
    private MenuButton difficultyMenuButton;

    @FXML
    protected void onBeginnerMenuButtonClick()
    {
        ROWS = 9;
        COLS = 9;
        WIDTH = TILE_SIZE * COLS;
        HEIGHT = TILE_SIZE * ROWS;
        NUM_MINES = 10;
        initialize(null, null);
    }

    @FXML
    protected void onIntermediateMenuButtonClick()
    {
        ROWS = 16;
        COLS = 16;
        WIDTH = TILE_SIZE * COLS;
        HEIGHT = TILE_SIZE * ROWS;
        NUM_MINES = 40;
        initialize(null, null);
    }

    @FXML
    protected void onExpertMenuButtonClick()
    {
        ROWS = 16;
        COLS = 30;
        WIDTH = TILE_SIZE * COLS;
        HEIGHT = TILE_SIZE * ROWS;
        NUM_MINES = 99;
        initialize(null, null);
    }

    @FXML
    protected void onNewGameButtonClick()
    {
        initialize(null, null);
    }

    @FXML
    protected void onSolveGameButtonClick()
    {
        if (!firstMove)
        {
            if (solveGame(gridTile))
            {
                System.out.println("Solved");
            }
            else
            {
                moveMines();
                System.out.println("Not Solved");
            }
        }
        else
        {
            Alerts.showAlert("Unable to solve game", "The game has not been started yet. " +
                    "Click on a tile to generate a board.", Alert.AlertType.INFORMATION);
        }
    }

    private static Tile[][] gridTile;
    private final Image flagImg = new Image(Objects.requireNonNull(getClass().getResource("/images/flag.png")).toString());
    private final Image bombImg = new Image(Objects.requireNonNull(getClass().getResource("/images/bomb.png")).toString());
    private ImageView[][] flagIv;
    boolean[][] mineList;
    private int flagCount;
    public static int unOpenedTileCount;
    private boolean firstMove;
    public static boolean gameOver;
    private static final int[][] offsets = {{-1, -1}, {0, -1}, {1, -1}, {-1, 0}, {1, 0}, {-1, 1}, {0, 1}, {1, 1}};
    private Timeline timeline;
    private int seconds;
    private int minutes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        gridTile = new Tile[ROWS][COLS];
        flagIv = new ImageView[ROWS][COLS];
        flagCount = 0;
        gameOver = false;
        firstMove = true;
        unOpenedTileCount = ROWS * COLS;
        seconds = 0;
        minutes = 0;

        anchorPane.setMinHeight(HEIGHT + 100);
        anchorPane.setMinWidth(WIDTH + 100);
        titleLabel.setTranslateX((double)(WIDTH / 2) - 25);
        paneGame.getChildren().clear();
        paneGame.setPrefSize(WIDTH, HEIGHT);
        newGameButton.setTranslateX((double)(WIDTH / 2) + 12);
        difficultyMenuButton.setTranslateX((double)(WIDTH / 2) - 104);
        solveGameButton.setTranslateX((double)(WIDTH / 2) + 126);
        gameStatus.setLayoutY(HEIGHT + 75);
        gameStatus.setText(String.format("Marked: %s / %s", flagCount, NUM_MINES));
        timerLabel.setLayoutY(HEIGHT + 75);
        timerLabel.setText("[00:00]");

        if (timeline != null)
        {
            timeline.stop();
        }
        for (int row = 0; row < ROWS; row++)
        {
            for (int col = 0; col < COLS; col++)
            {
                Tile tile = new Tile(row, col, false);
                gridTile[row][col] = tile;
                tile.setOnMouseClicked(this::tile_Click);
                paneGame.getChildren().add(tile);
            }
        }
    }

    private void setFlag(Tile tile, int row1, int col1)
    {
        tile.setFlagged(true);
        setImageView(flagImg, row1, col1);
        flagIv[row1][col1].setId(String.format("%s_%s", row1, col1));
        flagIv[row1][col1].setOnMouseClicked(this::flag_Click);
        paneGame.getChildren().add(flagIv[row1][col1]);
        flagCount++;
    }

    private void tile_Click(MouseEvent mouseEvent)
    {
        if (!gameOver)
        {
            Tile clickedTile = (Tile) mouseEvent.getSource();
            int row = clickedTile.getRow();
            int col = clickedTile.getCol();
            //Set flags
            if (!clickedTile.isRevealed() && mouseEvent.getButton() == MouseButton.SECONDARY)
            {
                if (!clickedTile.isFlagged())
                {
                    setFlag(clickedTile, row, col);
                    //Display red if number of surrounding flags do not match number of surrounding mines
                    for (Tile tile : getNeighbors(clickedTile))
                    {
                        if (tile.isRevealed())
                        {
                            long mines = getNeighbors(tile).stream().filter(Tile::hasMine).count();

                            if (mines < numFlagNeighbors(tile))
                            {
                                tile.setTileFill(Color.RED, 0.3);
                            }
                        }
                    }
                }
                gameStatus.setText(String.format("Marked: %s / %s", flagCount, NUM_MINES));
            }

            if (mouseEvent.getButton() == MouseButton.PRIMARY && !clickedTile.isFlagged())
            {
                //Configure mine layout after first click
                if (firstMove)
                {
                    timer();
                    paneGame.getChildren().clear();
                    mineList = new boolean[ROWS][COLS];

                    int mineCount = 0;

                    while (mineCount < NUM_MINES)
                    {
                        int randomRow = (int) Math.floor(Math.random() * ROWS);
                        int randomCol = (int) Math.floor(Math.random() * COLS);
                        //Do not place mine if around starting tile or if mine is already there
                        if ((Math.abs(row - randomRow) > 1 || Math.abs(col - randomCol) > 1) && !mineList[randomRow][randomCol])
                        {
                            mineList[randomRow][randomCol] = true;
                            mineCount++;

                            if (randomRow == row && randomCol == col)
                            {
                                mineList[row][col] = false;

                                for (int i = 0; i < COLS; i++)
                                {
                                    if (!mineList[0][i] && i != col)
                                    {
                                        mineList[0][i] = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //Add tiles to grid and board
                    for (int i = 0; i < ROWS; i++)
                    {
                        for (int j = 0; j < COLS; j++)
                        {
                            Tile tile = new Tile(i, j, mineList[i][j]);
                            gridTile[i][j] = tile;
                            tile.setOnMouseClicked(this::tile_Click);
                            paneGame.getChildren().add(tile);
                        }
                    }
                    //Assign number of mines around tile
                    for (int i = 0; i < ROWS; i++)
                    {
                        for (int j = 0; j < COLS; j++)
                        {
                            long mines = getNeighbors(gridTile[i][j]).stream().filter(Tile::hasMine).count();

                            if (mines > 0 && !gridTile[i][j].hasMine())
                            {
                                gridTile[i][j].setText(String.valueOf(mines));
                                gridTile[i][j].setEmpty(false);
                            }
                        }
                    }
                    firstMove = false;
                    clickedTile = gridTile[row][col];
                }
                if (!clickedTile.isRevealed())
                {
                    clickedTile.openTile();
                }
                //Reveal surrounding tiles of revealed tile if number of flags around it matches the number of mines around it
                else
                {
                    long flags = getNeighbors(clickedTile).stream().filter(Tile::isFlagged).count();
                    long mines = getNeighbors(clickedTile).stream().filter(Tile::hasMine).count();

                    if (flags == mines)
                    {
                        for (Tile tile : getNeighbors(clickedTile))
                        {
                            if (!tile.isFlagged())
                            {
                                tile.openTile();
                            }
                        }
                    }
                }
                if (gameOver)
                {
                    gameStatus.setText("GAME OVER");
                    flashResult("red");

                    for (int i = 0; i < ROWS; i++)
                    {
                        for (int j = 0; j < COLS; j++)
                        {
                            if (gridTile[i][j].hasMine())
                            {
                                setImageView(bombImg, i, j);
                                paneGame.getChildren().add(flagIv[i][j]);
                            }
                        }
                    }
                }
                if (!gameOver && unOpenedTileCount == NUM_MINES)
                {
                    gameStatus.setText("COMPLETED!");
                    flashResult("#ade8f4");
                    //Display flags on remaining mines
                    for (int i = 0; i < ROWS; i++)
                    {
                        for (int j = 0; j < COLS; j++)
                        {
                            if (gridTile[i][j].hasMine() && !gridTile[i][j].isFlagged())
                            {
                                gridTile[i][j].setFlagged(true);
                                setImageView(flagImg, i, j);
                                paneGame.getChildren().add(flagIv[i][j]);
                            }
                        }
                    }
                    gameOver = true;
                }
            }
        }
    }

    public static List<Tile> getNeighbors(Tile tile)
    {
        List<Tile> neighbors = new ArrayList<>();

        for (int[] offset : offsets)
        {
            int newRow = tile.getRow() + offset[0];
            int newCol = tile.getCol() + offset[1];

            if ((newRow >= 0 && newRow < ROWS) && (newCol >= 0 && newCol < COLS))
            {
                neighbors.add(gridTile[newRow][newCol]);
            }
        }
        return neighbors;
    }

    private void setImageView(Image image, int row, int col)
    {
        flagIv[row][col] = new ImageView();
        flagIv[row][col].setImage(image);
        flagIv[row][col].setFitHeight(TILE_SIZE);
        flagIv[row][col].setFitWidth(TILE_SIZE);
        flagIv[row][col].setTranslateX(col * TILE_SIZE);
        flagIv[row][col].setTranslateY(row * TILE_SIZE);
    }

    private void flag_Click(MouseEvent mouseEvent)
    {
        if (mouseEvent.getButton() == MouseButton.SECONDARY)
        {
            ImageView iv = (ImageView) mouseEvent.getSource();
            int row = Integer.parseInt(iv.getId().split("_")[0]);
            int col = Integer.parseInt(iv.getId().split("_")[1]);

            gridTile[row][col].setFlagged(false);
            paneGame.getChildren().remove(flagIv[row][col]);
            flagCount--;
            gameStatus.setText(String.format("Marked: %s / %s", flagCount, NUM_MINES));

            for (Tile tile : getNeighbors(gridTile[row][col]))
            {
                if (tile.isRevealed())
                {
                    long mines = getNeighbors(tile).stream().filter(Tile::hasMine).count();

                    if (mines >= numFlagNeighbors(tile))
                    {
                        tile.setTileFill(null, 1);
                    }
                }
            }
        }
    }

    private int numFlagNeighbors(Tile tile)
    {
        int flagNeighborCount = 0;

        for (int[] offset : offsets)
        {
            int newRow = tile.getRow() + offset[0];
            int newCol = tile.getCol() + offset[1];

            if ((newRow >= 0 && newRow < ROWS) && (newCol >= 0 && newCol < COLS))
            {
                if (gridTile[newRow][newCol].isFlagged())
                {
                    flagNeighborCount++;
                }
            }
        }
        return flagNeighborCount;
    }

    private void timer()
    {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            if (!gameOver)
            {
                if (seconds < 59)
                {
                    seconds++;
                }
                else
                {
                    minutes++;
                    seconds = 0;
                }
                timerLabel.setText("[" + (((minutes/10) == 0) ? "0" : "") + minutes + ":"
                        + (((seconds/10) == 0) ? "0" : "") + seconds + "]");
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void flashResult(String color)
    {
        paneGame.setStyle(String.format("-fx-background-color: %s", color));
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        paneGame.setStyle("-fx-background-color: #caf0f8");
                    }
                },
                300
        );
    }

    public static int getTileSize()
    {
        return TILE_SIZE;
    }

    private boolean solveGame(Tile[][] grid)
    {
        boolean active = true;
        while (active)
        {
            active = false;
            for (int row = 0; row < ROWS; row++)
            {
                for (int col = 0; col < COLS; col++)
                {
                    long numMineNeighbors = getNeighbors(grid[row][col]).stream().filter(Tile::hasMine).count();
                    if (grid[row][col].isEmpty() && numUndecidedNeighbors(grid[row][col]) >= 0)
                    {
                        grid[row][col].openTile();
                    }
                    if (grid[row][col].isRevealed() && !grid[row][col].isEmpty()
                            && numMineNeighbors == numFlagNeighbors(grid[row][col])
                            && numUndecidedNeighbors(grid[row][col]) > 0)
                    {
                        for (Tile tile : getNeighbors(grid[row][col]))
                        {
                            if (!tile.isFlagged() && !tile.isRevealed())
                            {
                                tile.openTile();
                                active = true;
                            }
                        }
                    }
                    if (grid[row][col].isRevealed()
                            && (numUndecidedNeighbors(grid[row][col]) + numFlagNeighbors(grid[row][col])) == numMineNeighbors
                            && numUndecidedNeighbors(grid[row][col]) > 0)
                    {
                        for (Tile tile : getNeighbors(grid[row][col]))
                        {
                            if (!tile.isRevealed() && !tile.isFlagged())
                            {
                                setFlag(tile, tile.getRow(), tile.getCol());
                                active = true;
                            }
                        }
                    }
                }
            }
        }
        for (int row = 0; row < ROWS; row++)
        {
            for (int col = 0; col < COLS; col++)
            {
                if (grid[row][col].hasMine()
                        && !grid[row][col].isFlagged()
                        && numUndecidedNeighbors(grid[row][col]) > 0)
                {
                    return false;
                }
            }
        }
        return true;
    }

    private void moveMines()
    {
        for (int row = 0; row < ROWS; row++)
        {
            for (int col = 0; col < COLS; col++)
            {
                if (mineList[row][col]
                        && !gridTile[row][col].isFlagged()
                        && numUndecidedNeighbors(gridTile[row][col]) > 0)
                {
                    mineList[row][col] = false;

                    int randomRow = (int) Math.floor(Math.random() * ROWS);
                    int randomCol = (int) Math.floor(Math.random() * COLS);

                    if (!mineList[randomRow][randomCol])
                    {
                        mineList[randomRow][randomCol] = true;
                    }
                    else
                    {
                        while (true)
                        {
                            int newRandomRow = (int) Math.floor(Math.random() * ROWS);
                            int newRandomCol = (int) Math.floor(Math.random() * COLS);
                            if (!mineList[newRandomRow][newRandomCol])
                            {
                                mineList[newRandomRow][newRandomCol] = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        for (int row = 0; row < ROWS; row++)
        {
            for (int col = 0; col < COLS; col++)
            {
                if (mineList[row][col])
                {
                    System.out.print(1 + " ");
                } else
                {
                    System.out.print(0 + " ");
                }
            }
            System.out.println();
        }
    }

    private int numUndecidedNeighbors(Tile tile)
    {
        int undecidedNeighborCount = 0;

        for (int[] offset : offsets)
        {
            int newRow = tile.getRow() + offset[0];
            int newCol = tile.getCol() + offset[1];

            if ((newRow >= 0 && newRow < ROWS) && (newCol >= 0 && newCol < COLS))
            {
                if (!gridTile[newRow][newCol].isRevealed() && !gridTile[newRow][newCol].isFlagged())
                {
                    undecidedNeighborCount++;
                }
            }
        }
        return undecidedNeighborCount;
    }
}