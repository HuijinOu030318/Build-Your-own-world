package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import java.io.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import edu.princeton.cs.algs4.StdDraw;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 60;
    public static final int HEIGHT = 30;

    private static final String PATH = "last_conf.txt";

    private String _seed;

    private WorldFrame _world;



    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        init();
        drawIntro();
        int i = 0;
        boolean readSeedOrNot = false;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (i == 0) {
                    if (c == 'N' || c == 'n') {
                        clear();
                        readSeedOrNot = true;
                        _seed = "";
                        drawSeed();
                    } else if (c == 'L' || c == 'l') {
                        restoreWorld();
                        ter.initialize(WIDTH, HEIGHT + 1, 0, 0);
                        drawWorld();
                    } else if (c == 'Q' || c == 'q'){
                        System.exit(0);
                    }

                } else if (readSeedOrNot) {
                    if (c == 'S' || c == 's') {
                        _world = new WorldFrame(_seed);
                        ter.initialize(WIDTH, HEIGHT + 1, 0, 0);
                        readSeedOrNot = false;
                        drawWorld();
                    } else {
                        _seed += c;
                        drawSeed();
                    }
                } else {
                    if (c == 'o' || c =='O') {
                        _world.changeLight();
                        drawWorld();
                    }else if (c == ':') {
                        while (true) {
                            if (StdDraw.hasNextKeyTyped()) {
                                char next = StdDraw.nextKeyTyped();
                                if (next == 'Q' || next == 'q') {
                                    save(_world.getAllActions());
                                    System.exit(0);
                                }
                            }
                        }
                    } else {
                        _world.updateWorld(c);
                        drawWorld();
                    }
                }
                i += 1;
            }
            if (_world != null) {
                drawHUD();
            }
        }
    }

    private void drawIntro() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH * 7.5, HEIGHT * 11, "CS 61B: THE GAME");
        StdDraw.text(WIDTH * 7.5, HEIGHT * 5, "New Game (N)");
        StdDraw.text(WIDTH * 7.5, HEIGHT * 4, "Load Game (L)");
        StdDraw.text(WIDTH * 7.5, HEIGHT * 3, "Quit (Q)");
        StdDraw.show();
    }

    private void drawSeed() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        String dummy = "Enter the Seed: " + _seed;
        StdDraw.text(WIDTH * 7.5, HEIGHT * 7,  dummy);
        StdDraw.show();
    }

    private void drawHUD() {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.filledRectangle(WIDTH / 2, HEIGHT + 0.5, WIDTH/2, 0.5);
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        if (x >= WIDTH || y >= HEIGHT) {
            return;
        }
        StdDraw.setPenColor(Color.BLACK);
        String curr = _world.getTileName(x, y);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        StdDraw.text(1.5, HEIGHT + 0.5, curr);
        StdDraw.text( 10, HEIGHT + 0.5, now.toString());
        StdDraw.show();
    }

    private void drawWorld() {
        assert _world != null;
        ter.renderFrame(_world.getGrid());
        drawHUD();
    }

    private void init() {
        StdDraw.setCanvasSize(WIDTH * 15, HEIGHT * 15);
        Font font = new Font("Times New Roman", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH * 15);
        StdDraw.setYscale(0, HEIGHT * 15);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        init();
        char first = input.charAt(0);
        int i = 1;
        _seed = "";
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        if (first == 'N' || first == 'n') {
            clear();
            for (;i < input.length(); i += 1) {
                if (!updateSeed(input.charAt(i))) {
                    i += 1;
                    break;
                }
            }
            _world = new WorldFrame(_seed);
        } else if (first == 'L' || first == 'l') {
            restoreWorld();
        } else {
            System.exit(0);
        }
        for (;i < input.length(); i += 1) {
            char c = input.charAt(i);
            if (i + 1 <input.length() && c == ':'
                    && (input.charAt(i + 1) == 'q' || input.charAt(i + 1) == 'Q')) {
                save(_world.getAllActions());
                break;
            } else if (c == 'o' || c == 'o') {
                _world.changeLight();
            }
            _world.updateWorld(c);
        }
        TETile[][] finalWorldFrame = _world.getGrid();
        return finalWorldFrame;
    }

    private boolean updateSeed(char c) {
        if (c == 's' || c == 'S') {
            return false;
        } else{
            _seed += c;
            return true;
        }
    }

    private void restoreWorld() {
        try {
            FileReader reader = new FileReader(PATH);
            BufferedReader buffer = new BufferedReader(reader);
            String line = buffer.readLine();
            buffer.close();
            if (line == null) {
                System.exit(0);
            }
            interactWithInputString(line);
        } catch (IOException e) {
            System.exit(0);
        }
    }

    private void save(String stuff) {
        try {
            FileWriter writer = new FileWriter(PATH);
            BufferedWriter buffer = new BufferedWriter(writer);
            buffer.write(stuff);
            buffer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void clear() {
        save("");
    }
}
