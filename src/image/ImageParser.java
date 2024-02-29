package image;

import java.awt.*;

public class ImageParser {

    public static final int MAX_GRAY_VAL = 255;

    public static Image padImage(Image image) {
        int newHeight = (int) Math.pow(2, (int) Math.ceil(Math.log(image.getHeight()) / Math.log(2)));
        int newWidth = (int) Math.pow(2, (int) Math.ceil(Math.log(image.getWidth()) / Math.log(2)));
        int heightMargin = (newHeight - image.getHeight()) / 2;
        int widthMargin = (newWidth - image.getWidth()) / 2;
        Color[][] colors = new Color[newHeight][newWidth];
        for (int row = 0; row < newHeight; row++) {
            for (int col = 0; col < newWidth; col++) {
                if(row < heightMargin || row >= image.getHeight() + heightMargin) {
                    colors[row][col] = Color.WHITE;
                } else if (col < widthMargin || col >= image.getWidth() + widthMargin) {
                    colors[row][col] = Color.WHITE;
                } else{
                    colors[row][col] = image.getPixel(row - heightMargin, col - widthMargin);
                }
            }
        }
        return new Image(colors, newWidth, newHeight);
    }

    public static double[][] getImageParts(Image image, int resolution) {
        double[][] subImagesGrayScale = new double[resolution][resolution];
        int subImageSize = image.getWidth() / resolution;
        for (int startRow = 0; startRow < image.getHeight(); startRow += subImageSize){
            for (int startCol = 0; startCol < image.getWidth(); startCol += subImageSize){
                subImagesGrayScale[startRow / subImageSize][startCol / subImageSize] =
                        getSubImageGrayScale(image, subImageSize, startRow, startCol);
            }
        }
        return subImagesGrayScale;
    }

    private static double getSubImageGrayScale(Image image, int subImageSize, int startRow, int startCol) {
        double totalGrayness = 0;
        for (int row = startRow; row < startRow + subImageSize; row++) {
            for (int col = startCol; col < startCol + subImageSize; col++) {
                totalGrayness += getPixelGrayScale(image.getPixel(row, col));
            }
        }
        return totalGrayness / (Math.pow(subImageSize, 2) * MAX_GRAY_VAL);
    }

    private static double getPixelGrayScale(Color color) {
        return color.getRed() * 0.2126 + color.getGreen() * 0.7152
                + color.getBlue() * 0.0722;
    }
}
