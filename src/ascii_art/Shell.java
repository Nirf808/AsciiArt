package ascii_art;

import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;
import image.ImageParser;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * The Shell class represents a command-line shell for interacting with ASCII art generation functionalities.
 * It allows users to perform various operations such as changing characters used for rendering,
 * adjusting image resolution, loading different images, selecting output formats,
 * and generating ASCII art from images.
 */
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
    private static final String REMOVE_ERR_MSG = "Did not remove due to incorrect format.";
    private static final String RES_INCORRECT_FORMAT = "Did not change resolution due to incorrect format.";
    private static final String RES_EXCEEDED_BOUNDARIES = "Did not change resolution due to exceeding " +
            "boundaries.";
    private static final String RESOLUTION_CHANGED_MSG = "Resolution set to <?>.";
    private static final String IMAGE_ERR = "Did not execute due to problem with image file.";
    private static final String OUTPUT_ERR = "Did not change output method due to incorrect format.";
    private static final String EMPTY_CHARS_ERR = "Did not execute. Charset is empty.";
    private static final String COMMAND_NOT_FOUND_ERR = "Did not execute due to incorrect command.";

    private static final char[] DEFAULT_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final String DEFAULT_IMAGE_NAME = "cat.jpeg";
    private static final int DEFAULT_RESOLUTION = 128;
    private static final int FIRST_PRINTABLE_CHAR = 32;
    private static final int LAST_PRINTABLE_CHAR = 126;
    private static final String NEW_LINE = ">>> ";
    private static final String CONSOLE_OUT = "console";
    private static final String HTML_OUT = "html";
    private static final String RES_UP = "up";
    private static final String RES_DOWN = "down";
    private static final String ALL_CHARS = "all";
    private static final String SPACE_CHAR =  "space";


    private final AsciiOutput CONSOLE_OUTPUT = new ConsoleAsciiOutput(); //single instance of console output
    private final AsciiOutput HTML_OUTPUT = new HtmlAsciiOutput("out.html", "Courier New");
    // single instance of html output

    private final SubImgCharMatcher charMatcher;
    private int resolution = DEFAULT_RESOLUTION;
    private Image image;
    private AsciiOutput output;
    private char[][] imageAsAscii;
    private HashMap<Integer, AsciiArtAlgorithm> algoMap;

    /**
     * Constructs a new Shell instance with default settings.
     * It initializes the image, character set, and output method.
     */
    public Shell() {
        imageAsAscii = null;
        output = CONSOLE_OUTPUT;
        charMatcher = new SubImgCharMatcher(DEFAULT_CHARS);
        algoMap = new HashMap<>();
        try {
            image = new Image(DEFAULT_IMAGE_NAME);
        } catch (IOException e) {
            System.out.println("cannot load default image");
        }
    }

    /**
     * Runs the shell, allowing users to interact with ASCII art generation functionalities through
     * command-line inputs. It reads user input until the 'exit' command is entered.
     * Support commands for displaying characters, adding or removing characters from the character set,
     * changing image resolution, loading different images, selecting output formats,
     * and generating ASCII art from images.
     */
    public void run() {
        System.out.print(NEW_LINE);
        String userInput = KeyboardInput.readLine();
        while (notExit(userInput)) {
            String[] commandAndArgs = userInput.split(" ");
            String command = commandAndArgs[0];
            try {
                switch (command) {
                    case CHARS:
                        printChars();
                        break;
                    case ADD:
                        changeChar(commandAndArgs, charMatcher::addChar, ADD_ERR_MSG);
                        break;
                    case REMOVE:
                        changeChar(commandAndArgs, charMatcher::removeChar, REMOVE_ERR_MSG);
                        break;
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
                    default:
                        System.out.println(COMMAND_NOT_FOUND_ERR);
                        break;
                }
            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                System.out.println(e.getMessage());
            }
            System.out.print(NEW_LINE);
            userInput = KeyboardInput.readLine();
        }
    }

    private boolean notExit(String command) {
        return !command.equals(EXIT);
    }

    private void printChars() {
        for (char c : charMatcher.getChars()) {
            System.out.print(c + " ");
        }
        System.out.println();
    }

    private void changeChar(String[] args, Consumer<Character> f, String err_msg)
            throws IllegalArgumentException {
        if (args.length != 2) {
            throw new IllegalArgumentException(err_msg);
        }

        String arg = args[1];
        switch (arg) {
            case ALL_CHARS:
                for (char c : getAllPossibleChars()) {
                    f.accept(c);
                }
                break;
            case SPACE_CHAR:
                f.accept(' ');
                break;
            default:
                if (arg.length() == 1) {
                    f.accept(arg.charAt(0));
                    return;
                }
                if (rangeOfCharsValidFormat(arg)) {
                    int from = Math.min(arg.charAt(0), arg.charAt(2));
                    int to = Math.max(arg.charAt(0), arg.charAt(2));
                    for (int i = from; i <= to; i++) {
                        f.accept((char) i);
                    }
                    return;
                }
                throw new IllegalArgumentException(err_msg);
        }
    }


    private boolean rangeOfCharsValidFormat(String arg) {
        return arg.length() == 3 && arg.charAt(1) == '-';
    }

    private char[] getAllPossibleChars() {
        char[] chars = new char[1 + LAST_PRINTABLE_CHAR - FIRST_PRINTABLE_CHAR];
        for (int i = FIRST_PRINTABLE_CHAR; i <= LAST_PRINTABLE_CHAR; i++) {
            chars[i - FIRST_PRINTABLE_CHAR] = (char) i;
        }
        return chars;
    }

    private void changeResolution(String[] args) throws IllegalArgumentException {
        if (args.length != 2 || (!args[1].equals(RES_UP) && !args[1].equals(RES_DOWN))) {
            throw new IllegalArgumentException(RES_INCORRECT_FORMAT);
        }
        switch (args[1]) {
            case RES_UP:
                if (2 * resolution > imageWidth() || 2 * resolution > imageHeight()) {
                    throw new IllegalArgumentException(RES_EXCEEDED_BOUNDARIES);
                }
                resolution *= 2;
                break;
            case RES_DOWN:
                if (resolution / 2 < getMinCharsInRow()) {
                    throw new IllegalArgumentException(RES_EXCEEDED_BOUNDARIES);
                }
                resolution /= 2;
                break;
        }
        System.out.println(RESOLUTION_CHANGED_MSG.replace("<?>", String.valueOf(resolution)));
    }

    private int getMinCharsInRow() {
        return Math.max(1, imageWidth() / imageHeight());
    }

    private void changeImage(String[] args) throws IllegalArgumentException, IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException(IMAGE_ERR);
        }
        String path = args[1];
        try {
            image = new Image(path);
            algoMap = new HashMap<>();
        } catch (IOException e) {
            throw new IOException(IMAGE_ERR);
        }
    }


    private int imageHeight() {
        return ImageParser.getPaddedImageSize(image)[0];
    }

    private int imageWidth() {
        return ImageParser.getPaddedImageSize(image)[1];
    }

    private void changeOutput(String[] args) throws IllegalArgumentException {
        if (args.length != 2 || (!args[1].equals(HTML_OUT) && !args[1].equals(CONSOLE_OUT))) {
            throw new IllegalArgumentException(OUTPUT_ERR);
        }
        switch (args[1]) {
            case CONSOLE_OUT:
                output = CONSOLE_OUTPUT;
                break;
            case HTML_OUT:
                output = HTML_OUTPUT;
                break;
        }
    }

    /**
     * Runs the ASCII art algorithm to convert the image into ASCII art and outputs the result.
     * If the character set is empty, throws an IllegalStateException.
     * If the image or resolution has changed since the last run, re-runs the algorithm.
     * If only the character set has changed, re-runs the algorithm without changing the image.
     * Outputs the ASCII art result using the configured output method.
     *
     * @throws IllegalStateException if the character set is empty
     */
    private void runAlgorithm() throws IllegalStateException {
        if (charMatcher.getChars().isEmpty()) {
            throw new IllegalStateException(EMPTY_CHARS_ERR);
        }
        if (algoMap.containsKey(resolution)){
            imageAsAscii = algoMap.get(resolution).run();
        } else {
            AsciiArtAlgorithm algo = new AsciiArtAlgorithm(image, resolution, charMatcher);
            algoMap.put(resolution, algo);
            imageAsAscii = algo.run();
        }
        output.out(imageAsAscii);
    }

    /**
     * Entry point to start the ASCII art shell application.
     *
     * @param args the command-line arguments (not used)
     */
    public static void main(String[] args) {
        Shell shell = new Shell();
        shell.run();
    }
}
