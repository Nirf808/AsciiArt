package image;

import java.awt.*;

public class GrayNormalizer implements ImageNormalizeBrightness{
    @Override
    public Image normalize(Image image) {
        Color[][] imageColors = new Color[image.getHeight()][image.getWidth()];
        for (int row = 0; row < image.getHeight(); row++){
            for (int col = 0; col < image.getWidth(); col++){
                imageColors[row][col] = toGray(image, row, col);
            }
        }
        return new Image(imageColors, image.getHeight(), image.getWidth());
    }

    private Color toGray(Image image, int row, int col){
        Color color = image.getPixel(row, col);
        return new Color(0,0,0);
//        return color.getRed() * 0.2126 + color.getGreen() * 0.7152
//                + color.getBlue() * 0.0722;
    }
}
