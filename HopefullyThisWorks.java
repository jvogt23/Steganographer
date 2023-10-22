import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class HopefullyThisWorks {
    private static Scanner scan = new Scanner(System.in);
    static String filePath;
    static BufferedImage img;
    static File imgFile;

    //Message
    static StringBuilder binaryMessage = new StringBuilder();
    //Image colors
    static ArrayList<Color> colors = new ArrayList<Color>();
    static ArrayList<Color> finalColors = new ArrayList<Color>();
    
    public static void main(String[] args) {
        
        System.out.println("Give the full file path of an image. ");
        filePath = scan.nextLine();

        try{
            imgFile = new File(filePath);
            img = ImageIO.read(imgFile);
            System.out.println(img);  
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
        
        System.out.println("Are you encoding (0) or decoding (1)?");
        boolean isEncoding = false;
        if(isEncoding){
            System.out.println("Please provide a message to encode into this image.");
            String message = scan.nextLine();
            binaryMessage.append(ConvertStringToBinary(message));
            try {
                ImageIO.write(editRGBVals(img, binaryMessage), "png", new File("C:/Users/james/Documents/img1.png"));
            }
            catch(Exception E){
                E.printStackTrace();
                System.exit(0);
            }
        } else {
            System.out.println(binaryToMessage(new StringBuilder(getMessageBack(img))));
        }
        /*
        getRGBVals(img);
        for(int i = 0; i < colors.size(); i++){
            System.out.println(colors.get(i));
        }
        */
    }







    //Store binary string in least significant bits of R, G, and B


    //Convert the numbers for RGB to binary
    public static BufferedImage editRGBVals(BufferedImage img, StringBuilder message){
        for(int i = 0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++){
                Color tempColor = new Color(img.getRGB(j, i));
                int blue = tempColor.getBlue();
                if(!(message.length() == 0)){
                    int injectedChar = Integer.parseInt(message.substring(0,1));
                    System.out.println(blue);
                    blue = blue - blue % 10;
                    blue = blue + injectedChar;
                    System.out.println(blue);
                    message = message.deleteCharAt(0);
                    img.setRGB(j, i, new Color(tempColor.getRed(), tempColor.getGreen(), blue).getRGB());
                }
                else{
                    return img;
                }
            }
        }
        return img;
    }

    //get the message back
    public static String getMessageBack(BufferedImage img){
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
    
    //Sets the rgb values to the buffered image
    //IMPORTANT: THIS METHOD NEEDS TO STOP WHEN THE ARRAY IS EMPTY. CHECK OTHER METHODS TO MAKE SURE THEY ARENT DOING STUPID THINGS EITHER








    //good method
    //Getting the characters of the message back from binary

    //good method



    public static String ConvertStringToBinary(String MESSAGE) {
        
        StringBuilder binaryResult = new StringBuilder();
        for (char c : MESSAGE.toCharArray()){
            String binString = Integer.toBinaryString((int) c);
            binaryResult.append(setASCIIBits(binString, 8));
        }
        binaryResult.append("00000100");
        System.out.println(binaryResult.toString());
        return binaryResult.toString();
    }

        //good method
    //Sets number of bits for each block. If I decide to use unicode at any point (hopefully not) this will be useful
    public static String setASCIIBits(String inputBinary, int blockSize){
        StringBuilder finalString = new StringBuilder();

        for(int i = 0; i < blockSize; i++){
            finalString.append("0");
        }
        finalString.append(inputBinary);
        finalString.delete(0, finalString.length() - blockSize);
        return finalString.toString();
    }

        //Getting the characters of the message back from binary
        public static String binaryToMessage(StringBuilder binary){
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
}
