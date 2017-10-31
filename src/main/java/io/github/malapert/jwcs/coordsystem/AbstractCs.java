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
package io.github.malapert.jwcs.coordsystem;

/**
 *
 * @author malapert
 */
public abstract class AbstractCs {   

    private Axis[] axes;
    private int dimension;
    
    protected AbstractCs(int dimension) {
        this.dimension = dimension;
        this.axes = new Axis[dimension];
    }

    /**
     * @return the axes
     */
    public Axis[] getAxes() {
        return axes;
    }

    /**
     * @return the dimension
     */
    public int getDimension() {
        return dimension;
    }
    
    

    public enum Unit {
        DEG,
        RAD
    }
    
    public enum AxisDirection {
        NORTH,
        SOUTH,
        EAST,
        WEST,
        UP,
        DOWN
    }
    
    public static class Axis {
        
        private String abbreviation;
        private String name;
        private AxisDirection direction;
        public Unit unit;        
        
        public Axis(final String abbreviation, final String name, final AxisDirection direction, final Unit unit) {
            this.abbreviation = abbreviation;
            this.name = name;
            this.direction = direction;
            this.unit = unit;
        }    

        /**
         * @return the abbreviation
         */
        public String getAbbreviation() {
            return abbreviation;
        }

        /**
         * @param abbreviation the abbreviation to set
         */
        public void setAbbreviation(String abbreviation) {
            this.abbreviation = abbreviation;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the direction
         */
        public AxisDirection getDirection() {
            return direction;
        }

        /**
         * @param direction the direction to set
         */
        public void setDirection(AxisDirection direction) {
            this.direction = direction;
        }

        /**
         * @return the unit
         */
        public Unit getUnit() {
            return unit;
        }

        /**
         * @param unit the unit to set
         */
        public void setUnit(Unit unit) {
            this.unit = unit;
        }
        
        
    }
    
    public enum CoordinateSystem {
        CARTESIAN2D,
        CARTESIAN3D,
        SPHERICAL2D,
        SPHERICAL3D
    }
    
    
}
