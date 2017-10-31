/* 
 * Copyright (C) 2014-2016 Jean-Christophe Malapert
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.malapert.jwcs;

import io.github.malapert.jwcs.proj.exception.JWcsException;
import java.util.Iterator;

/**
 * Interface to get the main WCS keywords.
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public interface JWcsKeyProvider {
    
    /**
     * Number of axes in WCS description.
     * 
     * <p>Specify the highest value of the index of any WCS key-word in the header 
     * (i.e. CRPIX j, PC i j or CD i j, CDELT i,CTYPE i, CRVAL i, or CUNIT i). 
     * The default value is the larger of NAXIS and the largest index of these 
     * keywords found in the FITS header.
     * 
     * @return the number of axes 
     */
    int wcsaxes();
    
    /**
     * Number of pixels along j axis. 
     * @param j the axis coordinate
     * @return number of pixels along j axis
     */
    int naxis(int j);
    
    /**
     * Coordinate value at reference point.
     * @param n the axis coordinate
     * @return coordinate value
     */
    double crval(int n);
    
    /**
     * Array location of the reference point in pixels.
     * @param n the axis
     * @return array location of the reference point
     */
    double crpix(int n);
    
    /**
     * Axis type.
     * @param n the axis
     * @return the projection type
     */
    String ctype(int n);
    
    /**
     * Linear transformation matrix (with scale).
     * @param i the row
     * @param j the column
     * @return Linear transformation matrix
     */
    double cd(int i, int j);
    
    /**
     * non-linear algorithms.
     * @param i the row
     * @param m the column
     * @return non-linear algorithms
     */
    double pv(int i, int m);   
    
    /**
     * The units of CRVAL_i and CDELT_i.
     * @param i the axis
     * @return the unit
     */
    String cunit(int i);
    
    /**
     * longitude of the pole.
     * @return long pole
     */
    double lonpole();
    
    /**
     * latitude of the pole.
     * @return lat pole
     */
    double latpole();
    
    /**
     * The epoch of the equinox.
     * @return the equinox
     */
    double equinox();
    
    /**
     * Check if the CD matrix is available.
     * @return True when the CD matrix is available else False
     */
    boolean hasCd();
    
    /**
     * Transforms the position of a pixel given by (x,y) in a position in the sky.
     * @param x X coordinate of the pixel. Starts to 1 according to FITS standard
     * @param y Y coordinate of the pixel. Starts to 1 according to FITS standard.
     * @return the pixel position in the sky
     * @throws io.github.malapert.jwcs.proj.exception.JWcsException When there is a projection error
     */
    double[] pix2wcs(double x, double y) throws JWcsException;
    
    /**
     * Transforms an array of pixel position in an array of position in the sky.
     * @param pixels an array of pixel. Starts to 1 according to FITS standard
     * @return an array of sky position 
     * @throws io.github.malapert.jwcs.proj.exception.JWcsException When there is a projection error
     */
    double[] pix2wcs(double[] pixels) throws JWcsException;
    
    /**
     * Transforms the sky position given by (longitude, latitude) 
     * in a pixel position.
     * @param longitude longitude of the sky position
     * @param latitude latitude of the sky position
     * @return the sky position in the pixel grid.
     * @throws io.github.malapert.jwcs.proj.exception.JWcsException When there is a projection error
     */
    double[] wcs2pix(double longitude, double latitude) throws JWcsException;
    
    /**
     * Transforms an array of sky position in an array of pixel position.
     * @param skyPositions array of sky positions
     * @return the sky position in the pixel grid.
     * @throws io.github.malapert.jwcs.proj.exception.JWcsException When there is a projection error
     */  
    double[] wcs2pix(double[] skyPositions) throws JWcsException;

    /**
     * Returns true if the given lat/lon point is visible in this projection.
     * @param lon longitude in degrees.
     * @param lat latitude in degrees.
     * @return True when the point is visible otherwise False.
     */
    boolean inside(double lon, double lat);  
    
    /**
     * Returns the center of the image in sky coordinates.
     * @return the center of the image
     * @throws io.github.malapert.jwcs.proj.exception.JWcsException When there is a projection error
     */
    double[] getCenter() throws JWcsException;
    
    /**
     * Returns the Field of view of the image in sky coordinates.
     * <p>
     * Computes the following coordinates (0.5,0.5), (naxis1+0.5,0.5), (naxis1+0.5, naxis2+0.5), (0.5, naxis2+0.5).
     * </p>
     * @return the field of view of the image.
     * @throws io.github.malapert.jwcs.proj.exception.JWcsException When there is a projection error
     */
    double[] getFov() throws JWcsException;
    
    /**
     * Checks if a keyword exists.
     * @param keyword keyword to test
     * @return True when the keyword exists else False
     */
    boolean hasKeyword(String keyword);
    
    /**
     * Returns the keyword value as integer.
     * @param keyword keyword to get
     * @return the value as Int
     */
    int getValueAsInt(String keyword);
    
    /**
     * Returns the keyword value as double.
     * @param keyword the keyword
     * @return the value
     */
    double getValueAsDouble(String keyword);
    
    /**
     * Returns the keyword value as float.
     * @param keyword the keyword
     * @return the value
     */
    float getValueAsFloat(String keyword);    
    
    /**
     * Returns the keyword value as String.
     * @param keyword the keyword
     * @return the value.
     */
    String getValueAsString(String keyword);
    
    /**
     * Returns a iterator of the WCS keywords.
     * @return iterator
     */
    Iterator iterator();
}
