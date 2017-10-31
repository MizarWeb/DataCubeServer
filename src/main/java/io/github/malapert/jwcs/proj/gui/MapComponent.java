/*
 * Map.java
 *
 * Created on September 15, 2006, 5:08 PM
 * Apache License 2
 *
 */
package io.github.malapert.jwcs.proj.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.apache.commons.math3.util.FastMath;

/**
 * Map is a JComponent that draws a set of MapLine objects.
 * @author Bernhard Jenny, Institute of Cartography, ETH Zurich.
 */
public class MapComponent extends JComponent {

    /**
     * A vector that holds all lines.
     */
    private List<MapLine> lines = new ArrayList<>();
    /**
     * The percentage of space that is added around the lines when drawing them.
     */
    private final static double BORDER_PERCENTAGE = 3;

    /**
     * Replaces the current lines by the passed lines.
     * @param lines A vector holding MapLine objects.
     */
    public void setLines(final List<MapLine> lines) {
        if (lines == null) {
            this.lines.clear();
        } else {
            this.lines = lines;
        }
        repaint();
    }

    /**
     * Returns the lines that are drawn by this map.
     * @return A vector of lines.
     */
    java.util.List<MapLine> getLines() {
        return lines;
    }

    /**
     * Return the bounding box of all lines.
     * @return The extension including all lines.
     */
    private Rectangle2D getMapExtension() {
        Rectangle2D totalExt = null;

        // loop over all lines
        final int nbrLines = lines.size();
        for (int i = 0; i < nbrLines; i++) {
            final MapLine line = (MapLine) lines.get(i);
            // ask the line for its bounding box
            final Rectangle2D lineExt = line.getExtension();
            // if we have not yet found a valid bounding box, use the 
            // bounding box of the current line.
            if (totalExt == null) {
                totalExt = lineExt;
            }
            // combine the bounding box of the current line with the bounding
            // box of all previous lines.
            if (totalExt != null && lineExt != null) {
                Rectangle2D.union(totalExt, lineExt, totalExt);
            }
        }
        return totalExt;
    }

    /**
     * Compute the scale by which the lines have to be scaled in order to show
     * them all.
     * @return The scale to apply to the lines to make them all visible.
     */
    private double getScaleToShowAll() {
        final Rectangle2D ext = getMapExtension();
        final Dimension dim = getSize();
        final double horScale = dim.getWidth() / ext.getWidth();
        final double verScale = dim.getHeight() / ext.getHeight();
        final double borderScale = 1 / (1 + BORDER_PERCENTAGE * 2 / 100);
        return FastMath.min(horScale, verScale) * borderScale;
    }

    /**
     * Draw all lines.
     * @param g The Graphics canvas to draw to.
     */
    @Override
    public void paintComponent(final Graphics g) {

        final Graphics2D g2d = (Graphics2D) g.create();

        // erase everything previously drawn
        g2d.setBackground(Color.white);
        g2d.clearRect(0, 0, getWidth(), getHeight());

        // enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // enable high quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        final Rectangle2D ext = getMapExtension();
        if (ext == null) {
            return;
        }

        /*
         * Transformation: offset, scale and flip vertically
         * x_ = (x-west)*scale;
         * y_ = (north-y)*scale = (y-north)*(-scale);
         */
        final double scale = getScaleToShowAll();
        //g2d.scale(-scale, -scale);
        g2d.scale(scale, -scale);
        // add a border so that the drawing is centered.
        final double borderX = (this.getWidth() / scale - ext.getWidth()) / 2;
        final double borderY = (this.getHeight() / scale - ext.getHeight()) / 2;
        //g2d.translate(-ext.getMinX() - 2*borderX, -ext.getMaxY() - borderY);
        g2d.translate(-ext.getMinX() + borderX, -ext.getMaxY() - borderY);

        // draw lines with a thin black stroke
        g2d.setStroke(new BasicStroke((float) (1 / scale)));
        g2d.setColor(Color.black);

        // draw each line
        final int nbrLines = lines.size();
        for (int lineID = 0; lineID < nbrLines; lineID++) {
            final MapLine line = (MapLine) lines.get(lineID);
            final GeneralPath path = line.getPath();
            g2d.draw(path);
        }
        g2d.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 200);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(200, 200);
    }
}
