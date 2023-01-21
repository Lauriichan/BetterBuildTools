package me.lauriichan.build.builder.ui.window.ui.component;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.Objects;

import me.lauriichan.build.builder.ui.util.Rotation;
import me.lauriichan.build.builder.ui.util.render.Area;
import me.lauriichan.build.builder.ui.window.ui.Component;

public class Sprite extends Component {

    private Image image;
    private Rotation original;

    private int imgWidth;
    private int imgHeight;

    private int imgWidthHalf;
    private int imgHeightHalf;

    private Rotation rotation;
    private int amount = 0;

    public Sprite(final Image image) {
        this(image, Rotation.NORTH);
    }

    public Sprite(final Image image, final Rotation rotation) {
        setOriginal(image, rotation);
    }

    public void setOriginal(final Image image, final Rotation rotation) {
        this.image = Objects.requireNonNull(image);
        this.original = Objects.requireNonNull(rotation);
        setRotation(original);
        this.imgWidth = image.getWidth(null);
        this.imgHeight = image.getHeight(null);
        this.imgWidthHalf = imgWidth / 2;
        this.imgHeightHalf = imgHeight / 2;
        setWidth(imgWidth);
        setHeight(imgHeight);
    }

    public void setRotation(final Rotation rotation) {
        if (rotation == this.rotation) {
            return;
        }
        this.rotation = Objects.requireNonNull(rotation);
        this.amount = rotation.ordinal() - original.ordinal();
    }

    public Rotation getRotation() {
        return rotation;
    }

    @Override
    public void render(final Area area) {
        final Graphics2D graphics = area.getGraphics();
        final AffineTransform old = graphics.getTransform();
        final AffineTransform transform = (AffineTransform) old.clone();
        transform.quadrantRotate(amount, imgWidthHalf, imgHeightHalf);
        graphics.setTransform(transform);
        graphics.drawImage(image, 0, 0, imgWidth, imgHeight, null);
        graphics.setTransform(old);
    }

    @Override
    public boolean isUpdating() {
        return false;
    }

}
