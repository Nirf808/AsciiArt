package image_char_matching;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class SubImgCharMatcher {
    //fields
    private final TreeMap<Double, Set<Character>> charsBrightness;
    private double max_brightness, min_brightness;

    public SubImgCharMatcher(char[] charset) {
        max_brightness = 0;
        min_brightness = 1;
        charsBrightness = new TreeMap<>();
        for (char c: charset) {
            addChar(c);
        }
    }

    public char getCharByImageBrightness(double brightness) {
        double correctedBrightness = correctBrightness(brightness);
        Double closestKey = findClosestKey(correctedBrightness);
        Set<Character> set = charsBrightness.get(closestKey);
        return Collections.min(set);
    }

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

                if (floorDiff <= ceilingDiff) {
                    return floorKey;
                } else {
                    return ceilingKey;
                }
            }
        }
    }

    private double correctBrightness(double brightness) {
        return brightness * (max_brightness - min_brightness) + min_brightness;
    }


    public void addChar(char c) {
        double charBrightness = determineBrightnessValue(c);
        if(charBrightness > max_brightness) {
            max_brightness = charBrightness;
        }
        if (charBrightness < min_brightness) {
            min_brightness = charBrightness;
        }
        if (charsBrightness.containsKey(charBrightness)) {
            charsBrightness.get(charBrightness).add(c);
        }
        else {
            // If the key doesn't exist, create a new set and add the character to it
            Set<Character> charSet = new HashSet<>();
            charSet.add(c);
            charsBrightness.put(charBrightness, charSet);
        }
    }

    public void removeChar(char c) {
        double charBrightness = determineBrightnessValue(c);
        if (charsBrightness.containsKey(charBrightness)) {
            Set<Character> charSet = charsBrightness.get(charBrightness);
            charSet.remove(c);
            if (charSet.isEmpty()) {
                charsBrightness.remove(charBrightness);
                if(charBrightness == min_brightness) {
                    min_brightness = charsBrightness.firstKey();
                }
                if(charBrightness == max_brightness) {
                    max_brightness = charsBrightness.lastKey();
                }
            }
        }
    }

    private double determineBrightnessValue(char c) {
        boolean[][] boolRepresentation = CharConverter.convertToBoolArray(c);
        int arrSize = boolRepresentation.length * boolRepresentation[0].length;
        double count = 0.0;
        for (boolean[] booleanLine : boolRepresentation) {
            for (boolean booleanCell : booleanLine) {
                if (booleanCell) {
                    count++;
                }
            }
        }
        return count / arrSize;
    }
}
