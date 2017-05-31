import java.awt.Color;

/**
 * Class that makes it easier to use images for Plotting Julia sets
 * 
 * @author RyanPachauri
 * @version 5/16/17
 */
public class ImageStandardizer {
   
   private static final String FILE_NAME = "fdh julia.png";
   private static final String BOX_FILE_NAME = "fdh box.png";
   private static final int MAX_COLOR_RANGE = 255;
   
   public static final Color IN_IMAGE = Color.BLACK;
   public static final Color NOT_IN_IMAGE = Color.WHITE;

   public static void main(String[] args) {
      insertBox();
   }
   
   /**
    * It is easier to plot the initial shape with the zoom area immediately
    *    before plotting the Julia set with the zoom area.
    * Allows for combining the two Pictures to show the initial Julia set with
    *    the zoom area.
    * This method is used to help us determine if the proposed area to zoom is
    *    appropriate
    */
   public static void insertBox() {
      Picture imgJulia = new Picture(FILE_NAME);
      Picture imgBox = new Picture(BOX_FILE_NAME);
      for (int x = 0; x < imgBox.width(); x++) {
         for (int y = 0; y < imgBox.height(); y++) {
            if (imgBox.get(x, y).equals(LejaPlotter.ZOOM_AREA)) {
               imgJulia.set(x, y, LejaPlotter.ZOOM_AREA);
            }
         }
      }
      imgJulia.save(FILE_NAME);
   }
   
   /**
    * Standardizes a Picture to contain only two colors.
    */
   public static void standardize() {
      Picture img = new Picture(FILE_NAME);
      for (int x = 0; x < img.width(); x++) {
         for (int y = 0; y < img.height(); y++) {
            if (isCloseToWhite(img.get(x, y))) {
               img.set(x, y, ImageStandardizer.NOT_IN_IMAGE);
            } else {
               img.set(x, y, ImageStandardizer.IN_IMAGE);
            }
         }
      }
      img.save(FILE_NAME);
   }
   
   /**
    * @param c a Color object
    * @return  true if the given Color object is closer to white than it is
    *    to black
    */
   private static boolean isCloseToWhite(Color c) {
      int red = c.getRed();
      int green = c.getGreen();
      int blue = c.getBlue();
      return (red + green + blue) / 3 > MAX_COLOR_RANGE / 2;
   }

}
