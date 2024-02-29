package ascii_art;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;
import java.util.function.Consumer;

public class Shell {
    private static final String EXIT = "exit";
    private static final String CHARS = "chars";
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String RES = "res";
    private static final String IMAGE = "image";
    private static final String OUTPUT = "output";
    private static final String ASCII_ART = "asciiArt";
    private static final String ADD_ERR_MSG = "Did not add due to incorrect format.";
    private static final String REMOVE_ERR_MSG = "Did not remove due to incorrect format";
    private static final String RES_INCORRECT_FORMAT = "Did not change resolution due to incorrect format.";
    private static final String RES_EXCEEDED_BOUNDARIES = "Did not change resolution due to exceeding " +
            "boundaries.";
    private static final String RESOLUTION_CHANGED_MSG = "Resolution set to <?>.";
    private static final String IMAGE_ERR = "Did not execute due to problem with image file.";
    private static final String OUTPUT_ERR = "Did not change output method due to incorrect format.";
    private static final String EMPTY_CHARS_ERR = " Did not execute. Charset is empty.";

    private static final char[] DEFAULT_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final String IMAGE_NAME = "cat.jpeg";
    private static final int DEFAULT_RESOLUTION = 128;
    private static final int FIRST_PRINTABLE_CHAR = 32;
    private static final int LAST_PRINTABLE_CHAR = 132;

    private AsciiOutput CONSOLE = new ConsoleAsciiOutput(); //single instance of console output
    private AsciiOutput HTML = new HtmlAsciiOutput("out.html", "Courier New"); // single instance of html
    // output
    private SubImgCharMatcher chars;
    private String imageName = IMAGE_NAME;
    private int resolution = DEFAULT_RESOLUTION;
    private Image image;
    private boolean imageChanged;
    private AsciiOutput output;
    private int lastRunResolution;
    private char[][] imageAsAscii;

    public Shell(){
        imageAsAscii = null;
        lastRunResolution = 0;
        imageChanged = true;
        output = CONSOLE; // todo: is this the right default?
        chars = new SubImgCharMatcher(DEFAULT_CHARS);
        try {
            image = new Image(imageName);
        }
        catch (IOException e){
            System.out.println("impossibe");
        }
    }

    public void run(){
        String userInput = KeyboardInput.readLine();
        while (notExit(userInput)){
            String[] commandAndArgs = userInput.split(" ");
            String command = commandAndArgs[0];
            switch (command){
                case CHARS:
                    printChars();
                    break;
                case ADD:
                    changeChar(commandAndArgs, chars::addChar, ADD_ERR_MSG);
                    break;
                case REMOVE:
                    changeChar(commandAndArgs, chars::removeChar, REMOVE_ERR_MSG);
                case RES:
                    changeResolution(commandAndArgs);
                    break;
                case IMAGE:
                    changeImage(commandAndArgs);
                    break;
                case OUTPUT:
                    changeOutput(commandAndArgs);
                    break;
                case ASCII_ART:
                    runAlgorithm();
                    break;
            }
            userInput = KeyboardInput.readLine();
        }
    }

    private boolean notExit(String command) {
        return !command.equals(EXIT);
    }

    private void printChars(){
        for (char c: chars.getChars()){
            System.out.print(c + " ");
        }
    }

    private void changeChar(String[] args, Consumer<Character> f, String err_msg) {
        if (args.length != 2) {
            System.out.println(err_msg);
            return;
        }

        String arg = args[1];
        switch (arg) {
            case "all":
                for (char c: getAllPossibleChars()){
                    f.accept(c);
                }
                break;
            case "space":
                f.accept(' ');
                break;
            default:
                if (arg.length() == 1) {
                    f.accept(arg.charAt(0));
                    return;
                }
                if (rangeOfCharsValidFormat(arg)) {
                    for (int i = arg.charAt(0); i < arg.charAt(2); i++) {
                        f.accept((char) i);
                    }
                }
        }
    }


    private boolean rangeOfCharsValidFormat(String arg){
        return arg.length() == 3 && arg.charAt(1) == '-'
                && arg.charAt(2) > arg.charAt(0);
    }

    private char[] getAllPossibleChars(){
        char[] chars = new char[1 + LAST_PRINTABLE_CHAR - FIRST_PRINTABLE_CHAR];
        for (int i = FIRST_PRINTABLE_CHAR; i <= LAST_PRINTABLE_CHAR; i++){
            chars[i - FIRST_PRINTABLE_CHAR] = (char)i;
        }
        return chars;
    }

    private void changeResolution(String[] args){
        if (args.length != 2 || (!args[1].equals("up") && !args[1].equals("down"))){
            System.out.println(RES_INCORRECT_FORMAT);
            return;
        }
        switch (args[1]){
            case "up":
                if (2 * resolution > image.getWidth() || 2 * resolution > image.getHeight()){
                    System.out.println(RES_EXCEEDED_BOUNDARIES);
                    return;
                }
                resolution *= 2;
                break;
            case "down":
                if (resolution / 2 < getMinCharsInRow()){
                    System.out.println(RES_EXCEEDED_BOUNDARIES);
                    return;
                }
                resolution /= 2;
                break;
        }
        System.out.println(RESOLUTION_CHANGED_MSG.replace("<?>", String.valueOf(resolution)));
    }

    private int getMinCharsInRow(){
        return Math.max(1, image.getWidth() / image.getHeight());
    }

    private void changeImage(String[] args){
        if (args.length != 2){
            System.out.println(IMAGE_ERR); //todo: this message is not the right one
            return;
        }
        String path = args[1];
        try {
            image = new Image(path);
            imageChanged = true;
        }
        catch (IOException e){
            System.out.println(IMAGE_ERR);
        }
    }

    private void changeOutput(String[] args){
        if (args.length != 2 || (!args[1].equals("html") && !args[1].equals("console"))){
            System.out.println(OUTPUT_ERR); //todo: this message is not the right one
            return;
        }
        switch (args[1]){
            case "console":
                output = CONSOLE;
                break;
            case "html":
                output = HTML;
                break;
        }
    }

    private void runAlgorithm(){
        if (chars.getChars().isEmpty()){
            System.out.println(EMPTY_CHARS_ERR);
            return;
        }

        if (imageChanged || resChanged()){
            AsciiArtAlgorithm asciiArtAlgorithm = new AsciiArtAlgorithm(image, resolution, chars);
            imageAsAscii = asciiArtAlgorithm.run();
            output.out(imageAsAscii);
        }
        output.out(imageAsAscii);
    }

    private boolean resChanged(){
       return lastRunResolution != resolution;
    }

    public static void main(String[] args) {
        Shell shell = new Shell();
        shell.run();
    }
}
