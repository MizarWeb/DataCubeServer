/**
 * Apache License 2
 */
package io.github.malapert.jwcs.proj.gui;

/**
 *
 * @author malapert
 */
public final class MapPoint {

    /**
     * X coordinate along X axis.
     */
    public double x;
    
    /**
     * Y coordinate along Y axis.
     */
    public double y;
    
    /**
     * Constructor.
     */
    public MapPoint() {
        //do nothing
    }

    /**
     * Constructor.
     * @param x coordinate along X axis
     * @param y coordinate along Y axis
     */
    public MapPoint (final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    
    @Override
    public boolean equals(final Object obj) {
        final MapPoint p = (MapPoint)obj;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }
}
