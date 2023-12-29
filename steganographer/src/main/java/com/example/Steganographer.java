/**
 * Class intended to traverse an image and encode text within it.
 * @author James Vogt
 * @version 1.2
 */
package com.example;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class Steganographer {
    /**
     * Completes entire encoding process for an image.
     * @param img The image to be edited
     * @param message the message to be encoded in
     * @return a new BufferedImage containing the message
     * @throws IllegalArgumentException If either argument is null, or the string is empty
     */
    public BufferedImage encode(BufferedImage img, String message) throws IllegalArgumentException {
        if (img == null || message == null || message.isEmpty()) throw new IllegalArgumentException(
            "Arguments must not be null or empty"
        );
        BufferedImage copyImg = deepCopy(img);
        StringBuilder binaryMessage = new StringBuilder(convertStringToBinary(message.toString()));
        BufferedImage newImg = editRGBVals(copyImg, binaryMessage);
        return newImg;
    }

    /**
     * Edits the RGB values of the inputted image based on the ASCII translation of the inputted message.
     * @param img BufferedImage object representing the image to edit
     * @param message StringBuilder representing the message to encode
     * @return BufferedImage object with updated RGB values
     */
    private BufferedImage editRGBVals(BufferedImage img, StringBuilder message) {
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color tempColor = new Color(img.getRGB(j, i));
                int blue = tempColor.getBlue();
                if (!(message.length() == 0)) {
                    int injectedChar = Integer.parseInt(message.substring(0,1));
                    System.out.println(blue);
                    blue = blue - blue % 10;
                    blue = blue + injectedChar;
                    System.out.println(blue);
                    message = message.deleteCharAt(0);
                    img.setRGB(j, i, new Color(tempColor.getRed(), tempColor.getGreen(), blue).getRGB());
                } else {
                    return img;
                }
            }
        }
        return img;
    }

    /**
     * Takes in a String representing a block of data and pads it out with zeros at the beginning.
     * Resulting string exactly fits specified size
     * @param inputBinary String to input, typically a binary string
     * @param blockSize Size of each block of data, as an int
     * @return String containing data in inputBinary padded with zeros.
     */
    private String padBlock(String inputBinary, int blockSize){
        StringBuilder finalString = new StringBuilder();

        for (int i = 0; i < blockSize; i++) {
            finalString.append("0");
        }
        finalString.append(inputBinary);
        finalString.delete(0, finalString.length() - blockSize);
        return finalString.toString();
    }

    /**
     * Converts a string to binary characters, as ASCII.
     * @param MESSAGE String to be converted
     * @return Binary String of block size 8 containing the ASCII of the input
     */
    private String convertStringToBinary(String MESSAGE) {
        
        StringBuilder binaryResult = new StringBuilder();
        for (char c : MESSAGE.toCharArray()) {
            String binString = Integer.toBinaryString((int) c);
            binaryResult.append(padBlock(binString, 8));
        }
        binaryResult.append("00000100");
        System.out.println(binaryResult.toString());
        return binaryResult.toString();
    }

    /**
     * Converts an ASCII binary string to plain text.
     * @param binary StringBuilder containing ASCII in 8 bit blocks
     * @return String containing the original message
     */
    private String binaryToMessage(StringBuilder binary){
        int messageLength = (binary.length() / 8);
        char[] chars = new char[messageLength];
        StringBuilder decodedMessage = new StringBuilder();
        for(int i = 0; i < messageLength; i++){
            chars[i] = (char) Integer.parseInt(binary.substring(0,8), 2);
            binary.delete(0,8);
        }

        decodedMessage.append(chars, 0, chars.length);

        return decodedMessage.toString();
    }

    /**
     * Removes a message from an image so it can be decoded.
     * @param img BufferedImage object to grab message from
     * @return String containing binary message retrieved
     */
    private String getMessageBack(BufferedImage img){
        StringBuilder message = new StringBuilder();
        for(int i = 0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++){
                Color tempColor = new Color(img.getRGB(j,i));
                int blue = tempColor.getBlue() % 10;
                if((blue != 0 && blue != 1) || (message.length() >= 8 && message.substring(message.length() - 8).equals("00000100"))){
                    return message.toString();
                }
                message.append(blue);
                
            }
        }
        return message.toString();
    }

    private BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
