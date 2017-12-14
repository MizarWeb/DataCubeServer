/**
 * 
 */
package common.enums;

/**
 * @author vincent.cephirins
 * 
 */
public enum CubeType {
    /**
     * FITS (default)
     */
    FITS("fits"),
    /**
     * netCDF
     */
    NETCDF("netCDF"),
    /**
     * Mizar
     */
    MIZAR("mizar");

    private String name;

    CubeType(String name) {
        this.name = name;
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
