package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class WorldFrame {

    private static final int WIDTH = Engine.WIDTH;


    private static final int HEIGHT = Engine.HEIGHT;

    private boolean _lightOnOrNot;

    private static final TETile FLOOR = Tileset.SAND;

    private static final TETile WALL = Tileset.GRASS;

    private static final TETile BLANK = Tileset.MOUNTAIN;

    private static final TETile AVATAR = Tileset.FLOWER;

    private Coordinate _avatarCor;
    private static final int MAX_ROOM_NUM = 15;

    private static final int MIN_ROOM_NUM = 5;

    private Room[] _rooms;
    private Random _state;

    private TETile[][] _grid;

    private HashSet<Coordinate> _allWalls;

    private HashSet<Coordinate> _allFloors;

    private String _allActions;

    public WorldFrame(String seed) {
        _state = new Random(Long.parseLong(seed));
        _grid = new TETile[WIDTH][HEIGHT];
        _lightOnOrNot = true;
        createWorld();
        _allActions = "n" + seed + "s";
    }

    public TETile[][] getGrid() {
        if (_lightOnOrNot) {
            return _grid;
        } else {
            TETile[][] dummy = new TETile[WIDTH][HEIGHT];
            for (int i = 0; i < WIDTH; i += 1) {
                for (int j = 0; j < HEIGHT; j += 1) {
                    if (new Coordinate(i, j).dist(_avatarCor) > 5) {
                        dummy[i][j] = Tileset.NOTHING;
                    } else {
                        dummy[i][j] = _grid[i][j];
                    }
                }
            }
            return dummy;
        }
    }

    public void changeLight() {
        _lightOnOrNot = !_lightOnOrNot;
    }

    public void updateWorld(char c) {
        Coordinate dummy = null;
        int x = _avatarCor.getX();
        int y = _avatarCor.getY();
        if (c == 'W' || c == 'w') {
            dummy = new Coordinate(x, y + 1);
        } else if (c == 'A' || c == 'a') {
            dummy = new Coordinate(x - 1, y);
        } else if (c == 'S' || c == 's') {
            dummy = new Coordinate(x, y - 1);
        } else if (c == 'D' || c == 'd') {
            dummy = new Coordinate(x + 1, y);
        } else {

        }
        if (moveAvatar(dummy)) {
            _grid[_avatarCor.getX()][_avatarCor.getY()] = FLOOR;
            _avatarCor = dummy;
            _grid[_avatarCor.getX()][_avatarCor.getY()] = AVATAR;
        }
        _allActions += c;
    }

    public String getAllActions() {
        return _allActions + "\n";
    }

    public String getTileName(int x, int y) {
        TETile dummy = _grid[x][y];
        if (dummy.equals(AVATAR)) {
            return "AVATAR";
        } else if (dummy.equals(FLOOR)) {
            return "FLOOR";
        } else if (dummy.equals(WALL)) {
            return "WALL";
        } else {
            return "BLANK";
        }
    }

    private boolean moveAvatar(Coordinate c) {
        return _allFloors.contains(c);
    }
    private void createWorld() {
        for (int i = 0; i < WIDTH; i += 1) {
            for (int j = 0;j < HEIGHT; j += 1) {
                _grid[i][j] = BLANK;
            }
        }
        int numRoom = RandomUtils.uniform(_state, MIN_ROOM_NUM,  MAX_ROOM_NUM);
        _rooms = new Room[numRoom];
        for (int i = 0; i < numRoom; i += 1) {
            Room dummy = createRoom();
            while (dummy.overlap(_rooms)) {
                 dummy = createRoom();
            }
            for (Coordinate ext: dummy.getExteriors()) {
                _grid[ext.getX()][ext.getY()] = WALL;
            }
            for (Coordinate inte: dummy.getInteriors()) {
                _grid[inte.getX()][inte.getY()] = FLOOR;
            }
            _rooms[i] = dummy;
        }
        connectRoom();
        _allWalls = new HashSet<>();
        _allFloors = new HashSet<>();
        for (int i = 0; i < WIDTH; i += 1) {
            for (int j = 0;j < HEIGHT; j += 1) {
                if (_grid[i][j].equals(WALL)) {
                    _allWalls.add(new Coordinate(i, j));
                } else if (_grid[i][j].equals(FLOOR)) {
                    _allFloors.add(new Coordinate(i, j));
                }
            }
        }
        _avatarCor = (Coordinate) _allFloors.toArray()[RandomUtils.uniform(_state, _allFloors.size())];
        _grid[_avatarCor.getX()][_avatarCor.getY()] = AVATAR;
    }

    private Room createRoom() {
        int width = RandomUtils.uniform(_state, 5, 11);
        int height = RandomUtils.uniform(_state, 5, 11);
        int x = RandomUtils.uniform(_state, 0, 50);
        int y = RandomUtils.uniform(_state, 0, 20);
        Coordinate cor = new Coordinate(x, y);
        return new Room(width, height, cor);
    }

    private void connectRoom() {
        Union dummy = new Union(_rooms.length);
        for (int i = 0; i < _rooms.length; i += 1) {
            for (int j = i + 1; j < _rooms.length; j += 1) {
                if (!dummy.isJoined(i, j)) {
                    connect(_rooms[i], _rooms[j]);
                    dummy.join(i, j);
                }
            }
        }
    }

    private void connect(Room r1, Room r2) {
        ArrayList<Coordinate> om1 = r1.getOutermost();
        ArrayList<Coordinate> om2 = r2.getOutermost();
        Coordinate c1 = om1.get(RandomUtils.uniform(_state, om1.size()));
        Coordinate c2 = om2.get(RandomUtils.uniform(_state, om2.size()));

        Coordinate first = c1.getX() < c2.getX()? c1: c2;
        Coordinate other = first == c1? c2: c1;
        for (int i = first.getX(); i < other.getX() + 1; i +=1) {
            for (int j = first.getY() - 1; j < first.getY() + 2; j += 1) {
                if (j == first.getY()) {
                    _grid[i][j] = FLOOR;
                } else if (_grid[i][j] != FLOOR){
                    _grid[i][j] = WALL;
                }
            }
        }
        Coordinate dummy = c1 = new Coordinate(other.getX(), first.getY());
        c2 = other;
        first = c1.getY() < c2.getY()? c1: c2;
        other = first == c1? c2: c1;
        for (int i = first.getX() - 1; i < first.getX() + 2; i +=1) {
            for (int j = first.getY(); j < other.getY() + 1; j += 1) {
                if (i == first.getX()) {
                    _grid[i][j] = FLOOR;
                } else if (_grid[i][j] != FLOOR) {
                    _grid[i][j] = WALL;
                }
            }
        }
        if (dummy == other) {
            _grid[dummy.getX() + 1][dummy.getY() + 1] = WALL;
        } else {
            _grid[dummy.getX() + 1][dummy.getY() - 1] = WALL;
        }
    }

}
