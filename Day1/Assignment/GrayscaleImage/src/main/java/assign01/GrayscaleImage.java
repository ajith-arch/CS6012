package assign01;


import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.stream.DoubleStream;


/**
 * Represents a grayscale (black and white) image as a 2D array of "pixel" brightnesses
 * 255 is "white" 127 is "gray" 0 is "black" with intermediate values in between
 * Author: Ben Jones and Ajith Alphonse
 */
public class GrayscaleImage {
    private double[][] imageData; // the actual image data


    /**
     * Initialize an image from a 2D array of doubles
     * This constructor creates a copy of the input array
     * @param data initial pixel values
     * @throws IllegalArgumentException if the input array is empty or "jagged" meaning not all rows are the same length
     */
    public GrayscaleImage(double[][] data){
        if(data.length == 0 || data[0].length == 0){
            throw new IllegalArgumentException("Image is empty");
        }

        imageData = new double[data.length][data[0].length];
        for(var row = 0; row < imageData.length; row++){
            if(data[row].length != imageData[row].length){
                throw new IllegalArgumentException("All rows must have the same length");
            }
            for(var col = 0; col < imageData[row].length; col++){
                imageData[row][col] = data[row][col];
            }
        }
    }

    /**
     * Fetches an image from the specified URL and converts it to grayscale
     * Uses the AWT Graphics2D class to do the conversion, so it may add
     * an item to your dock/menu bar as if you're loading a GUI program
     * @param url where to download the image
     * @throws IOException if the image can't be downloaded for some reason
     */
    public GrayscaleImage(URL url) throws IOException {
        var inputImage = ImageIO.read(url);
        //convert input image to grayscale
        //based on (https://stackoverflow.com/questions/6881578/how-to-convert-between-color-models)
        var grayImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d= grayImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, null);
        g2d.dispose();
        imageData = new double[grayImage.getHeight()][grayImage.getWidth()];

        //raster is basically a width x height x 1 3-dimensional array
        var grayRaster = grayImage.getRaster();
        for(var row = 0; row < imageData.length; row++){
            for(var col = 0; col < imageData[0].length; col++){
                //getSample parameters are x (our column) and y (our row), so they're "backwards"
                imageData[row][col] = grayRaster.getSampleDouble(col, row, 0);
            }
        }
    }

    public void savePNG(File filename) throws IOException {
        var outputImage = new BufferedImage(imageData[0].length, imageData.length, BufferedImage.TYPE_BYTE_GRAY);
        var raster = outputImage.getRaster();
        for(var row = 0; row < imageData.length; row++){
            for(var col = 0; col < imageData[0].length; col++){
                raster.setSample(col, row, 0, imageData[row][col]);
            }
        }
        ImageIO.write(outputImage, "png", filename);
    }

    ///Methods to be filled in by students below

    /**
     * Get the pixel brightness value at the specified coordinates
     * (0,0) is the top left corner of the image, (width -1, height -1) is the bottom right corner
     * @param x horizontal position, increases left to right
     * @param y vertical position, **increases top to bottom**
     * @return the brightness value at the specified coordinates
     * @throws IllegalArgumentException if x, y are not within the image width/height
     */
    public double getPixel(int x, int y) throws IllegalArgumentException{
        // TODO: determine if negative indexes should be supported. Doc string and testGetPixelThrowsOnNegativeX test are contradictory.
        if (x < 0 || x >= imageData[0].length || y < 0 || y >= imageData.length) {
            throw new IllegalArgumentException("Pixels are out of bounds");
        }

        return  imageData[y][x];
    }

    /**

     * Two images are equal if they have the same size and each corresponding pixel
     * in the two images is exactly equal
     * @param other
     * @return true if the objects are equivalent, otherwise false.
     */
    @Override
    public boolean equals(Object other){
        if(!(other instanceof GrayscaleImage)){
            return false;
        }

        GrayscaleImage otherImage = (GrayscaleImage)other;

        if (imageData.length != otherImage.imageData.length || imageData[0].length
                != otherImage.imageData[0].length) {
            return false;
        }

        // Check that each pixel value is equivalent
        for (int i = 0; i < imageData.length; i++) {
            for (int j = 0; j < imageData[0].length; j++) {
                if (imageData[i][j] != otherImage.imageData[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Computes the average of all values in image data
     * @return the average of the imageData array
     */
    public double averageBrightness(){
        double sum = 0;
        for (int i = 0; i < imageData.length; i++) {
            sum += DoubleStream.of(imageData[i]).sum();
        }
        return sum / (imageData.length * imageData[0].length);
    }

    /**
     * Return a new GrayScale image where the average new average brightness is 127
     * To do this, uniformly scale each pixel (ie, multiply each imageData entry by the same value)
     * Due to rounding, the new average brightness will not be 127 exactly, but should be very close
     * The original image should not be modified
     * @return a GrayScale image with pixel data uniformly rescaled so that its averageBrightness() is 127
     */
    public GrayscaleImage normalized(){
        double avgBrightness = averageBrightness();
        // If the average is zero then return a copy of this image as the "scaled" from will still be 0
        if (avgBrightness == 0){
            return new GrayscaleImage(imageData);
        }

        double scale = 127 / averageBrightness();

        double[][] scaledpart = new double[imageData.length][imageData[0].length];
        for (int i = 0; i < imageData.length; i++) {
            for (int j = 0; j < imageData[0].length; j++) {
                scaledpart[i][j] = imageData[i][j] * scale;
            }
        }

        return new GrayscaleImage(scaledpart);
    }


    /**
     * Returns a new grayscale image that has been "mirrored" across the y-axis
     * In other words, each row of the image should be reversed
     * The original image should be unchanged
     * @return a new GrayscaleImage that is a mirrored version of the this
     */
    public GrayscaleImage mirrored(){
        double[][] croppedData = new double[imageData.length][imageData[0].length];

        for (int i = 0; i < imageData.length; i++) {
            for (int j = 0; j < imageData[0].length; j++) {
                croppedData[i][j] = imageData[i][imageData[0].length - 1 - j];
            }
        }

        return new GrayscaleImage(croppedData);
    }

    /**
     * Returns a new GrayscaleImage of size width x height, containing the part of `this`
     * from startRow -> startRow + height, startCol -> startCol + width
     * The original image should be unmodified
     * @param startRow
     * @param startCol
     * @param width
     * @param height
     * @return A new GrayscaleImage containing the sub-image in the specified rectangle
     * @throws IllegalArgumentException if the specified rectangle goes outside the bounds of the original image
     */
    public GrayscaleImage cropped(int startRow, int startCol, int width, int height) throws IllegalArgumentException{
        int endRow = startRow + (height - 1); // Last row index in source data required for crop
        int endCol = startCol + (width - 1); // Last column index in source data required for crop

        // ensure the target end indexes exist in the source image
        if (endRow >= imageData.length || endCol >= imageData[0].length) {
            throw new IllegalArgumentException("Cropped row and column out of bounds");
        }

        double[][] croppedData = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                croppedData[i][j] = imageData[i + startRow][j + startCol];
            }
        }

        return new GrayscaleImage(croppedData);
    }

    /**
     * Returns a new "centered" square image (new width == new height)
     * For example, if the width is 20 pixels greater than the height,
     * this should return a height x height image, with 10 pixels removed from the left and right
     * edges of the image
     * If the number of pixels to be removed is odd, remove 1 fewer pixel from the left or top part
     * (note this convention should be SIMPLER/EASIER to implement than the alternative)
     * The original image should not be changed
     * @return a new, square, GrayscaleImage
     */
    public GrayscaleImage squarified(){
        int numRows = imageData.length;
        int numColumns = imageData[0].length;
        if (numRows == numColumns) {
            return new GrayscaleImage(imageData);
        }

        if (numRows > numColumns) {
            // height > width
            return cropped(0, 0, numColumns, numColumns);
        } else {
            // width > height
            return cropped(0, 0, numRows, numRows);
        }
    }
 /*
    public GrayscaleImage squarified(){
        int newSize = Math.min(imageData.length, imageData[0].length);
        int rowOffset = (imageData.length - newSize) / 2;
        int colOffset = (imageData[0].length - newSize) / 2;
        return cropped(rowOffset, colOffset, newSize, newSize);
    }

       */

    public double[][] getData(){
        return imageData;
    }

    public int width(){
        return imageData[0].length;
    }

    public int height(){
        return imageData.length;
    }
}