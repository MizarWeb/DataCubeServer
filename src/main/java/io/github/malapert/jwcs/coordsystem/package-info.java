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
 * Provides the classes necessary to handle 
 * the conversion between the astronomical coordinate reference systems.
 *
 * <p>A Coordinate Reference System (crs) contains two different elements : 
 * the <b>coordinate reference frame</b> {@link io.github.malapert.jwcs.crs.AbstractCrs#getCoordinateReferenceFrame}
 * and the <b>coordinate system</b> {@link io.github.malapert.jwcs.crs.AbstractCrs#getCoordinateReferenceSystem} .
 *
 * <p>The coordinate reference frame defines how the CRS is related to the origin
 * (position and the date of the origin - equinox {@link io.github.malapert.jwcs.datum.CoordinateReferenceFrame#getEquinox} , 
 * date of observation {@link io.github.malapert.jwcs.datum.CoordinateReferenceFrame#getEpochObs} ) and 
 * the coordinate system describes how the coordinates are expressed in the 
 * coordinate reference frame (e.g. as cartesian coordinates, spherical 
 * coordinates or coordinates of a map projection).
 * 
 * <p>An equinox is an astronomical event in which the plane of Earth's equator 
 * passes through the center of the Sun.
 * 
 * <p>An epoch of observation is the moment in time when the coordinates are known 
 * to be correct. Often, this will be the date of observation, and is important 
 * in cases where coordinates systems move with respect to each other over the 
 * course of time
 * 
 * <p>These classes are capable of converting a coordinate reference system (crs)
 * to another one. This package contains three main groups of classes:
 * <ul>
 * <li>classes representing the supported coordinate systems : Ecliptic, Equatorial,
 * galactic and super-galactic.</li>
 * <li>classes representing the supported reference frame : FK4, FK4_NO_E, FK5, 
 * J2000 and ICRS</li>
 * <li>classes converting sky coordinates from a crs to another one</li>
 * </ul>
 * 
 * <h2>Coordinate reference system</h2>
 * In the current implementation, it exists four CRS:
 * <ul>
 * <li>{@link io.github.malapert.jwcs.crs.Equatorial} coordinate reference system</li>
 * <li>{@link io.github.malapert.jwcs.crs.Ecliptic} coordinate reference system</li>
 * <li>{@link io.github.malapert.jwcs.crs.Galactic} coordinate reference system</li>
 * <li>{@link io.github.malapert.jwcs.crs.SuperGalactic} coordinate reference system</li>
 * </ul>
 * <br>
 * For the {@link io.github.malapert.jwcs.crs.Equatorial} and {@link io.github.malapert.jwcs.crs.Ecliptic} CRS, it is possible to describe
 * the origin of the CRS by the use of a coordinate reference frame :
 * <ul>
 * <li>{@link io.github.malapert.jwcs.datum.ICRS}</li>
 * <li>{@link io.github.malapert.jwcs.datum.FK5}</li>
 * <li>{@link io.github.malapert.jwcs.datum.FK4}</li>
 * <li>{@link io.github.malapert.jwcs.datum.FK4NoEterms}</li>
 * <li>{@link io.github.malapert.jwcs.datum.J2000}</li>
 * </ul>
 * 
 * <h2>Others capabilities</h2>
 * The AbstractCrs object is also able to compute :
 * <ul>
 * <li>the angular separation between two sky positions. A sky position contains the
 * position and the crs in which the position is expressed.</li>
 * <li>the conversion of sky positions to a target CRS.</li> 
 * </ul>
 * 
 * <h2>How to use it</h2>
 * Describe first your coordinate reference system. For example, let's say that we
 * want to convert sky coordinates from an equatorial system in ICRS to a 
 * super-galactic system. Then use the convertTo method from a CRS object:
 * 
 * <p><u>Example1:</u> Converts a position (182.63867, 39.401167) in Equatorial CRS into SuperGalactic CRS<br><code>
 * AbstractCrs crs1 = new Equatorial(new ICRS());<br>
 * AbstractCrs crs2 = new SuperGalactic();<br>
 * SkyPosition position = crs1.convertTo(crs2, 182.63867, 39.401167);
 * </code>
 * 
 * <p><u>Example2:</u> Converts the angular position from two positions, each being in a different CRS<br><code>
 * CoordinateReferenceFrame icrs = new ICRS();<br>
 * AbstractCrs sysEqIcrs = new Equatorial(icrs);<br>
 * CoordinateReferenceFrame fk5 = new FK5();<br>
 * AbstractCrs sysEqFK5 = new Equatorial(fk5);<br> 
 * SkyPosition pos1 = new SkyPosition(10, 9, sysEqIcrs);<br>
 * SkyPosition pos2 = new SkyPosition(11, 10, sysEqFK5);<br>
 * double separation = AbstractCrs.separation(pos1, pos2);
 * </code>
 * 
 * <p><u>Example3:</u> Converts a position in a CRS to another CRS<br><code>
 * CoordinateReferenceFrame icrs = new ICRS();<br>
 * AbstractCrs sysEqIcrs = new Equatorial(icrs);<br>
 * CoordinateReferenceFrame fk5 = new FK5();<br>
 * AbstractCrs sysEqFK5 = new Equatorial(fk5);<br> 
 * SkyPosition pos1 = new SkyPosition(10, 9, sysEqIcrs);<br>
 * SkyPosition result = AbstractCrs.convertTo(sysEqFK5, pos1);
 * </code>
 * 
 */
package io.github.malapert.jwcs.coordsystem;
