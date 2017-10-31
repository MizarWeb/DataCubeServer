/*
 * Copyright (C) 2016 malapert
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
package io.github.malapert.jwcs.datum;

/**
 *
 * @author malapert
 */
public class CoordinateReferenceFrameFactory {
    
    public static CoordinateReferenceFrame create(final CoordinateReferenceFrame.ReferenceFrame referenceFrame) {
        CoordinateReferenceFrame datum = null;
        if(null != referenceFrame) switch (referenceFrame) {
            case FK4:
                datum = new FK4();
                break;
            case FK4_NO_E:
                datum = new FK4NoEterms();
                break;
            case FK5:
                datum = new FK5();
                break;
            case ICRS:
                datum = new ICRS();
                break;
            case J2000:
                datum = new J2000();
                break;
            default:
                throw new IllegalArgumentException("The reference frame is not implemented");
        } else {
            throw new RuntimeException("A reference frame must be provided");
        }
        return datum;
    }
    
}
