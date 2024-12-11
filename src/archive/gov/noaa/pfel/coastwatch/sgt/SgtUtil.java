public class SgtUtil {
  /**
   * Saves an image as a gif. Currently this uses ImageMagick's "convert" (Windows or Linux) because
   * it does the best job at color reduction (and is fast and is cross-platform). This will
   * overwrite an existing file.
   *
   * @param bi
   * @param transparent the color to be made transparent
   * @param fullGifName but without the .gif at the end
   * @throws Exception if trouble
   */
  public static void saveAsTransparentGif(BufferedImage bi, Color transparent, String fullGifName)
  throws Exception {

    // POLICY: because this procedure may be used in more than one thread,
    // do work on unique temp files names using randomInt, then rename to proper file name.
    // If procedure fails half way through, there won't be a half-finished file.
    int randomInt = Math2.random(Integer.MAX_VALUE);

    // convert transparent color to be transparent
    long time = System.currentTimeMillis();
    Image image = Image2.makeImageBackgroundTransparent(bi, transparent, 10000);

    // convert image back to bufferedImage
    bi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics g = bi.getGraphics();
    g.drawImage(image, 0, 0, bi.getWidth(), bi.getHeight(), null);
    image = null; // encourage garbage collection

    // save as png
    ImageIO.write(bi, "gif", new File(fullGifName + randomInt + ".gif"));

    // "convert" to .gif
    // SSR.dosOrCShell(
    //     "convert " + fullGifName + randomInt + ".png" + " " + fullGifName + randomInt + ".gif",
    // 30);
    // File2.delete(fullGifName + randomInt + ".png");

    // try fancy color reduction algorithms
    // Image2.saveAsGif(Image2.reduceTo216Colors(bi), fullGifName + randomInt + ".gif");

    // try dithering
    // Image2.saveAsGif216(bi, fullGifName + randomInt + ".gif", true);

    // last step: rename to final gif name
    File2.rename(fullGifName + randomInt + ".gif", fullGifName + ".gif");

    if (verbose)
    String2.log(
        "SgtUtil.saveAsTransparentGif TIME=" + (System.currentTimeMillis() - time) + "ms\n");
    }
}
