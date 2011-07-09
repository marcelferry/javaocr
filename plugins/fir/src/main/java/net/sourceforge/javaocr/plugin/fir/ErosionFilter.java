package net.sourceforge.javaocr.plugin.fir;

import net.sourceforge.javaocr.Image;

/**
 * Apply erosion by structuring element to binarized source image.
 * TODO FIXME Works for binarized images only!
 * @author Andrea De Pasquale
 */
public class ErosionFilter extends AbstractMorphologyFilter {

  /**
   * Create an <code>ErosionFilter</code> with default values
   * of 255 for the foreground and 0 for the background.
   * @param strElem Structuring element
   * @param dest Output image
   */
  public ErosionFilter(Image strElem, Image dest) {
    this(strElem, dest, 255, 0);
  }
  
  /**
   * Create an <code>ErosionFilter</code>.
   * @param strElem Structuring element
   * @param dest Output image
   * @param full Foreground value 
   * @param empty Background value
   */
  public ErosionFilter(Image strElem, Image dest, int full, int empty) {
    super(strElem, dest, full, empty);
  }
  
  /**
   * Apply erosion to the given image, leaving borders unprocessed.
   * @param image Input image
   */
  public void process(Image image) {
    final int imageW = image.getWidth();
    final int imageH = image.getHeight();
    
    // copy four borders as they are
    image.chisel(0, 0, imageW, sizeT).copy(destImage.chisel(0, 0, imageW, sizeT));
    image.chisel(0, imageH-sizeB, imageW, sizeB).copy(destImage.chisel(0, imageH-sizeB, imageW, sizeB));
    image.chisel(0, 0, sizeL, imageH).copy(destImage.chisel(0, 0, sizeL, imageH));
    image.chisel(imageW-sizeR, 0, sizeR, imageH).copy(destImage.chisel(imageW-sizeR, 0, sizeR, imageH));
    
    // process valid area of the image
    for (int y = sizeT; y < imageH-sizeB; ++y) {
      for (int x = sizeL; x < imageW-sizeR; ++x) {
        Image nImage = image.chisel(x-sizeL, y-sizeT, seImageW, seImageH);
        destImage.put(x, y, processNeighborhood(nImage));
      }
    }
  }

  protected int processNeighborhood(Image nImage) {
    for (int i = 0; i < seImageH; ++i) {
      for (seImage.iterateH(i), nImage.iterateH(i); 
           seImage.hasNext() && nImage.hasNext(); ) {
        seImage.next(); nImage.next();
        if (seImage.get() == full && nImage.get() == empty)
          return empty;
      }
    }
    
    return full;
  }

}
