package atnum.content.swing.components.previews;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import atnum.content.core.geometry.PenPoint2D;
import atnum.content.core.model.shape.RectangleShape;
import atnum.content.core.tool.Stroke;
import atnum.content.swing.converter.ColorConverter;
import atnum.content.swing.renderer.RectangleRenderer;

public class RectangleToolPreview extends ToolPreview {

    private final RectangleRenderer renderer;

    private final RectangleShape rectangleShape;

    public RectangleToolPreview() {
        renderer = new RectangleRenderer();
        rectangleShape = new RectangleShape(new Stroke());
    }

    @Override
    public void setColor(Color color) {
        rectangleShape.getStroke().setColor(ColorConverter.INSTANCE.from(color));
        repaint();
    }

    @Override
    public void setWidth(float width) {
        rectangleShape.getStroke().setWidth(width * 1.2);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        rectangleShape.setStartPoint(new PenPoint2D(15f, getHeight() / 8f));
        rectangleShape.setEndPoint(new PenPoint2D(getWidth() - 15f, 7f * getHeight() / 8f));

        renderer.render(rectangleShape, (Graphics2D) g);
    }
}
