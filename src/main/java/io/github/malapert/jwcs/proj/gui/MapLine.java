/*
 * MapLine.java
 *
 * Created on September 15, 2006, 5:04 PM
 * Apache License 2
 *
 */

package io.github.malapert.jwcs.proj.gui;

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * MapLine stores a series of 2D points in a vector that form a simple line.
 * @author Bernhard Jenny, Institute of Cartography, ETH Zurich.
 */
public class MapLine {

    /**
     * A vector that holds all points of this line.
     */
    private final List<MapPoint> points = new ArrayList<>();
    
    /**
     * The bounding box of this line.
     */
    private Rectangle2D extension;
    
    /**
     * The path that can be used to draw this line.
     */
    private GeneralPath path;

    /** Creates a new instance of MapLine */
    public MapLine() {
        //do nothing
    }
    
    /**
     * Return the number of points of this line.
     * @return the number of points.
     */
    public int size() {
        return points.size();
    }
    
    /**
     * Return a copy of a point.
     * @param pointID The index of the point to return (zero-based).
     * @return A reference to the point at position pointID.
     */
    public MapPoint getPoint (final int pointID) {
        return points.get(pointID);
    }

    /**
     * Returns the list of points.
     * @return the list of points
     */
    public List<MapPoint> getPoints() {
        return points;
    }
    
    /**
     * Add a point at the end of the line.
     * @param mapPoint The point to add.
     */
    public void addPoint(final MapPoint mapPoint) {
        if (Double.isNaN(mapPoint.x) || Double.isNaN(mapPoint.y)) {
            return;
        }
        points.add(mapPoint);
        pointChanged();
    }
    
    /**
     * Add a point at the end of the line.
     * @param x The horizontal coordinate of the point.
     * @param y The vertical coordinate of the point.
     */
    public void addPoint(final double x, final double y) {
        addPoint(new MapPoint(x, y));
    }
    
    /**
     * Remove a point from the line.
     * @param mapPoint The point to remove.
     */
    public void removePoint(final MapPoint mapPoint) {
        points.remove(mapPoint);
        pointChanged();
    }
    
    /**
     * Remove a point from the line.
     * @param pointID The index of the point to remove (zero-based).
     */
    public void removePoint(final int pointID) {
        points.remove(pointID);
        pointChanged();
    }
    
    /**
     * Return the bounding box of this line.
     * @return The bounding box.
     */
    public java.awt.geom.Rectangle2D getExtension() {
        
        if (points.isEmpty()) {
            return null;
        }
        
        // If a bounding box has been computed before, return it.
        // This is probably faster than looping throuh all points.
        if (extension != null) {
            return extension;
        }
        
        // search the smallest and largest coordinates in x and y direction.
        double xMin = Double.MAX_VALUE;
        double xMax = -Double.MAX_VALUE;
        double yMin = Double.MAX_VALUE;
        double yMax = -Double.MAX_VALUE;
        
        for (final MapPoint p : points) {
            if (p.x < xMin) {
                xMin = p.x;
            }
            if (p.x > xMax) {
                xMax = p.x;
            }
            if (p.y < yMin) {
                yMin = p.y;
            }
            if (p.y > yMax) {
                yMax = p.y;
            }
        }
        
        // store the found bounding values
        extension = new java.awt.geom.Rectangle2D.Double(xMin, yMin,
                xMax - xMin, yMax - yMin);
        return extension;

    }
    
    /**
     * Return a GeneralPath that can be drawn.
     * @return The GeneralPath.
     */
    public GeneralPath getPath() {
        
        // if the path has been constructed before, return it.
        if (path != null) {
            return path;
        }
        
        path = new GeneralPath();
        final int nbrPoints = size();
        
        // a line must have at least 2 points
        if (nbrPoints < 2) {
            return path;
        }
        
        // add first point
        path.moveTo((float)points.get(0).x, points.get(0).y);
        
        // add all following points
        for (int pointID = 1; pointID < nbrPoints; pointID++) {
            final MapPoint p = points.get(pointID);
            path.lineTo((float)p.x, (float)p.y);
        }
        
        return path;
    }
    
    /**
     * Private helper method that must be called whenever a point changes.
     */
    private void pointChanged() {
        path = null;
        extension = null;
    }


}
