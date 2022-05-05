package mod.crend.autohud.component;

public enum Crosshair {
    DEFAULT(0, 0),
    CROSS(0, 15),
    DIAGONAL_CROSS(15, 0),
    DIAGONAL_SQUARE(15, 30),
    CARET(15, 45),
    CIRCLE(30, 0),
    SQUARE(45, 0);

    private final int x;
    private final int y;
    Crosshair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
