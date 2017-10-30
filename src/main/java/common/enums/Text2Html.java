/**
 * 
 */
package common.enums;

/**
 * @author vincent.cephirins
 * 
 */
public enum Text2Html {
	LT("<", "&lt;"),
	GT(">", "&gt;"),
	EACUTE("é", "&eacute;"),
	EGRAVE("è", "&egrave;"),
	AGRAVE("à", "&agrave;");

	private String sequence;
	private String replace;

	Text2Html(String regex, String replace) {
		this.sequence = regex;
		this.replace = replace;
	}

	/**
	 * Retourne La chaine à remplacer
	 * 
	 */
	public String toString() {
		return sequence;
	}

	/**
	 * Retourne la liste des sequences à remplacer
	 * 
	 */
	static public String[] listSequences() {
		String[] result = new String[Text2Html.values().length];
		int idx = 0;
		for(Text2Html elt : Text2Html.values()) {
			result[idx++] = elt.toString();
		}
		return result;
	}

	/**
	 * @param source String to convert
	 * @return A string to html compliant
	 */
	static public String replace(String source) {
		if (source == null) return null;
		String result = null;
		for (Text2Html elt : Text2Html.values()) {
			result = source.replaceAll(elt.sequence, elt.replace);
		}
		return result;
	}

}
