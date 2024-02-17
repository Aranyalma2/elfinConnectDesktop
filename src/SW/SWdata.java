// SWdata.java
package SW;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * The SWdata class contains static data and utility methods related to the application.
 */
public class SWdata {
    /**
     * The version information for the application.
     */
    public static String version = "v0.2.155 (dev build)";

    /**
     * Retrieves the application icon as a BufferedImage.
     *
     * @return The BufferedImage representing the application icon.
     */
    public static BufferedImage getIcon() {
        BufferedImage image = null;
        try {
            // Load the application icon image from the file "gfx/icon.png"
            image = ImageIO.read(new File("gfx/icon.png"));
        } catch (IOException ioException) {
            Log.logger.warning("Unable to load icon: [" +ioException.getMessage()+"]");
            // Just logging any exceptions during image loading (for simplicity)

        }
        return image;
    }
}
