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

/**
 * Exception.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 */
public class JWcsException extends Exception {
    private final static long serialVersionUID = -4269837119668785840L;

    /**
     * Creates an exception.
     */
    public JWcsException() {
        super();
    }

    /**
     * Creates an exception based on a message.
     *
     * @param message message
     */
    public JWcsException(final String message) {
        super(message);
    }

    /**
     * Creates an exception based on a cause.
     *
     * @param cause cause
     */
    public JWcsException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates an exception based on a message and a cause.
     *
     * @param message message
     * @param cause cause
     */
    public JWcsException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
