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
package io.github.malapert.jwcs.proj;

import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author malapert
 */
public class NCP extends SIN {
    
    public NCP() {
        this(FastMath.toDegrees(AbstractZenithalProjection.DEFAULT_PHI0), FastMath.toDegrees(AbstractZenithalProjection.DEFAULT_THETA0), 20);
    }
    
    public NCP(final double crval1, final double crval2, final double crval2bis) {
        super(crval1, crval2, 0, 1/FastMath.tan(FastMath.toRadians(crval2bis)));
    }
    
}
