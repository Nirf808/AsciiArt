package tests;

import image.Image;
import image.ImageParser;

import java.io.IOException;

public class TestImageParser {
    public static void main(String[] args) throws IOException {
        testPadding();
        testSubImageGrayScale();
    }

    public static void testPadding() throws IOException {
        Image im = new Image("cat.jpeg");
        Image padded = ImageParser.padImage(im);
        assert padded.getHeight() == 1024;
        assert padded.getWidth() == 1024;
    }

    public static void testSubImageGrayScale() throws IOException {
        Image im = new Image("cat.jpeg");
        Image paddedIm = ImageParser.padImage(im);
        double[][] grays = ImageParser.getImageParts(paddedIm, 128);
        assert grays.length == 128;
        assert grays[0].length == 128;

    }
}
