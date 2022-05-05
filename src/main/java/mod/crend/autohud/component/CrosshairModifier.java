package mod.crend.autohud.component;

public enum CrosshairModifier {
    DISABLED(240, 240),
    DOT(45, 45),
    DIAGONAL_CROSS(45, 60),
    BRACKETS(0, 45),
    BRACKETS_BOTTOM(0, 60),
    BRACKETS_TOP(0, 95),
    ROUND_BRACKETS(30, 60),
    LINES(15, 60),
    LINE_BOTTOM(15, 75);

    private final int x;
    private final int y;
    CrosshairModifier(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
