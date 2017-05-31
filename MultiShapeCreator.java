import java.util.Set;

/**
 * This class creates multiple shapes that would be used in Julia Sets
 * @author RyanPachauri
 */
public class MultiShapeCreator {
   
   //the smaller the spacing, the larger the Julia Set
   private static final double SPACING = 0.005;
   //how many decimal places to round to
   private static final int ROUNDING = 3;
   
   /**
    * @Precondition: For this method, we are assuming that the dimensions of
    *    the Complex Plane are s.t. width = 3 * height and height = size
    * @param size the size of each letter
    * @return a set of <int[]> corresponding to coordinates that would make
    *          up this shape
    */
   public static Set<int[]> getLinearISM(int size) {
      Set<int[]> originalShape = ShapeCreator.getI(0, 0, size);
      originalShape.addAll(ShapeCreator.getS(size, 0, size));
      originalShape.addAll(ShapeCreator.getM(size * 2, 0, size));
      return originalShape;
   }
   
   public static Complex[][] getLinearISMComplexValues(int size) {
      int height = size;
      int width = 3 * height;
      return createComplexValues(width, height);
   }
   
   /**
    * @Precondition: For this method, we are assuming that the dimensions of
    *    the Complex Plane are s.t. width = 3 * height and height = size
    * @param size the size of each letter
    * @return a set of <int[]> corresponding to coordinates that would make
    *          up this shape
    */
   public static Set<int[]> getLinearKLMY(int size) {
      Set<int[]> originalShape = ShapeCreator.getK(0, 0, size);
      originalShape.addAll(ShapeCreator.getL(size, 0, size));
      originalShape.addAll(ShapeCreator.getM(size * 2, 0, size));
      originalShape.addAll(ShapeCreator.getY(size * 3, 0, size));
      return originalShape;
   }
   
   /**
    * @Precondition: For this method, we are assuming that the dimensions of
    *    the Complex Plane are s.t. width = 3 * height and height = size
    * @param size the height, which is 1/4 the width
    */
   public static Complex[][] getLinearKLMYComplexValues(int size) {
      int height = size;
      int width = 4 * height;
      return createComplexValues(size * 4 / 3, height / 2, width, height);
   }
   
   public static Set<int[]> getStaggeredKLMY(int size) {
      Set<int[]> originalShape = ShapeCreator.getK(0, size, size);
      originalShape.addAll(ShapeCreator.getL(size, 0, size));
      originalShape.addAll(ShapeCreator.getM(size * 2, size, size));
      originalShape.addAll(ShapeCreator.getY(size * 3, 0, size));
      return originalShape;
   }
   
   public static Complex[][] getStaggeredKLMYComplexValues(int size) {
      return createComplexValues(size * 3 / 2, size * 25 / 32, size * 4, size * 2);
   }
   
   public static Set<int[]> getStaggeredKLMY2(int size) {
      Set<int[]> originalShape = ShapeCreator.getK(0, 0, size);
      originalShape.addAll(ShapeCreator.getL(size, size, size));
      originalShape.addAll(ShapeCreator.getM(size * 2, 0, size));
      originalShape.addAll(ShapeCreator.getY(size * 3, size, size));
      return originalShape;
   }
   
   public static Complex[][] getStaggeredKLMYComplexValues2(int size) {
      return createComplexValues(size * 3 / 2, size * 57 / 32, size * 4, size * 2);
   }
   
   public static Set<int[]> getHeartDiamondFish(int size) {
      Set<int[]> originalShape = ShapeCreator.getHeart(0, 0, size * 5 / 4);
      originalShape.addAll(ShapeCreator.getDiamond(size, size, size));
      originalShape.addAll(ShapeCreator.getFish(size * 2, 0, size));
      return originalShape;
   }
   
   public static Complex[][] getHeartDiamondFishComplexValues(int size) {
      return createComplexValues(size * 3 / 2, size * 4 / 3, size * 3, size * 2);
   }
   
   public static Complex[][] createComplexValues(int width, int height) {
      return createComplexValues(width / 2, height / 2, width, height);
   }
   
   public static Complex[][] createComplexValues(int originX, int originY,
         int width, int height) {
      Complex[][] points = new Complex[width][height];
      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            double re = round((x - originX) * SPACING, ROUNDING);
            double im = round((originY - y) * SPACING, ROUNDING);
            points[x][y] = new Complex(re, im);
         }
      }
      return points;
   }
   
   public static Complex[][] createComplexValues(double leftBound, double topBound,
         double spacing, int width, int height) {
      Complex[][] points = new Complex[width][height];
      int rounding = findNumDecimals(spacing);
      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            double re = round(x * spacing + leftBound, rounding);
            double im = round(topBound - y * spacing, rounding);
            points[x][y] = new Complex(re, im);
         }
      }
      return points;
   }
   
   private static double round(double d, int numDigits) {
      int multiplier = (int) Math.pow(10, numDigits);
      int x = (int) (d * multiplier);
      double y = (double) x / multiplier;
      return y;
   }
   
   /**
    * @Precondition: d is a double greater than or equal to 0
    * @param d
    * @return
    */
   private static int findNumDecimals(double d) {
      if (d > 0) {
         int i = (int) d;
         d = d - i;
      }
      if (d > 0) {
         return 1 + findNumDecimals(d * 10);
      }
      return 0;
   }
}
