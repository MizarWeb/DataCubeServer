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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reads data from a HeaderFits map.
 * @author Jean-Christophe Malapert (jcmalapert@gmail.com)
 * @version 2.0
 */
public class HeaderFitsReader {

    /**
     * Separator between keyword/value.
     */
    private final static String SEPARATOR = "=";

    /**
     * Reader.
     */
    private final Reader source;

    /**
     * Constructs a HeaderFitsReader based on a reader.
     * @param source reader
     */
    public HeaderFitsReader(final Reader source) {
        this.source = source;
    }
    
    /**
     * Constructs a HeaderFitsReader based on a file.
     * @param file file
     * @throws FileNotFoundException File not found
     */
    public HeaderFitsReader(final File file) throws FileNotFoundException {
        final Reader reader = new FileReader(file);        
        this.source = reader;
    }    
    
    /**
     * Constructs a HeaderFitsReader based on a URI.
     * @param uri uri of the file
     * @throws FileNotFoundException  File not found
     */
    public HeaderFitsReader(final URI uri) throws FileNotFoundException {
        final Reader reader = new FileReader(new File(uri));
        this.source = reader;
    }            
    
    /**
     * Constructs a HeaderFitsReader based on a URL.     
     * @param url URL
     * @throws IOException File not found
     */
    public HeaderFitsReader(final URL url) throws IOException  {
        final Reader reader = new InputStreamReader(url.openStream());
        this.source = reader;
    }      

    /**
     * Constructs a HeaderFitsReader based on a filename.     
     * @param filename filename
     * @throws URISyntaxException URI syntax problem
     * @throws IOException File not found
     */
    public HeaderFitsReader(final String filename) throws URISyntaxException, IOException  {
        final Path path = Paths.get(new URI(filename));
        final Reader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"));
        this.source = reader;
    }

    /**
     * Reads keywords from a reader.
     * @return the list of (keyword,value)
     */
    public List<List<String>> readKeywords() {
        try (BufferedReader reader = new BufferedReader(source)) {
            return reader.lines()
                    .filter(line -> line.contains("="))
                    .map(line -> Arrays.asList(line.split(SEPARATOR)))
                    .map(line -> {
                        final String keyword = line.get(0);
                        final String[]valComm = line.get(1).split(" /");
                        final String value = valComm[0].replace("'", "");
                        return Arrays.asList(keyword.trim(),value.trim());
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
