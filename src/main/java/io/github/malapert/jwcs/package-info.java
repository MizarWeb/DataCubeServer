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

/**
 * Provides the classes necessary to handle astronomical projections and 
 * coordinate reference system conversions from a FITS file.
 * 
 * <h2>How to use it</h2>
 * <h3>1 - Astronomical projection</h3>
 * <h4>1.1 - From a FITS file</h4>
 * First, you need to load a FITS file. This FITS file will be read to find 
 * necessary keywords for the computation. Then, use the object FitsWcs and the
 * methods :
 * <ul>
 * <li>pix2wcs : to compute the pixel position in the sky</li>
 * <li>wcs2pix : to compute the sky position in the camera</li>
 * </ul>
 * <pre>
 *    <code>
 *    AbstractJWcs wcs = new WcsFits(new Fits("/tmp/1904-66_AIT.fits"));
 *    wcs.doInit();
 *    double[] skyPosition = wcs.pix2wcs(1, 1);
 *    </code>
 * </pre>
 * <h4>1.2 - From a dictionary (Map)</h4>
 * First, you need to create a Map file. This Map file contains the list of 
 * necessary keywords for the computation. Then, use the object FitsWcs and the
 * methods :
 * <ul>
 * <li>pix2wcs : to compute the pixel position in the sky</li>
 * <li>wcs2pix : to compute the sky position in the camera</li>
 * </ul>
 * <pre>
 *    <code>
 *    String filename="/tmp/myHeaderFits.hdr";
 *    HeaderFitsReader hdr = new HeaderFitsReader(filename);
 *    List&lt;List&lt;String&gt;&gt; listKeywords = hdr.readKeywords();
 *    Map&lt;String, String&gt; keyMap = new HashMap();
 *    listKeywords.stream().forEach((keywordLine) -&gt; {
 *        keyMap.put(keywordLine.get(0), keywordLine.get(1));
 *    });
 *    JWcsMap wcs = new JWcsMap(keyMap);
 *    wcs.doInit();
 *    double[] result = wcs.pix2wcs(1, 1);
 *    </code>
 * </pre>
 * 
 * <h3>2 - Coordinate reference systems conversion</h3>
 * <h4>2.1 - From a FITS file</h4>
 * First, you need to load a FITS file. This FITS file will be read to find 
 * necessary keywords for the computation.
 * <pre>
 *   <code>
 *      JWcs wcs = new JWcsFits(new Fits(new URL("http://fits.gsfc.nasa.gov/samples/WFPC2ASSNu5780205bx.fits")));
 *      wcs.doInit();
 *      // convert the pixel (1,1) to the sky
 *      double[] posOrigin = wcs.pix2wcs(1, 1);
 *      // Get the sky system
 *      Crs sysOrigin = wcs.getCrs();
 *      // Convert the coordinates from the sysOrigin sky system to Galactic      
 *      SkySystem sysTarget = new Galactic();
 *      SkyPosition skyPosTarget = sysOrigin.convertTo(sysTarget, posOrigin[0], posOrigin[1]);      
 *   </code>
 * </pre>
 * <h4>2.2 - From sky coordinates</h4>
 * <pre>
 *   <code>
 *      double coordinates[][] = new double[][]{
 *           {0, 0},
 *           {180, 60},
 *           {359, 60},
 *           {86, -35}
 *       };
 *       Crs sk1 = new Equatorial();
 *       Crs sk2 = new Equatorial(new FK5());
 *       for (double[] coordinate : coordinates) {
 *           SkyPosition[] result = sk1.convertTo(sk2, coordinate);
 *           result = sk2.convertTo(sk1, result[0].getDoubleArray());
 *           assertArrayEquals(coordinate, result[0].getDoubleArray(), EPSILON_SINGLE);
 *       }
 *   </code>
 * </pre>
 * 
 * @see <a href="./proj/package-summary.html">List of supported projections</a>
 * @see <a href="./JWcs.html#field.summary">List of FITS keywords</a>
 * @see <a href="./coordsystem/package-summary.html#package.description">List of supported coordinate systems</a>
 * 
 */
package io.github.malapert.jwcs;
