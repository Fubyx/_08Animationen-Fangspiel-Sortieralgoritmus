package PacMan;

public class WallNode {
    public boolean isConnected = false;
    public boolean[] wallInDirection = new boolean[4]; // 0 = up, 1 = right, 2 = down, 3 = left, false for no wall
}
