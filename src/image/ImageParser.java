package image;

import java.awt.*;

public class ImageParser {
    public static Image padImage(Image image){
        int newHeight = (int)Math.pow(2, (int) Math.ceil(Math.log(image.getHeight()) / Math.log(2)));
        int newWidth = (int)Math.pow(2, (int) Math.ceil(Math.log(image.getWidth()) / Math.log(2)));
        Color[][] colors = new Color[newHeight][newWidth];
        for (int row = 0; row < newHeight; row++){
            for (int col = 0; col < newWidth; col++){
                if (row < image.getHeight() && col < image.getWidth()){
                    Color original = image.getPixel(row, col);
                    colors[row][col] = new Color(original.getRGB());
                }
                else {
                    colors[row][col] = Color.WHITE;
                }
            }
        }
        return new Image(colors, newWidth, newHeight);
    }

    public static double[][] getImageParts(Image image, int resolution){
        int subImage = image.getWidth() / resolution;
        return new double[0][0];
    }

    private static double getGrayScale(Image image, int subImageSize, int startRow, int startCol){
        return 0;
    }
}
