package ascii_art;

import image.Image;
import image.ImageParser;
import image_char_matching.SubImgCharMatcher;

/**
 * The AsciiArtAlgorithm class converts an image into ASCII art using a specified resolution
 * and set of characters.
 */
public class AsciiArtAlgorithm {
    //fields
    private final double[][] image; // Stores the pixel values of the image
    private final SubImgCharMatcher matcher; // Used to match image brightness to characters

    /**
     * Constructs an AsciiArtAlgorithm object with the specified image, resolution, and set of characters.
     *
     * @param image      The image to convert to ASCII art.
     * @param resolution The resolution used for the ASCII art (number of characters per row
     *                   in the ASCII image).
     * @param matcher    The set of characters to build the image with.
     */
    public AsciiArtAlgorithm(Image image, int resolution, SubImgCharMatcher matcher) {
        // Pad the image to ensure proper processing
        image = ImageParser.padImage(image);

        // Initialize fields
        this.image = ImageParser.getImageParts(image, resolution);
        // Stores the resolution used for ASCII art conversion
        this.matcher = matcher;
    }

    /**
     * Runs the ASCII art conversion algorithm on the provided image.
     *
     * @return A 2D char array representing the ASCII art version of the image.
     */
    public char[][] run() {
        char[][] ascii = new char[image.length][image[0].length];
        for (int row = 0; row < ascii.length; row++) {
            for (int col = 0; col < ascii[row].length; col++) {
                // Get the character representation of the image brightness at the current pixel
                ascii[row][col] = matcher.getCharByImageBrightness(image[row][col]);
            }
        }
        return ascii;
    }
}