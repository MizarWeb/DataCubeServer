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
package io.github.malapert.jwcs;

import io.github.malapert.jwcs.proj.exception.JWcsError;
import io.github.malapert.jwcs.proj.exception.JWcsException;
import java.util.Iterator;
import java.util.Map;

/**
 * A class allowing to compute the WCS by initializing the WCS structure by
 * reading a map containing the WCS keywords.
 *
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0 
 */
public class WcsNumericalMap extends AbstractJWcs {

    /**
     * List of WCS keywords provided by the user.
     */
    private Map keywords;

    /**
     * Clones a WcsNumericalMap.
     * @param wcs WcsNumericalMap object
     */
    public WcsNumericalMap(final WcsNumericalMap wcs) {
        setKeywords(wcs.keywords);
    }

    /**
     * Creates AbstractJWcs based on a map.
     * @param keywords map of keywords
     */
    public WcsNumericalMap(final Map keywords) {
        this.keywords = keywords;
    }

    @Override
    public void doInit() throws JWcsException {
        super.init();
    }

    @Override
    public int wcsaxes() {
        return this.getValueAsInt("NAXIS");
    }

    @Override
    public boolean hasKeyword(final String keyword) {
        return this.getKeywords().containsKey(keyword);
    }

    @Override
    public int getValueAsInt(final String keyword) {
        if (hasKeyword(keyword)) {
            return (int) this.keywords.get(keyword);
        } else {
            throw new JWcsError("cannot get "+keyword);
        }
    }

    @Override
    public double getValueAsDouble(final String keyword) {
        if (hasKeyword(keyword)) {
            return (double) this.keywords.get(keyword);
        } else {
            throw new JWcsError("cannot get "+keyword);
        }
    }

    @Override
    public float getValueAsFloat(final String keyword) {
        if (hasKeyword(keyword)) {
            return (float) this.keywords.get(keyword);
        } else {
            throw new JWcsError("cannot get "+keyword);
        }
    }

    @Override
    public String getValueAsString(final String keyword) {
        return (String) this.getKeywords().getOrDefault(keyword, null);
    }

    @Override
    public Iterator iterator() {
        return this.getKeywords().keySet().iterator();
    }

    /**
     * Returns all the keywords.
     *
     * @return the keywords
     */
    private Map getKeywords() {
        return keywords;
    }

    /**
     * Sets the whole map.
     *
     * @param keywords the keywords to set
     */
    private void setKeywords(final Map keywords) {
        this.keywords = keywords;
    }

    @Override
    protected void checkWcs() throws JWcsException {
        //do nothing
    }
}
