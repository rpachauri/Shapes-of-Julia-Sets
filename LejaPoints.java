import java.io.*;
import java.util.*;

/**
 * @author Xiao Li
 * @author RyanPachauri
 * @version 2/24/17
 */
public class LejaPoints {
   //we need to keep track of all the points that the user wants the shape of
   //map of every point in the shape to a double representing their max
   // helps in calculating the next leja point
   private Map<Complex, Double> allPoints;
   private List<Complex> lejaPoints;
   private final double capE;
   private final double POLYNOMIAL_CONSTANT;
   private static final String CAP_E_FILE = "capE:";
   private static final String CONSTANT_FILE = "constant:";
   
   /**
    * @param fileName String that contains the information in the proper
    *    format for us to create a LejaPoints instance
    */
   @SuppressWarnings("resource")
   public LejaPoints(String fileName) {
      File file = new File(fileName);
      try {
         Scanner fileReader = new Scanner(file);
         String firstLine = fileReader.nextLine();
         Scanner firstLineReader = new Scanner(firstLine);
         String secondLine = fileReader.nextLine();
         Scanner secondLineReader = new Scanner(secondLine);
         if (!firstLineReader.next().equals(LejaPoints.CAP_E_FILE) ||
               !secondLineReader.next().equals(LejaPoints.CONSTANT_FILE)) {
            throw new IllegalArgumentException();
         }
         this.capE = firstLineReader.nextDouble();
         firstLineReader.close();
         this.POLYNOMIAL_CONSTANT = secondLineReader.nextDouble();
         secondLineReader.close();
         
         this.lejaPoints = new ArrayList<Complex>();
         while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            Scanner lineReader = new Scanner(line);
            Complex z = new Complex(lineReader.nextDouble(),
                  lineReader.nextDouble());
            lejaPoints.add(z);
            lineReader.close();
         }
         fileReader.close();
      } catch (FileNotFoundException e) {
         throw new IllegalArgumentException("File does not exist");
      }
   }
   
   /**
    * Initializes this LejaPoints instance to make sure that it stores all the
    *    necessary points for creating the shape
    * 
    * @param points     Set of all points representing a shape we'd like to
    *                   create
    * @param numLejaPoints the number of leja points to select from points
    * @param S very small constant to counteract the magnitude of numLejaPoints
    * @throws IllegalArgumentException if:
    *    1. lejaPoint is not in points
    *    2. numLejaPoints is greater than points.size()
    */
   public LejaPoints(Set<Complex> points, int n, double s) {
      this(points, points.iterator().next(), n, s);
   }
   
   /**
    * Initializes this LejaPoints instance to make sure that it stores all the
    *    necessary points for creating the shape
    * 
    * @param points     Set of all points representing a shape we'd like to
    *                   create
    * @param lejaPoint  Complex number that we'd like to have as our first
    *                   lejaPoint
    * @param numLejaPoints the number of leja points to select from points
    * @throws IllegalArgumentException if:
    *    1. lejaPoint is not in points
    *    2. numLejaPoints is greater than points.size()
    */
   public LejaPoints(Set<Complex> points, Complex lejaPoint, int n, double s) {
      if (!points.contains(lejaPoint) || n > points.size()) {
         throw new IllegalArgumentException();
      }
      points.remove(lejaPoint);
      this.allPoints = new HashMap<Complex, Double>();
      for (Complex z : points) {
         this.allPoints.put(z, 1.0);
      }
      this.lejaPoints = new ArrayList<Complex>();
      this.lejaPoints.add(lejaPoint);
      //after this, we should have numLejaPoints - 1
      double exp = 1.0 / n;
      for (int i = 1; i < n - 1; i++) {
         this.getNextLejaPoint(exp);
      }
      this.capE = this.getNextLejaPoint(exp);
      this.POLYNOMIAL_CONSTANT = Math.exp(-1 * n * s / 2);
      System.out.println("lejaPolynomialConstant: " + this.POLYNOMIAL_CONSTANT);
      System.out.println("cap(E): " + this.capE);
   }
   
   /**
    * Assumes that this instance contains at least one leja point
    * Finds the next leja point and adds it to this set of leja points
    * @param exp
    * @return capE
    * @throws IllegalStateException if lejaPoints is empty
    */
   private double getNextLejaPoint(double exp) {
      if (this.lejaPoints.isEmpty() || this.allPoints.keySet().isEmpty()) {
         throw new IllegalStateException();
      }
      double max = -1;
      //has to be set to a dummy value in order to compile
      Complex nextLejaPoint = new Complex(0, 0);
      for (Complex z : this.allPoints.keySet()) {
         //only need to multiply the difference between z and the last leja point
         //because we store those multiples
         Complex lastLejaPoint = this.lejaPoints.get(this.lejaPoints.size() - 1);
         double diff = z.minus(lastLejaPoint).abs();
         double product = this.allPoints.get(z) * Math.pow(diff, exp);
         this.allPoints.put(z, product);
         if (max < product) {
            max = product;
            nextLejaPoint = z;
         }
      }
      this.lejaPoints.add(nextLejaPoint);
      //since we add it to lejaPoints, we don't need it when calculating anymore
      this.allPoints.remove(nextLejaPoint);
      return max;
   }
   
   /**
    * @return  the leja points
    */
   public Set<Complex> getLejaPoints() {
      //returns a copy to protect the field instance
      Set<Complex> lejaCopy = new HashSet<Complex>();
      lejaCopy.addAll(lejaPoints);
      return lejaCopy;
   }
   
   /**
    * This polynomial is defined by Malik Younsi. For more information, please
    *    refer to his research
    * 
    * @param z a Complex number passed into the function
    * @return  the Complex number as a result of z being put through this
    *          function
    */
   public Complex polynomial(Complex z) {
      Complex result = z;
      for (Complex leja : this.lejaPoints) {
         result = result.times(z.minus(leja));
         double re = result.re() / this.capE;
         double im = result.im() / this.capE;
         result = new Complex(re, im);
      }
      return new Complex(result.re() * this.POLYNOMIAL_CONSTANT,
            result.im() * this.POLYNOMIAL_CONSTANT);
   }
   
   /**
    * @param output PrintStream object used to print information about this
    *    object
    */
   public void export(PrintStream output) {
      output.println(LejaPoints.CAP_E_FILE + " " + this.capE);
      output.println(LejaPoints.CONSTANT_FILE + " " +
            this.POLYNOMIAL_CONSTANT);
      for (Complex z : this.lejaPoints) {
         output.println(z.re() + " " + z.im());
      }
      output.close();
   }
}
