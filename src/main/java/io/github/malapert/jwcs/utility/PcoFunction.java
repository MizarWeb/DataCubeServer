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
package io.github.malapert.jwcs.utility;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;

/**
 * Pco function to solve.
 * 
 * <p>The equation to solve is the following:<br>
 * <code>x<sup>2</sup> - 2 (y - \u03B8) * cot(\u03B8) + (y - \u03B8)<sup>2</sup> = 0</code>
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 */
public class PcoFunction implements UnivariateFunction {
    
    /**
     * x value.
     */
    private double x;
    
    /**
     * y value.
     */
    private double y;
    
    /**
     * Creates a constructor by initializing x and y to zero.
     */
    public PcoFunction() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Sets the x and y values in radians.
     * @param x the x value
     * @param y the y value
     */
    public void set(final double x, final double y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public double value(final double theta) {
        final double tanthe = FastMath.tan(theta);
        final double cothe = 1/tanthe;
        final double xx = getX()*getX();
        final double ymthe = getY()- theta;        
        final double f = xx + ymthe * (ymthe - 2 *cothe);
        return f;
    }

    /**
     * Returns the x value in radians.
     * @return the x value
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y value in radians.
     * @return the y value
     */
    public double getY() {
        return y;
    }
    
}
