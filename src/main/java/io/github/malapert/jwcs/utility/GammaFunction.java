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
 * GammaFunction function to solve for the Mollweide's projection.
 * 
 * <p>The following function must be solved:<br>
 * <code>v + sin(v) - PI * sin(theta) = 0</code><br>
 * with <code>v</code> the value to find.
 * 
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class GammaFunction implements UnivariateFunction {

    /**
     * theta value.
     */
    private double theta;

    /**
     * Initialize the constructor with <code>theta</code>=0.
     */
    public GammaFunction() {
        this.theta = 0;
    }
    
    /**
     * Sets the theta value.
     * @param theta the value of theta
     */
    public void setTheta(final double theta) {
        this.theta = theta;
    }

    /**
     * Evaluates the result for a given value.
     * 
     * @param d value to evaluate
     * @return the result
     */
    @Override
    public double value(final double d) {
        return d + FastMath.sin(d) - FastMath.PI * FastMath.sin(theta);
    }   
}
