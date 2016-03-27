package com.caseyweed.sudoku;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by casey on 2016-03-27.
 */
public class Sudoku extends Application {
    static Button[][] buttons = new Button[9][9];
    static ArrayList<String> easy = new ArrayList<>();
    static ArrayList<String> hard = new ArrayList<>();

    /**
     * Produces a 2D array of integers from a given string.
     *
     * @param line
     * @return 2D array of integers
     */
    public static int[][] inflate(String line) {
        int[][] grid = new int[9][9];
        int count = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid.length; col++) {
                int val = Integer.parseInt(String.valueOf(line.charAt(count++)));
                grid[row][col] = val;
            }
        }

        return grid;
    }

    /**
     * Identical to inflate, except instead of a 1D to 2D array we
     * consume the 2D array of Buttons to produce a 2D array of integers.
     *
     * @param buttons
     * @return
     */
    public static int[][] inflate(Button[][] buttons) {
        int[][] grid = new int[9][9];

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid.length; col++) {
                int val = Integer.parseInt(String.valueOf(buttons[col][row].getText()));
                grid[row][col] = val;
            }
        }

        return grid;
    }

    /**
     * Solves Sudoku puzzled with backtracking.
     *
     * @param i    row
     * @param j    col
     * @param grid
     * @return if the grid is solved
     */
    static boolean solve(int i, int j, int[][] grid) {
        // if >= 9 we've hit the end
        if (i == 9) {
            i = 0;
            if (++j == 9)
                return true;
        }

        // filled cell, skip
        if (grid[i][j] != 0)
            return solve(i + 1, j, grid);

        // attempt to fill cell
        for (int val = 1; val <= 9; ++val) {
            if (legal(i, j, val, grid)) {
                grid[i][j] = val;
                if (solve(i + 1, j, grid))
                    return true;
            }
        }
        // undo move
        grid[i][j] = 0;

        return false;
    }

    /**
     * Check if given value is legal to place at given cell.
     *
     * @param i    row
     * @param j    col
     * @param val  value to attempt
     * @param grid current grid
     * @return whether or not the value can be placed in the cell
     */
    static boolean legal(int i, int j, int val, int[][] grid) {
        // col check
        for (int k = 0; k < 9; ++k) {
            if (i == k) {
                continue;
            } else {
                if (val == grid[k][j])
                    return false;
            }
        }

        // row check
        for (int k = 0; k < 9; ++k) {
            if (j == k) {
                continue;
            } else {
                if (val == grid[i][k])
                    return false;
            }
        }

        // block check
        int rowOffset = (i / 3) * 3;
        int colOffset = (j / 3) * 3;
        for (int r = 0; r < 3; ++r) {
            for (int c = 0; c < 3; ++c) {
                if ((rowOffset + r) == i && (colOffset + c) == j) {
                    continue;
                } else {
                    if (val == grid[rowOffset + r][colOffset + c])
                        return false;
                }
            }
        }

        return true;
    }

    /**
     * Similar to the other legal, however, uses the current cell's value
     * and checks if the cell is already valid or not.
     *
     * @param i    row
     * @param j    col
     * @param grid
     * @return whether or no the given cell is legal as is
     */
    static boolean legal(int i, int j, int[][] grid) {
        int val = grid[i][j];
        return legal(i, j, val, grid);
    }

    /**
     * Will go to each cell and determine if it is filled or empty,
     * if filled will check if the cell is legal.
     *
     * @param grid
     * @return whether or not the grid can be solved
     */
    static boolean solvable(int[][] grid) {
        boolean isSolvable = false;
        boolean isEmpty = true;
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid.length; col++) {
                if (grid[row][col] != 0) {
                    isEmpty = false;
                    if (legal(row, col, grid)) {
                        isSolvable = true;
                        buttons[col][row].setStyle("-fx-text-fill: black;");
                    } else {
                        buttons[col][row].setStyle("-fx-text-fill: red;");
                        return false;
                    }
                }
            }
        }

        return isSolvable || isEmpty;
    }

    /**
     * Will attempt to solve the grid, if not will return false on unsolvable
     * boards.
     *
     * @return whether or not the grid was solved
     */
    public static boolean attempt() {
        int[][] grid = inflate(buttons);
        if (!solvable(grid)) {
            return false;
        }

        clear(); // clear entire board in case of error'd cells beforehand
        solve(0, 0, grid);
        setButtons(grid);
        return true;
    }

    /**
     * Clear button values and styles.
     */
    public static void clear() {
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons.length; j++) {
                buttons[i][j].setText("0");
                buttons[i][j].setStyle("-fx-text-fill: black;");
            }
        }
    }

    /**
     * Set button values to given grid values. <b>Note:</b> row/col is
     * swapped because JavaFX is idiotic.
     *
     * @param grid
     */
    public static void setButtons(int[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                buttons[j][i].setText("" + grid[i][j]);
            }
        }
    }

    /**
     * Increase value of given button's value 0-9 (zero being blank).
     *
     * @param i
     * @param j
     */
    public static void cycle(int i, int j) {
        int n = Integer.parseInt(String.valueOf(buttons[i][j].getText()));
        if (n == 9)
            n = 0;
        else
            n++;
        buttons[i][j].setText("" + n);
    }

    /**
     * Unfortunately deprecated as it is highly possible to create
     * random puzzles which cannot be solved. This is even more likely
     * on higher number of filled cells (see: easy and medium difficulties),
     * the harder difficulty with the least number of cells to fill
     * will typically find a solution, but it is likely to miss.
     *
     * @param n number of filled cells to create
     * @return random puzzle
     */
    public static int[][] generatePuzzle(int n) {
        int[][] grid = new int[9][9];
        if (n <= 0) return grid;

        Random rng = new Random();
        while (n > 0) {
            int row = rng.nextInt(9);
            int col = rng.nextInt(9);

            if (grid[row][col] != 0)
                continue;

            numbers:
            for (int k = 1; k <= 9; ++k) {
                if (legal(row, col, k, grid)) {
                    grid[row][col] = k;
                    n--;
                    break numbers;
                }
            }
        }

        return grid;
    }

    private static void generate(String difficulty) {
        int[][] grid;

        switch (difficulty) {
            case "Easy":
                grid = generatePuzzle(45);
                break;
            case "Medium":
                grid = generatePuzzle(35);
                break;
            case "Hard":
                grid = generatePuzzle(25);
                break;
            default:
                grid = generatePuzzle(35);
        }

        setButtons(grid);
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Load problem set from given resource name and add to list.
     *
     * @param rsc  resource name
     * @param list problem set list
     * @throws FileNotFoundException
     */
    private void loadFile(String rsc, ArrayList list) throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(rsc).getFile());

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                list.add(scanner.nextLine());
            }
        }
    }

    /**
     * Start loading of problem sets from resources.
     */
    private void load() {
        try {
            loadFile("easy", easy);
            loadFile("hard", hard);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load grid of appropriate difficulty.
     *
     * @param difficulty
     */
    public void pickPuzzle(String difficulty) {
        int[][] grid;
        String line = null;
        Random rng = new Random();

        switch (difficulty) {
            case "Easy/Medium":
                line = easy.get(rng.nextInt(easy.size()));
                break;
            case "Hard":
                line = hard.get(rng.nextInt(easy.size()));
                break;
            default:
        }

        grid = inflate(line);
        setButtons(grid);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // load solutions before starting UI
        load();

        // panes for all our junk
        GridPane grid = new GridPane(); // for buttons
        VBox vbox = new VBox(); // for everything
        HBox hbox = new HBox(); // for controls

        // make buttons
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons.length; j++) {
                // init
                buttons[i][j] = new Button("0");

                // set action
                final int x = i;
                final int y = j;
                buttons[i][j].setOnMouseClicked(event -> {
                    cycle(x, y);
                });

                // add to grid
                grid.add(buttons[i][j], i, j);
            }
        }

        // add grid to everything
        vbox.getChildren().add(grid);
        grid.setAlignment(Pos.CENTER);

        // add controls to everything
        Button solve = new Button("Solve");
        solve.setOnMouseClicked(event -> {
            if (!attempt())
                System.out.println("Cannot solve current board.");
        });
        Button clear = new Button("Clear");
        clear.setOnMouseClicked(event -> clear());
        ComboBox difficulty = new ComboBox();
        difficulty.getItems().addAll(
                "Easy/Medium",
                "Hard"
        );
        difficulty.setValue(difficulty.getItems().get(0));
        Button load = new Button("Load");
        load.setOnMouseClicked(event -> pickPuzzle((String) difficulty.getValue()));

        // add and center
        hbox.getChildren().addAll(difficulty, load, clear, solve);
        vbox.getChildren().add(hbox);
        hbox.setAlignment(Pos.CENTER);
        vbox.setAlignment(Pos.CENTER);

        // set scene
        Scene scene = new Scene(vbox);
        stage.setTitle("Sudoku");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}

