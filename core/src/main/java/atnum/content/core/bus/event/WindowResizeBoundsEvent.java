package atnum.content.core.bus.event;

import java.awt.*;

public class WindowResizeBoundsEvent {

    public Rectangle getBounds() {
        return bounds;
    }

    private final Rectangle bounds;
    public WindowResizeBoundsEvent(Rectangle bounds) {
        this.bounds = bounds;
    }
}
