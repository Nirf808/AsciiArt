package ascii_art;

import image.Image;
import image.ImageParser;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;

public class AsciiArtAlgorithm {
    //fields
    private double[][] image;
    private int resolution;
    private SubImgCharMatcher matcher;

    public AsciiArtAlgorithm(Image image, int resolution, SubImgCharMatcher matcher) {
        image = ImageParser.padImage(image);
        this.image = ImageParser.getImageParts(image, resolution);
        this.resolution = resolution;
        this.matcher = matcher;
    }

    public char[][] run() {
        char[][] ascii = new char[image.length][image[0].length];
        for (int row = 0; row < ascii.length; row++) {
            for (int col = 0; col < ascii[row].length; col++) {
                ascii[row][col] = matcher.getCharByImageBrightness(image[row][col]);
            }
        }
        return ascii;
    }
}
