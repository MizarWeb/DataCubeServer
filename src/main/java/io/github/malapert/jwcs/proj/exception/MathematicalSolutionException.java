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
package io.github.malapert.jwcs.proj.exception;

/**
 * Exception.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 */
public class MathematicalSolutionException extends JWcsError {
    
    public MathematicalSolutionException() {
        super();
    }    
    
    public MathematicalSolutionException(final String message) {
        super(message);
    }
    
    public MathematicalSolutionException(final Throwable cause) {
        super(cause);
    }
    
    public MathematicalSolutionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
