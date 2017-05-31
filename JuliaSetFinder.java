import java.awt.*;
import java.util.*;

/**
 * Class that maps a 2D array of Complex numbers to a 2D array Color objects
 *    using a polynomial determined by a LejaPoints object.
 * 
 * @author RyanPachauri
 * @version 4/23/17
 */
public class JuliaSetFinder {
   private LejaPoints lp;
   private final Complex[][] allPoints;
 
   //increasing this makes fewer points in the Julia Set
   private static final double FAR_ENOUGH = 10.0;
   //increasing this makes fewer points in the Julia Set
   private static final int NUM_ITERATIONS = 15;
   //keeps track of how many columns are completed so user knows how far along
   //they are in drawing the filled Julia set
   private int track;
   
   /**
    * Initializes a JuliaSetFinder instance
    * 
    * @param lp   a LejaPoints instance with a predefined polynomial
    * @param points a 2D array of Complex numbers we want to map to
    *    a 2D array of Color objects
    */
   public JuliaSetFinder(LejaPoints lp, Complex[][] points) {
      this.lp = lp;
      this.allPoints = points;
      this.track = 0;
   }
   
   /**
    * Determines the color using the obvious method
    * @param z a Complex number in the Complex plane
    * @param originalShape
    * @return a Color that corresponds to whether or not the Complex number
    *    is in the Julia set
    */
   private Color pickColor(Complex z, Set<Complex> originalShape) {
      Set<Complex> lejaPoints = this.lp.getLejaPoints();
      if (lejaPoints.contains(z)) {
         return LejaPlotter.LEJA_POINT;
      } else if (originalShape.contains(z)) {
         return LejaPlotter.ORIGINAL_SHAPE;
      } else if (this.isFarFromJuliaSet(z)) {
         return LejaPlotter.OUTSIDE_JULIA_SET;
      }
      return LejaPlotter.INSIDE_JULIA_SET;
   }
   
   /**
    * Determines the color using the distance estimation method
    * @param z a Complex number in the Complex plane
    * @return a Color that corresponds to whether or not the Complex number
    *    is in the Julia set
    */
   private Color pickColor(Complex z) {
      double distance = this.distanceFromJuliaSet(z);
      if (distance > 0) {
         return LejaPlotter.OUTSIDE_JULIA_SET;
      }
      return LejaPlotter.INSIDE_JULIA_SET;
   }
   
   /**
    * Maps complex numbers in stored Complex plane to a 2D array of Color
    *    objects using obvious method.
    * @param points  Complex numbers that should not be plotted
    */
   public Color[][] plotPointsExceptUsingObviousStream(Set<Complex> points) {
      return Arrays.stream(this.allPoints).parallel().
            map(zs -> this.mapComplexToColor(zs, points)).toArray(Color[][]::new);
   }
   
   /**
    * @param zs   1D array of Complex numbers
    * @param points  Set of Complex numbers belonging to the original shape
    * @return  1D array of Color objects
    */
   private Color[] mapComplexToColor(Complex[] zs, Set<Complex> points) {
      Color[] colors = new Color[zs.length];
      for (int i = 0; i < zs.length; i++) {
         colors[i] = this.pickColor(zs[i], points);
      }
      System.out.println(track++);
      return colors;
   }
   
   /**
    * Maps complex numbers in stored Complex plane to a 2D array of Color
    *    objects using distance estimation.
    */
   public Color[][] plotPointsExceptUsingDEStream() {
      return Arrays.stream(this.allPoints).parallel().
            map(zs -> this.mapComplexToColor(zs)).toArray(Color[][]::new);
   }
   
   /**
    * @param zs   1D array of Complex numbers
    * @return  1D array of Color objects
    */
   private Color[] mapComplexToColor(Complex[] zs) {
      Color[] colors = new Color[zs.length];
      for (int i = 0; i < zs.length; i++) {
         colors[i] = this.pickColor(zs[i]);
      }
      System.out.println(track++);
      return colors;
   }
   
   /**
    * @param z a Complex number
    * @return  true if the Complex number is not in the Julia Set;
    *    false if it is likely not to be in the Julia Set
    */
   private boolean isFarFromJuliaSet(Complex z) {
      for (int i = 0; i < NUM_ITERATIONS; i++) {
         z = this.lp.polynomial(z);
         if (z.abs() > FAR_ENOUGH || Double.isNaN(z.abs())) {
            return true;
         }
      }
      return false;
   }
   
   /**
    * 
    * @param z a Complex number
    * @return  the distance z is from the Julia set
    */
   private double distanceFromJuliaSet(Complex z) {
      Complex dz = new Complex(1, 0);
      int cnt = 1;
      if (z.abs() < 10) {
         while (cnt < 200) {
            dz = new Complex(2, 0).times(z).times(dz);
            z = this.lp.polynomial(z);
            if (z.abs() > 10 || Double.isNaN(z.abs())) {
               break;
            }
            cnt++;
         }
      }
      double distance = z.abs() * Math.log(z.abs()) / dz.abs();
      return distance;
   }
   
   /**
    * @param z a Complex number
    * @return
    *    if z is in this map of Complex numbers:
    *       an int[] of length 2, where:
    *       the first int represents the x coordinate and
    *       the second int represents the y coordinate;
    *    otherwise, null
    */
   public int[] findApproximateLocation(Complex z) {
      Complex topLeft = this.allPoints[0][0];
      double leftRe = topLeft.re();
      double topIm = topLeft.im();
      Complex bottomRight = this.allPoints[this.allPoints.length - 1]
            [this.allPoints[0].length - 1];
      double rightRe = bottomRight.re();
      double bottomIm = bottomRight.im();
      int x = (int) ((z.re() - leftRe) / (rightRe - leftRe) *
            this.allPoints.length);
      int y = (int) ((topIm - z.im()) / (topIm - bottomIm) *
            this.allPoints[0].length);
      return new int[] {x,y};
   }
}