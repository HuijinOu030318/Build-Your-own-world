package byow.Core;

public class Coordinate {
    private int _x;
    private int _y;

    public Coordinate(int x, int y) {
        _x = x;
        _y = y;
    }

    public int getX() {
        return _x;
    }

    public int getY() {
        return _y;
    }

    public int dist(Coordinate c) {
        return Math.abs(c._x - this._x) + Math.abs(c._y - this._y);
    }

    @Override
    public boolean equals(Object obj) {
        Coordinate c = (Coordinate) obj;
        return c._x == _x && c._y == _y;
    }

    @Override
    public int hashCode() {
        return _x + _y;
    }
}
