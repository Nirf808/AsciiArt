package tests;

import ascii_art.AsciiArtAlgorithm;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;

public class testAlgorithm {
    public static void main(String[] args) throws IOException {
        boolean test1Result = testBoard();
        if(test1Result) {
            System.out.println("Test 1 succeed");
        } else {
            System.out.println("Test 1 failed");
        }
        testCat();
    }

    public static boolean testBoard() throws IOException {
        Image im = new Image("board.jpeg");
        int resolution = 2;
        char[] chars = {'m', 'o'};
        SubImgCharMatcher matcher = new SubImgCharMatcher(chars);
        AsciiArtAlgorithm alg = new AsciiArtAlgorithm(im, resolution,matcher);
        char[][] result = alg.run();
        printResult(result);
        return checkResult(result);
    }

    private static void printResult(char[][] result) {
        ConsoleAsciiOutput output = new ConsoleAsciiOutput();
        output.out(result);
    }

    private static boolean checkResult(char[][] result) {
        boolean ans;
        ans= result[0][0] == 'm';
        ans =  ans & result[0][1] == 'o';
        ans = ans & result[1][0] == 'o';
        ans = ans & result[1][1] == 'm';
        return ans;
    }

    public static void testCat() throws IOException {
        Image im = new Image("cat.jpeg");
        int resolution = 128;
        char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        SubImgCharMatcher matcher = new SubImgCharMatcher(chars);
        AsciiArtAlgorithm alg = new AsciiArtAlgorithm(im, resolution,matcher);
        char[][] result = alg.run();
        HtmlAsciiOutput output = new HtmlAsciiOutput("cat output.html", "Courier New");
        output.out(result);

    }
}
