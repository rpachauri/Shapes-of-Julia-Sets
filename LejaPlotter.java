import java.awt.*;
import java.util.*;
import java.io.*;

/**
 * This class is the driver program for plotting Julia Sets.
 * It is possible to draw filled Julia sets along with zoomed in pictures.
 * 
 * Increasing the size of the picture allows for higher resolution.
 * However, also be sure to adjust n and s as you do so.
 * 
 * n should be a large number (at least 1000) for nice shapes and s
 * should be an inversely small number. (A good metric is typically 1/n).
 * 
 * To pick an area to zoom in on, you have to play around with the parameters
 *    until you get a good spot. Also be sure to use the insertBox method
 *    in the ImageStandardizer class if you want to see how the box would look
 *    on an original Julia set plot.
 * 
 * @author RyanPachauri
 * @version 5/23/17
 */
public class LejaPlotter {
   public static final String LEJA_POINTS_SUFFIX = ".txt";
   
   public static final Color OUTSIDE_JULIA_SET = Color.BLUE;
   public static final Color INSIDE_JULIA_SET = Color.RED;
   public static final Color ORIGINAL_SHAPE = Color.LIGHT_GRAY;
   public static final Color LEJA_POINT = Color.BLACK;
   public static final Color AXIS_POINT = Color.ORANGE;
   public static final Color ZOOM_AREA = Color.GREEN;
   
   public static void main(String[] args) {
      int size = 1000;
      int n = 2000;
      double s = 1.0 / n;
      String directory = "Shapes of Julia Sets/";
      String experiment = "fdh changing_n/";
      String fileNamePrefix = "Fish Diamond Heart";
      Complex[][] allPoints = MultiShapeCreator.getHeartDiamondFishComplexValues(size);
      Set<Complex> originalShape = getShape(MultiShapeCreator.getHeartDiamondFish(size), allPoints);
      String pictureInfo =  " - " + allPoints.length + " x " +
            allPoints[0].length + " - " + n + " leja points out of " +
            originalShape.size() + " - s = 1 ÷ " + n + " - ";
      String fileName = directory + experiment + fileNamePrefix + pictureInfo;
      
      int leftX = size * 3/ 2 - size / 11 - size / 64;
      int rightX = size * 3 / 2 - size / 11;
      int topY = size + size / 8 + size / 64;
      int bottomY = size + size / 8 + size / 32;
      LejaPlotter.drawInitialShapeWithZoomArea(fileName, originalShape, allPoints,
            leftX, rightX, topY, bottomY);
      LejaPlotter.drawInitialShape(fileName, originalShape, allPoints);
      LejaPoints lp = new LejaPoints(originalShape, n, s);
      LejaPlotter.saveLejas(fileName, lp);
      JuliaSetFinder jsf = new JuliaSetFinder(lp, allPoints);
      LejaPlotter.drawInitialJuliaSet(fileName, jsf, originalShape);
      int zoom = 100;
      LejaPlotter.drawSmallerRange(fileName, allPoints, leftX, rightX, topY, bottomY, zoom, lp);
      System.out.println("All experiments complete!");
   }
   
   /**
    * Plots a filled Julia set
    * @param fileName String to save the Picture to
    * @param jsf  JuliaSetFinder object used to plot
    * @param originalShape
    */
   public static void drawInitialJuliaSet(String fileName, JuliaSetFinder jsf,
         Set<Complex> originalShape) {
      long startTime = System.currentTimeMillis();
      Color[][] colors = jsf.plotPointsExceptUsingObviousStream(originalShape);
      long endTime = System.currentTimeMillis();
      String elapsedTime = LejaPlotter.convertElapsedTime(endTime - startTime);
      Picture img = LejaPlotter.convertColorsToPicture(colors);
      img.save(fileName + elapsedTime + "." + ShapeCreator.IMAGE_SUFFIX);
   }
   
   /**
    * @param points  a Set containing int[] each of length 2 where:
    *       the first int represents the x coordinate and
    *       the second int represents the y coordinate
    * @param allPoints all the points
    * @return  Set of Complex numbers from originalPoints that correspond to
    *             the coordinates in points
    */
   private static Set<Complex> getShape(Set<int[]> points, Complex[][]
         allPoints) {
      Set<Complex> complexNumbers = new HashSet<Complex>();
      for (int[] point : points) {
         complexNumbers.add(allPoints[point[0]][point[1]]);
      }
      return complexNumbers;
   }
   
   /**
    * given a range within a 2D array of complex numbers, an amount to zoom,
    *    a LejaPoints instance:
    *    we should be able to plot another image
    * @param fileName the name of the file we want to save the picture to
    * @param allPoints Complex numbers we'd like to zoom in on
    * @param leftX
    * @param rightX
    * @param topY
    * @param bottomY
    * @param zoom the amount to zoom by
    * @param lp   a LejaPoints instance that carries a polynomial we want to
    *             evaluate
    */
   private static void drawSmallerRange(String fileName, Complex[][] allPoints, int leftX,
         int rightX, int topY, int bottomY, int zoom, LejaPoints lp) {
      int width = zoom * (rightX - leftX);
      int height = zoom * (bottomY - topY);
      Complex topLeft = allPoints[leftX][topY];
      Complex bottomRight = allPoints[rightX][bottomY];
      double leftBound = topLeft.re();
      double topBound = topLeft.im();
      double spacing = (bottomRight.re() - leftBound) / (rightX - leftX ) /
            zoom;
      Complex[][] zoomedPoints = MultiShapeCreator.createComplexValues(leftBound, topBound,
            spacing, width, height);
      JuliaSetFinder jsf = new JuliaSetFinder(lp, zoomedPoints);
      System.out.println("Drawing zoom at " + zoom + "x with " +
            zoomedPoints.length + " columns");
      long startTime = System.currentTimeMillis();
      Color[][] colors = jsf.plotPointsExceptUsingObviousStream(new HashSet<Complex>());
      long endTime = System.currentTimeMillis();
      String elapsedTime = LejaPlotter.convertElapsedTime(endTime - startTime);
      Picture img = LejaPlotter.convertColorsToPicture(colors);
      img.save(fileName + zoom + "x - " + elapsedTime + "." + ShapeCreator.IMAGE_SUFFIX);
   }
   
   /**
    * @param time a long representing the number of milliseconds
    * @return String giving the amount of time in a more human-readable format
    */
   private static String convertElapsedTime(long time) {
      String result = "" + time % 1000 + " ms";
      if (time > 1000) {
         time /= 1000;//converts time from milliseconds to seconds
         result = time % 60 + " s " + result;
         if (time > 60) {
            time /= 60;//converts time from seconds to minutes
            result = time % 60 + " min " + result;
            if (time > 60) {
               time /= 60;
               result = time + " h " + result;
            }
         }
      }
      return result;
   }
   
   /**
    * Draws the initial shape with the given points
    * @param fileName   The name of the file to save the picture as
    * @param points  Complex numbers that should not be plotted
    * @param allPoints  
    */
   public static void drawInitialShape(String fileName,
         Set<Complex> originalShape, Complex[][] allPoints) {
      int width = allPoints.length;
      int height = allPoints[0].length;
      Picture img = new Picture(width, height);
      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            drawOriginalShapePoint(x, y, img, originalShape, allPoints);
         }
      }
      System.out.println("done printing original shape!");
      img.save(fileName + "- original image." +
            ShapeCreator.IMAGE_SUFFIX);
   }
   
   /**
    * Draws the initial shape with the given points
    * @param fileName   The name of the file to save the picture as
    * @param points  Complex numbers that should not be plotted
    * @param allPoints
    * @param leftX
    * @param rightX
    * @param topY
    * @param bottomY
    */
   private static void drawInitialShapeWithZoomArea(String fileName,
         Set<Complex> originalShape, Complex[][] allPoints, int leftX,
         int rightX, int topY, int bottomY) {
      int width = allPoints.length;
      int height = allPoints[0].length;
      Picture img = new Picture(width, height);
      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            if ((x == leftX || x == rightX || y == topY || y == bottomY) &&
                  (x >= leftX && x <= rightX && y >= topY && y <= bottomY)) {
               img.set(x, y, LejaPlotter.ZOOM_AREA);
            } else {
               drawOriginalShapePoint(x, y, img, originalShape, allPoints);
            }
         }
      }
      System.out.println("done printing original shape!");
      img.save(fileName + "- original image." +
            ShapeCreator.IMAGE_SUFFIX);
   }
   
   /**
    * Draws a point according to how the original shape should look.
    * 
    * @param x x-coordinate
    * @param y y-coordinate
    * @param img  Picture instance to draw to
    * @param originalShape points that are in the original shape
    * @param allPoints  Complex plane to pick our point from
    */
   public static void drawOriginalShapePoint(int x, int y, Picture img,
         Set<Complex> originalShape, Complex[][] allPoints) {
      Complex z = allPoints[x][y];
      if (originalShape.contains(z)) {
         img.set(x, y, LejaPlotter.ORIGINAL_SHAPE);
      } else if (z.re() == 0 || z.im() == 0) {
         img.set(x, y, LejaPlotter.AXIS_POINT);
      }
   }
   
   /**
    * 
    * @param colors 2D array of Color objects we want to map to a Picture
    * @return  Picture instance
    */
   private static Picture convertColorsToPicture(Color[][] colors) {
      int width = colors.length;
      int height = colors[0].length;
      Picture img = new Picture(width, height);
      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            img.set(x, y, colors[x][y]);
         }
      }
      return img;
   }
   
   /**
    * @param fileName   name of the file we want to save the LejaPoints
    *                   instance to
    * @param lp         LejaPoints instance we'd like to save
    */
   public static void saveLejas(String fileName, LejaPoints lp) {
      try {
         File file = new File(fileName + LejaPlotter.LEJA_POINTS_SUFFIX);
         PrintStream output = new PrintStream(file);
         lp.export(output);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
   }
}