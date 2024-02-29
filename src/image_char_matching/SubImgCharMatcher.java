package image_char_matching;

import java.util.*;

public class SubImgCharMatcher {
    //fields
    private TreeMap<Double, Set<Character>> charBrightness; //TODO return to private!
    private double max_brightness, min_brightness;

    public SubImgCharMatcher(char[] charset) {
        max_brightness = 0;
        min_brightness = 1;
        charBrightness = new TreeMap<>();
        for (char c: charset) {
            addChar(c);
        }
    }

    public Set<Character> getChars(){
        Set<Character> allChars = new TreeSet<>();  // treeSet in order to return chars in their ascii
        // order
        for (Set<Character> chars: charBrightness.values()){
            allChars.addAll(chars);
        }
        return allChars;
    }

    public char getCharByImageBrightness(double brightness) {
        double correctedBrightness = correctBrightness(brightness);
        Double closestKey = findClosestKey(correctedBrightness);
        Set<Character> set = charBrightness.get(closestKey);
        return Collections.min(set);
    }

    private Double findClosestKey(double brightness) {
        Double floorKey = charBrightness.floorKey(brightness);
        Double ceilingKey = charBrightness.ceilingKey(brightness);

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
        if (this.charBrightness.containsKey(charBrightness)) {
            this.charBrightness.get(charBrightness).add(c);
        }
        else {
            // If the key doesn't exist, create a new set and add the character to it
            Set<Character> charSet = new HashSet<>();
            charSet.add(c);
            this.charBrightness.put(charBrightness, charSet);
        }
    }

    public void removeChar(char c) {
        double charBrightness = determineBrightnessValue(c);
        if (this.charBrightness.containsKey(charBrightness)) {
            Set<Character> charSet = this.charBrightness.get(charBrightness);
            charSet.remove(c);
            if (charSet.isEmpty()) {
                this.charBrightness.remove(charBrightness);
                if(charBrightness == min_brightness) {
                    min_brightness = this.charBrightness.firstKey();
                }
                if(charBrightness == max_brightness) {
                    max_brightness = this.charBrightness.lastKey();
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
