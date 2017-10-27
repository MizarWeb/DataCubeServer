/**
 * 
 */
package common.enums;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author vincent.cephirins
 * 
 */
public enum FitsKeys {
    COMMENT(),
    CONTINUE(),
    DEFAULT(".*", Pattern.CASE_INSENSITIVE);

    private String regex;
    private Pattern pattern;

    FitsKeys() {
    	this.regex = this.name(); 
    }
    
    FitsKeys(String regex, int flags) {
        try {
            this.regex = regex;
            this.pattern = Pattern.compile(regex, flags);
        }
        catch (PatternSyntaxException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Retourne l'expression régulière associé au type
     * 
     * @return l'expression régulière du type demandé
     */
    public String toString() {
        return regex;
    }

    public static FitsKeys getType(String name) {
        for (FitsKeys type : FitsKeys.values()) {
            if (type.pattern != null) {
                if (type.pattern.matcher(name).matches()) {
                    return type;
                }
            }
            else if (type.toString().equalsIgnoreCase(name)) {
                return type;
            }
        }
        // Si non trouvé alors exception
        return valueOf(name);
    }
   
}
