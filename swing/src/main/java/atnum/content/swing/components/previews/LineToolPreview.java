package atnum.content.swing.components.previews;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import atnum.content.core.geometry.PenPoint2D;
import atnum.content.core.model.shape.LineShape;
import atnum.content.core.tool.Stroke;
import atnum.content.swing.converter.ColorConverter;
import atnum.content.swing.renderer.LineRenderer;

public class LineToolPreview extends ToolPreview {

    private final LineRenderer renderer;

    private final LineShape lineShape;

    public LineToolPreview() {
        renderer = new LineRenderer();
        lineShape = new LineShape(new Stroke());
    }

    @Override
    public void setColor(Color color) {
        lineShape.getStroke().setColor(ColorConverter.INSTANCE.from(color));
        repaint();
    }

    @Override
    public void setWidth(float width) {
        lineShape.getStroke().setWidth(width * 1.5);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        lineShape.setStartPoint(new PenPoint2D(20f, getHeight() / 2f));
        lineShape.setEndPoint(new PenPoint2D(getWidth() - 20f, getHeight() / 2f));

        renderer.render(lineShape, (Graphics2D) g);
    }
}
