package atnum.content.core.bus.event;

import java.awt.*;

public class WindowBootBoundsEvent {

    public Rectangle getBounds() {
        return bounds;
    }

    private final Rectangle bounds;
    public WindowBootBoundsEvent(Rectangle bounds) {
        this.bounds = bounds;
    }
}
