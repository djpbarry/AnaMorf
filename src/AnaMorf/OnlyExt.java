package AnaMorf;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filter used to identify a particular file extension in a directory.
 *
 * @author   David J Barry <davejpbarry@gmail.com>
 * @version  01SEP2010
 */
public class OnlyExt implements FilenameFilter {
    private String extension;

    /**
     * The argument <i>ext</i> represents the filename extension of interest.
     */
    public OnlyExt (String ext) {
        extension = ("."+ext).toUpperCase();
    }

    /**
     * Returns true if <i>name</i> ends with <i>ext</i>, false otherwise.
     */
    public boolean accept(File dir, String name){
        name = name.toUpperCase();
        if (name.endsWith(extension)) return true;
        else if (extension.equals(".TIF") && name.endsWith("TIFF")) return true;
        else if (extension.equals(".JPG") && name.endsWith("JPEG")) return true;
        return false;
    }
}
