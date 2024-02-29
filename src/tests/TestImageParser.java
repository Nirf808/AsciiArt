package tests;

import image.Image;
import image.ImageParser;

import java.io.IOException;

public class TestImageParser {
    public static void main(String[] args) throws IOException {
        Image im = new Image("cat.jpeg");
        System.out.println(im.getHeight());
        System.out.println(im.getWidth());
        Image padded = ImageParser.padImage(im);
        System.out.println(padded.getHeight());
        System.out.println(padded.getWidth());
    }
}
