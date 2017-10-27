/**
 * 
 */
package common.enums;

import java.util.Locale;

/**
 * @author vincent.cephirins
 * 
 */
public enum LangType {
    /**
     * ENGLISH (default)
     */
    ENGLISH(new Locale(""), "English"),
    /**
     * FRANCAIS
     */
    FRANCAIS(Locale.FRENCH, "Français");

    private Locale lc;
    private String name;

    LangType(Locale lc, String name) {
        this.lc = lc;
        this.name = name;
    }

    public Locale getLocale() {
        return lc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return name;
    }
}
