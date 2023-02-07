package PacMan;

public class WallNode {
    public boolean isConnected = false;
    public boolean[] wallInDirection = new boolean[4]; // 0 = up, 1 = right, 2 = down, 3 = left, false for no wall
    public boolean hasAWall() {
        for (int i = 0; i < 4; i++) {
            if (wallInDirection[i]) {
                return true;
            }
        }
        return false;
    }
}
