package image_char_matching;

import java.util.*;

/**
 * The SubImgCharMatcher class is responsible to hold a set of characters and match them a brightness value
 * between 0 and 1.
 */
public class SubImgCharMatcher {
    //fields
    private final TreeMap<Double, PriorityQueue<Character>> charsBrightness;
    private double max_brightness, min_brightness;

    /**
     * Constructs a SubImgCharMatcher object with the given character set.
     *
     * @param charset The character set to hold.
     */
    public SubImgCharMatcher(char[] charset) {
        //setting values that will change in the first iterations
        max_brightness = 0;
        min_brightness = 1;
        charsBrightness = new TreeMap<>();
        for (char c: charset) {
            addChar(c);
        }
    }

    /**
     * Retrieves all characters.
     * Characters are returned in ascending ASCII order.
     *
     * @return an ordered set of the characters
     */
    public Set<Character> getChars() {
        // Create a TreeSet to store characters in ascending ASCII order
        Set<Character> allChars = new TreeSet<>();
        for (PriorityQueue<Character> chars : charsBrightness.values()) {
            allChars.addAll(chars);
        }
        return allChars;
    }



    /**
     * Finds the char with the closest brightness to a given value.
     * @param brightness value between 0 and 1
     * @return a char in the set that best represent the brightness value.
     */
    public char getCharByImageBrightness(double brightness) {
        // corrects the brightness to the range of brightnesses of the chars
        double correctedBrightness = correctBrightness(brightness);
        Double closestKey = findClosestKey(correctedBrightness);
        //finds the best char with this brightness
        PriorityQueue<Character> minHeap = charsBrightness.get(closestKey);
        //can throw NullPointerException but we assume it's never empty
        return minHeap.peek();
    }

    /**
     * finds the brightness value closest to a given value
     * @param brightness Value between 0 and 1
     * @return A char with the brightness closest to brightness
     */
    private Double findClosestKey(double brightness) {
        Double floorKey = charsBrightness.floorKey(brightness);
        Double ceilingKey = charsBrightness.ceilingKey(brightness);

        if (floorKey == null) {
            return ceilingKey;
        }
        else {
            if (ceilingKey == null) {
                return floorKey;
            }
            else {
                double floorDiff = Math.abs(brightness - floorKey);
                double ceilingDiff = Math.abs(brightness - ceilingKey);
                //selects the value with the smallest absolute distance.
                if (floorDiff <= ceilingDiff) {
                    return floorKey;
                } else {
                    return ceilingKey;
                }
            }
        }
    }

    /**
     * Apllies a linear transformation to brightness, moving it from the range [0,1] to
     * [min_brightness, max_brightness]
     * @param brightness value in range [0,1]
     * @return value in range [min_brightness, max_brightness]
     */
    private double correctBrightness(double brightness) {
        return brightness * (max_brightness - min_brightness) + min_brightness;
    }

    /**
     * Adds a char to the database
     * @param c char to add
     */
    public void addChar(char c) {
        double charBrightness = determineBrightnessValue(c);
        //checks for new max or min
        if(charBrightness > max_brightness) {
            max_brightness = charBrightness;
        }
        if (charBrightness < min_brightness) {
            min_brightness = charBrightness;
        }
        //checks if this brightness value already exist
        if (charsBrightness.containsKey(charBrightness)) {
            charsBrightness.get(charBrightness).add(c);
        }
        else {
            // If the key doesn't exist, create a new priority queue and adds the character to it
            PriorityQueue<Character> characterPriorityQueue = new PriorityQueue<>();
            characterPriorityQueue.add(c);
            charsBrightness.put(charBrightness, characterPriorityQueue);
        }
    }

    /**
     * removes a character from the database
     * @param c char to remove
     */
    public void removeChar(char c) {
        double charBrightness = determineBrightnessValue(c);
        if (charsBrightness.containsKey(charBrightness)) {
            PriorityQueue<Character> charPriorityQueue = charsBrightness.get(charBrightness);
            charPriorityQueue.remove(c);
            if (charPriorityQueue.isEmpty()) {
                charsBrightness.remove(charBrightness);
                //checks if min or max need to be updated
                if(charBrightness == min_brightness) {
                    min_brightness = (charsBrightness.isEmpty()) ? Integer.MAX_VALUE :charsBrightness.firstKey();
                }
                if(charBrightness == max_brightness) {
                    max_brightness = (charsBrightness.isEmpty()) ? Integer.MIN_VALUE :charsBrightness.lastKey();
                }
            }
        }
    }

    /**
     * determine a brightness value of a given char between 0 and 1. the brightness is the ratio between
     * the white pixels and all the cells of a char image in font 'courier new'.
     * @param c char to determine brightness
     * @return value between 0 and 1
     */
    private double determineBrightnessValue(char c) {
        //gets the char pixels in courier font
        boolean[][] boolRepresentation = CharConverter.convertToBoolArray(c);
        int arrSize = boolRepresentation.length * boolRepresentation[0].length;
        int count = 0;
        for (boolean[] booleanLine : boolRepresentation) {
            for (boolean booleanCell : booleanLine) {
                if (booleanCell) {
                    count++;
                }
            }
        }
        return count /(double) arrSize;
    }
}
