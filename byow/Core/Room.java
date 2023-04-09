package byow.Core;

import java.util.ArrayList;

public class Room {

    private ArrayList<Coordinate> _exteriors;

    private ArrayList<Coordinate> _interiors;

    private ArrayList<Coordinate> _outermost;

    public Room(int width, int height, Coordinate cor) {
        int xStart = cor.getX();
        int yStart = cor.getY();
        int xEnd = xStart + width;
        int yEnd = yStart + height;
        _exteriors = new ArrayList<>();
        _interiors = new ArrayList<>();
        _outermost = new ArrayList<>();
        for (int x = xStart; x < xEnd; x += 1) {
            for (int y = yStart; y < yEnd; y += 1) {
                if (x == xStart|| x == xEnd - 1 || y == yStart || y == yEnd - 1) {
                    _exteriors.add(new Coordinate(x, y));
                } else {
                    if (x == xStart + 1 || x == xEnd - 2 || y == yStart + 1 || y == yEnd - 2) {
                        _outermost.add(new Coordinate(x, y));
                    }
                    _interiors.add(new Coordinate(x, y));
                }
            }
        }
    }

    public boolean overlap(Room[] rooms) {
        for (Room r: rooms) {
            if (r == null) {
                return false;
            }
            for (Coordinate ext1: this._exteriors) {
                for (Coordinate ext2: r._exteriors) {
                    if (ext1.dist(ext2) <= 2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public ArrayList<Coordinate> getOutermost() {
        return _outermost;
    }

    public ArrayList<Coordinate> getExteriors() {
        return _exteriors;
    }

    public ArrayList<Coordinate> getInteriors() {
        return _interiors;
    }
}
