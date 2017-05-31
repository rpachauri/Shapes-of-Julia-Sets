import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

/**
 * This class creates shapes that would be used in Julia Sets
 * @author RyanPachauri
 */
public class ShapeCreator {
   
   private static final String ORIGINAL_SHAPE_DIRECTORY = "original_shapes/";
   public static final String IMAGE_SUFFIX = "png";
   
   /**
    * For this method, please make sure that size == size
    */
   public static Set<int[]> getSquare(int size) {
      Set<int[]> points = new HashSet<int[]>();
      for (int x = size / 4; x < 3 * size / 4; x++) {
         int y = size / 4;
         points.add(new int[] {x, y});
         y = 3 * size / 4;
         points.add(new int[] {x, y});
      }
      for (int y = size / 4; y < 3 * size / 4; y++) {
         int x = size / 4;
         points.add(new int[] {x, y});
         x = 3 * size / 4;
         points.add(new int[] {x, y});
      }
      return points;
   }
   
   public static Set<int[]> getI(int startX, int startY, int size) {
      Set<int[]> points = new HashSet<int[]>();
      int topOfI = size * 3 / 16 + startY;
      int bottomOfI = startY + size - size * 3 / 16;
      int leftBoundary = size * 3 / 8 + startX;
      int rightBoundary = size * 5 / 8 + startX;
      for (int x = leftBoundary; x < rightBoundary; x++) {
         //top of bottom of I
         points.add(new int[] {x, topOfI});
         points.add(new int[] {x, bottomOfI});
      }
      int charactersize = size * 3 / 32;
      for (int y = 0; y < charactersize; y++) {
         //outside edges of I
         points.add(new int[] {leftBoundary, topOfI + y});
         points.add(new int[] {leftBoundary, bottomOfI - y});
         points.add(new int[] {leftBoundary, topOfI + y});
         points.add(new int[] {rightBoundary, bottomOfI - y});
      }
      int insideLeftBoundary = size * 29 / 64 + startX;
      for (int x = leftBoundary; x < insideLeftBoundary; x++) {
         //left inside edges of I
         points.add(new int[] {x, topOfI + charactersize});
         points.add(new int[] {x, bottomOfI - charactersize});
      }
      int insideRightBoundary = size * 35 / 64 + startX;
      for (int x = insideRightBoundary; x < rightBoundary; x++) {
         //right inside edges of I
         points.add(new int[] {x, topOfI + charactersize});
         points.add(new int[] {x, bottomOfI - charactersize});
      }
      for (int y = topOfI + charactersize; y < bottomOfI - charactersize; y++) {
         //inside bars of I
         points.add(new int[] {insideLeftBoundary, y});
         points.add(new int[] {insideRightBoundary, y});
      }
      return points;
   }
   
   public static Set<int[]> getK(int startX, int startY, int size) {
      return getShapeFromSolidImage("K", startX, startY, size);
   }
   
   public static Set<int[]> getL(int startX, int startY, int size) {
      return getShapeFromSolidImage("L", startX, startY, size);
   }
   
   public static Set<int[]> getM(int startX, int startY, int size) {
      return getShapeFromSolidImage("M", startX, startY, size);
   } 
   
   public static Set<int[]> getS(int startX, int startY, int size) {
      return getShapeFromSolidImage("S", startX, startY, size);
   }

   public static Set<int[]> getY(int startX, int startY, int size) {
      return getShapeFromSolidImage("Y", startX, startY, size);
   }
   
   public static Set<int[]> getHand(int startX, int startY, int size) {
      return getShapeFromSolidImage("hand", startX, startY, size);
   }
   
   public static Set<int[]> getFoot(int startX, int startY, int size) {
      return getShapeFromSolidImage("foot", startX, startY, size);
   }
   
   public static Set<int[]> getRabbit(int startX, int startY, int size) {
      return getShapeFromSolidImage("rabbit", startX, startY, size);
   }
   
   public static Set<int[]> getDiamond(int startX, int startY, int size) {
      return getShapeFromSolidImage("diamond", startX, startY, size);
   }

   public static Set<int[]> getHeart(int startX, int startY, int size) {
      return getShapeFromSolidImage("heart", startX, startY, size);
   }
   
   public static Set<int[]> getFish(int startX, int startY, int size) {
      return getShapeFromSolidImage("fish", startX, startY, size);
   }
   
   private static Picture getScaledImage(File directory, String shape, int size) {
      String[] files = directory.list();
      for (String file : files) {
         if (file.indexOf("size = " + size) > -1) {
            return new Picture(directory.getPath() + "/" + file);
         }
      }
      try {
         File originalImage = new File(directory.getPath() + "/" + shape
               + "." + IMAGE_SUFFIX);
         BufferedImage img = ImageIO.read(originalImage);
         Image scaledImage =
               img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
         
         BufferedImage buffered = new BufferedImage(size, size, img.getType());
         buffered.getGraphics().drawImage(scaledImage, 0, 0 , null);
         
         String newlyScaledImage = directory + "/" + shape + " size = " + size
               + "." + IMAGE_SUFFIX;
         ImageIO.write(buffered, IMAGE_SUFFIX, new File(newlyScaledImage));
         return new Picture(newlyScaledImage);
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }
   }
   
   /**
    * @Precondition:
    * 1. in the ORIGINAL_SHAPE_DIRECTORY, there must be a
    *    directory with the same name as the given parameter and .png file
    *    also with the same name as the given parameter
    * 2. the image should be black and white - where white surrounds the
    *    black image
    * 
    * This method assumes the image includes only solid images, meaning it
    *  does not have any white inside the edges.
    *    
    * @param shape   the name of the shape we want to create
    * @param startX
    * @param startY
    * @param size    the size of the shape we want to create
    * @return  a Map of Complex numbers to int[] values
    * @throws IOException 
    */
   private static Set<int[]> getShapeFromSolidImage(String shape,
         int startX, int startY, int size) {
      File directory = new File(ORIGINAL_SHAPE_DIRECTORY + shape);
      if (!directory.isDirectory()) {
         throw new IllegalArgumentException();
      }
      Picture shapeWithSize = getScaledImage(directory, shape, size);
      if (shapeWithSize == null) {
         throw new IllegalStateException();
      }
      Set<int[]> points = new HashSet<int[]>();
      for (int x = 1; x < size - 1; x++) {
         for (int y = 1; y < size - 1; y++) {
            if (shapeWithSize.get(x, y).equals(ImageStandardizer.IN_IMAGE) &&
                !(shapeWithSize.get(x - 1, y).equals(ImageStandardizer.IN_IMAGE) &&
                shapeWithSize.get(x + 1, y).equals(ImageStandardizer.IN_IMAGE) &&
                shapeWithSize.get(x, y - 1).equals(ImageStandardizer.IN_IMAGE) &&
                shapeWithSize.get(x, y + 1).equals(ImageStandardizer.IN_IMAGE))) {
               points.add(new int[] {x + startX, y + startY});
            }
         }
      }
      for (int i = 0; i < size; i++) {
         if (shapeWithSize.get(i, 0).equals(ImageStandardizer.IN_IMAGE)) {
            points.add(new int[] {i + startX, startY});
         }
         if (shapeWithSize.get(i, size - 1).equals(ImageStandardizer.IN_IMAGE)) {
            points.add(new int[] {i + startX, size - 1 + startY});
         }
         if (shapeWithSize.get(0, i).equals(ImageStandardizer.IN_IMAGE)) {
            points.add(new int[] {startX, i + startY});
         }
         if (shapeWithSize.get(size - 1, i).equals(ImageStandardizer.IN_IMAGE)) {
            points.add(new int[] {size - 1 + startX, i + startY});
         }
      }
      return points;
   }
}
