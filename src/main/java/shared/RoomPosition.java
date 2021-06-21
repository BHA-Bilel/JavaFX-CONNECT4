package shared;

public enum RoomPosition {
    BOTTOM, RIGHT, TOP, LEFT;

    public RoomPosition nextPlayerToNotify() {
        switch (this) {
            case BOTTOM:
                return RIGHT;
            case RIGHT:
                return TOP;
            case TOP:
                return LEFT;
            default:
                return null;
        }
    }
}
