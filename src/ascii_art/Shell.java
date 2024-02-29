package ascii_art;

import image_char_matching.SubImgCharMatcher;

public class Shell {
    private static final String EXIT = "exit";
    private static final String CHARS = "chars";
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String RES = "res";
    private static final String IMAGE = "image";
    private static final String OUTPUT = "output";
    private static final String ASCII_ART = "asciiArt";

    private static final char[] DEFAULT_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final String IMAGE_NAME = "cat.jpeg";
    private static final int DEFAULT_RESOLUTION = 128;

    private SubImgCharMatcher chars;
    private String imageName = IMAGE_NAME;
    private int resolution = DEFAULT_RESOLUTION;


    public Shell(){
        chars = new SubImgCharMatcher(DEFAULT_CHARS);
    }

    public void run(){
        String userInput = KeyboardInput.readLine();
        while (notExit(userInput)){
            String[] commandAndParams = userInput.split(" ");
            String command = commandAndParams[0];
            switch (command){
                case CHARS:
                    printChars();
                    break;
                case ADD:
                    addChar(commandAndParams);
                case REMOVE:
                    break;
                case RES:
                    break;
                case IMAGE:
                    break;
                case OUTPUT:
                    break;
                case ASCII_ART:
                    break;
            }
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

    private void addChar(String[] params){
        if (params.length != 2){
            System.out.println("Exception!!!!");
        }
        String arg = params[1];
        switch (arg){
            case "all":
//                addAllPossibleChars();
                break;
            case "space":
                chars.addChar(' ');
                break;
            default:
                if (arg.length() == 1){
                    chars.addChar(arg.charAt(0));
                    return;
                }
                if (rangeOfCharsValidFormat(arg)){
                    for (int i = arg.charAt(0); i < arg.charAt(2); i++){
                        chars.addChar((char)i);
                    }
                }

        }
    }

    private boolean rangeOfCharsValidFormat(String arg){
        return arg.length() == 3 && arg.charAt(1) == '-'
                && arg.charAt(2) > arg.charAt(0);
    }
}
