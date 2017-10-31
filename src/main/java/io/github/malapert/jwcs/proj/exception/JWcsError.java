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
package io.github.malapert.jwcs.proj.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JWcs Error.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 */
public class JWcsError extends RuntimeException {

    /**
     * Logger.
     */
    private final static Logger LOG = Logger.getLogger(JWcsError.class.getName());
    private final static long serialVersionUID = 2694328832860861046L;

    /**
     * Creates an error.
     */
    public JWcsError() {
        super();
        LOG.log(Level.SEVERE, "Undefined exception");
    }

    /**
     * Creates an error based on a message.
     *
     * @param message message
     */
    public JWcsError(final String message) {
        super(message);
        LOG.log(Level.SEVERE, message);        
    }

    /**
     * Creates an error based on a cause.
     *
     * @param cause cause
     */
    public JWcsError(final Throwable cause) {
        super(cause);
        LOG.log(Level.SEVERE, cause.getMessage());        
    }

    /**
     * Creates an error based on a message and a cause.
     *
     * @param message message
     * @param cause cause
     */
    public JWcsError(final String message, final Throwable cause) {
        super(message, cause);
        LOG.log(Level.SEVERE, message);        
    }
}
