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
     * FRANCAIS
     */
    NETCDF("netCDF");

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
