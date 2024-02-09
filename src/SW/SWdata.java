package SW;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SWdata {
    public static String version = "v0.2.149 (dev build)";

    public static BufferedImage getIcon(){
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("gfx/icon.png"));
        } catch (IOException ignored) {}
        return image;
    }
}
