package gov.noaa.pfel.coastwatch.util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Path;

import com.cohort.array.DoubleArray;
import com.cohort.array.LongArray;
import com.cohort.array.StringArray;
import com.cohort.util.Calendar2;
import com.cohort.util.MustBe;
import com.cohort.util.String2;
import com.cohort.util.Test;

import gov.noaa.pfel.coastwatch.pointdata.Table;
import tags.TagAWS;
import tags.TagExternalOther;
import tags.TagLargeFile;
import tags.TagLocalERDDAP;
import tags.TagThredds;

class FileVisitorDNLSTests {
  /**
   * This tests THREDDS-related methods.
   */
  @org.junit.jupiter.api.Test
  @TagThredds
  void testThredds() throws Throwable {
    // String2.log("\n*** FileVisitorDNLS.testThredds");
    // boolean oReallyVerbose = reallyVerbose;
    // reallyVerbose = true;

    if (true)
      Test.knownProblem(
          "2020-10-22 FileVisitorDNLS.testThredds is not run now because the sourceUrl often stalls: https://data.nodc.noaa.gov/thredds");

    // String url =
    // "https://data.nodc.noaa.gov/thredds/catalog/aquarius/nodc_binned_V4.0/monthly/";
    // //catalog.html //was
    String url = "https://www.ncei.noaa.gov/thredds-ocean/catalog/aquarius/nodc_binned_V4.0/monthly/"; // catalog.html
                                                                                                       // //doesn't
                                                                                                       // exist. Where
                                                                                                       // is this
                                                                                                       // dataset now?
    String fileNameRegex = "sss_binned_L3_MON_SCI_V4.0_\\d{4}\\.nc";
    boolean recursive = true;
    String pathRegex = null;
    boolean dirsToo = true;
    StringArray childUrls = new StringArray();
    DoubleArray lastModified = new DoubleArray();
    LongArray fSize = new LongArray();

    // test TT_REGEX
    // note that String2.extractCaptureGroup fails if the string has line
    // terminators
    Test.ensureEqual(String2.extractRegex("q\r\na<tt>b</tt>c\r\nz", FileVisitorDNLS.TT_REGEX, 0),
        "<tt>b</tt>", "");

    // test error via addToThreddsUrlList
    // (yes, logged message includes directory name)
    String2.log("\nIntentional error:");
    String results = FileVisitorDNLS.addToThreddsUrlList(url + "testInvalidUrl", fileNameRegex,
        recursive, pathRegex, dirsToo, childUrls, lastModified, fSize);
    String expected = // fails very slowly!
        "java.io.IOException: HTTP status code=404 java.io.FileNotFoundException: https://www.ncei.noaa.gov/thredds-ocean/catalog/aquarius/nodc_binned_V4.0/monthly/testInvalidUrl/catalog.html\n";
    Test.ensureEqual(results.substring(0, expected.length()), expected, "results=\n" + results);

    // test addToThreddsUrlList
    childUrls = new StringArray();
    lastModified = new DoubleArray(); // epochSeconds
    fSize = new LongArray();
    FileVisitorDNLS.addToThreddsUrlList(url, fileNameRegex, recursive, pathRegex,
        dirsToo, childUrls, lastModified, fSize);

    results = childUrls.toNewlineString();
    expected = "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/\n" +
        "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/sss_binned_L3_MON_SCI_V4.0_2011.nc\n"
        +
        "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/sss_binned_L3_MON_SCI_V4.0_2012.nc\n"
        +
        "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/sss_binned_L3_MON_SCI_V4.0_2013.nc\n"
        +
        "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/sss_binned_L3_MON_SCI_V4.0_2014.nc\n"
        +
        "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/sss_binned_L3_MON_SCI_V4.0_2015.nc\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    results = lastModified.toString();
    expected = "NaN, 1.449908961E9, 1.449902223E9, 1.449887547E9, 1.449874773E9, 1.449861892E9";
    Test.ensureEqual(results, expected, "results=\n" + results);

    results = fSize.toString();
    expected = "9223372036854775807, 2723152, 6528434, 6528434, 6528434, 3267363";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // test via oneStep -- dirs
    Table table = FileVisitorDNLS.oneStep(url, fileNameRegex, recursive, pathRegex, true);
    results = table.dataToString();
    expected = // lastMod is longs, epochMilliseconds
        "directory,name,lastModified,size\n" +
            "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/,,,\n" +
            "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/,sss_binned_L3_MON_SCI_V4.0_2011.nc,1449908961000,2723152\n"
            +
            "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/,sss_binned_L3_MON_SCI_V4.0_2012.nc,1449902223000,6528434\n"
            +
            "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/,sss_binned_L3_MON_SCI_V4.0_2013.nc,1449887547000,6528434\n"
            +
            "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/,sss_binned_L3_MON_SCI_V4.0_2014.nc,1449874773000,6528434\n"
            +
            "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/,sss_binned_L3_MON_SCI_V4.0_2015.nc,1449861892000,3267363\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // test via oneStep -- no dirs
    table = FileVisitorDNLS.oneStep(url, fileNameRegex, recursive, pathRegex, false);
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/,sss_binned_L3_MON_SCI_V4.0_2011.nc,1449908961000,2723152\n"
        +
        "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/,sss_binned_L3_MON_SCI_V4.0_2012.nc,1449902223000,6528434\n"
        +
        "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/,sss_binned_L3_MON_SCI_V4.0_2013.nc,1449887547000,6528434\n"
        +
        "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/,sss_binned_L3_MON_SCI_V4.0_2014.nc,1449874773000,6528434\n"
        +
        "https://data.nodc.noaa.gov/thredds/fileServer/aquarius/nodc_binned_V4.0/monthly/,sss_binned_L3_MON_SCI_V4.0_2015.nc,1449861892000,3267363\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // reallyVerbose = oReallyVerbose;
    // String2.log("\n*** FileVisitorDNLS.testThredds finished successfully");
  }

  /**
   * This tests this class with the local file system.
   */
  @org.junit.jupiter.api.Test
  void testLocal() throws Throwable {
    // String2.log("\n*** FileVisitorDNLS.testLocal");
    // verbose = true;
    boolean doBigTest = true;
    String tPathRegex = null;
    Table table;
    long time;
    int n;
    String results, expected;
    String testDir = Path.of(FileVisitorDNLSTests.class.getResource("/fileNames").toURI()).toString();

    String expectedDir = testDir.replace("\\", "\\\\") + "\\\\";

    // recursive and dirToo and test \\ separator
    table = FileVisitorDNLS.oneStep(testDir, ".*\\.png", true, tPathRegex, true);
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        expectedDir + ",jplMURSST20150103090000.png,1421272444000,46482\n" +
        expectedDir + ",jplMURSST20150104090000.png,1420665738000,46586\n" +
        expectedDir + "sub\\\\,,1706019600813,0\n" +
        expectedDir + "sub\\\\,jplMURSST20150105090000.png,1420665704000,46549\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // recursive and !dirToo and test // separator
    table = FileVisitorDNLS.oneStep(testDir, ".*\\.png", true, tPathRegex, false);
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        expectedDir + ",jplMURSST20150103090000.png,1421272444000,46482\n" +
        expectedDir + ",jplMURSST20150104090000.png,1420665738000,46586\n" +
        expectedDir + "sub\\\\,jplMURSST20150105090000.png,1420665704000,46549\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // !recursive and dirToo
    table = FileVisitorDNLS.oneStep(testDir, ".*\\.png", false, tPathRegex, true);
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        expectedDir + ",jplMURSST20150103090000.png,1421272444000,46482\n" +
        expectedDir + ",jplMURSST20150104090000.png,1420665738000,46586\n" +
        expectedDir + "sub\\\\,,1706019600813,0\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // !recursive and !dirToo
    table = FileVisitorDNLS.oneStep(testDir, ".*\\.png", false, tPathRegex, false);
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        expectedDir + ",jplMURSST20150103090000.png,1421272444000,46482\n" +
        expectedDir + ",jplMURSST20150104090000.png,1420665738000,46586\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // ***
    // oneStepDouble
    table = FileVisitorDNLS.oneStepDouble(testDir, ".*\\.png", true, tPathRegex, true);
    results = table.toString();
    expected = "{\n" +
        "dimensions:\n" +
        "\trow = 4 ;\n" +
        "\tdirectory_strlen = " + (expectedDir.length() - 2) + " ;\n" +
        "\tname_strlen = 27 ;\n" +
        "variables:\n" +
        "\tchar directory(row, directory_strlen) ;\n" +
        "\t\tdirectory:ioos_category = \"Identifier\" ;\n" +
        "\t\tdirectory:long_name = \"Directory\" ;\n" +
        "\tchar name(row, name_strlen) ;\n" +
        "\t\tname:ioos_category = \"Identifier\" ;\n" +
        "\t\tname:long_name = \"File Name\" ;\n" +
        "\tdouble lastModified(row) ;\n" +
        "\t\tlastModified:ioos_category = \"Time\" ;\n" +
        "\t\tlastModified:long_name = \"Last Modified\" ;\n" +
        "\t\tlastModified:units = \"seconds since 1970-01-01T00:00:00Z\" ;\n" +
        "\tdouble size(row) ;\n" +
        "\t\tsize:ioos_category = \"Other\" ;\n" +
        "\t\tsize:long_name = \"Size\" ;\n" +
        "\t\tsize:units = \"bytes\" ;\n" +
        "\n" +
        "// global attributes:\n" +
        "}\n" +
        "directory,name,lastModified,size\n" +
        expectedDir + ",jplMURSST20150103090000.png,1.421272444E9,46482.0\n" +
        expectedDir + ",jplMURSST20150104090000.png,1.420665738E9,46586.0\n" +
        expectedDir + "sub\\\\,,1.706019600813E9,0.0\n" +
        expectedDir + "sub\\\\,jplMURSST20150105090000.png,1.420665704E9,46549.0\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // ***
    // oneStepAccessibleViaFiles
    table = FileVisitorDNLS.oneStepDoubleWithUrlsNotDirs(testDir, ".*\\.png",
        true, tPathRegex,
        "http://localhost:8080/cwexperimental/files/testFileNames/");
    results = table.toString();
    expected = "{\n" +
        "dimensions:\n" +
        "\trow = 3 ;\n" +
        "\turl_strlen = 88 ;\n" +
        "\tname_strlen = 27 ;\n" +
        "variables:\n" +
        "\tchar url(row, url_strlen) ;\n" +
        "\t\turl:ioos_category = \"Identifier\" ;\n" +
        "\t\turl:long_name = \"URL\" ;\n" +
        "\tchar name(row, name_strlen) ;\n" +
        "\t\tname:ioos_category = \"Identifier\" ;\n" +
        "\t\tname:long_name = \"File Name\" ;\n" +
        "\tdouble lastModified(row) ;\n" +
        "\t\tlastModified:ioos_category = \"Time\" ;\n" +
        "\t\tlastModified:long_name = \"Last Modified\" ;\n" +
        "\t\tlastModified:units = \"seconds since 1970-01-01T00:00:00Z\" ;\n" +
        "\tdouble size(row) ;\n" +
        "\t\tsize:ioos_category = \"Other\" ;\n" +
        "\t\tsize:long_name = \"Size\" ;\n" +
        "\t\tsize:units = \"bytes\" ;\n" +
        "\n" +
        "// global attributes:\n" +
        "}\n" +
        "url,name,lastModified,size\n" +
        "http://localhost:8080/cwexperimental/files/testFileNames/jplMURSST20150103090000.png,jplMURSST20150103090000.png,1.421272444E9,46482.0\n"
        +
        "http://localhost:8080/cwexperimental/files/testFileNames/jplMURSST20150104090000.png,jplMURSST20150104090000.png,1.420665738E9,46586.0\n"
        +
        "http://localhost:8080/cwexperimental/files/testFileNames/sub/jplMURSST20150105090000.png,jplMURSST20150105090000.png,1.420665704E9,46549.0\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // *** huge dir
    String unexpected = "\nUnexpected FileVisitorDNLS error (but /data/gtspp/temp dir has variable nFiles):\n";

    if (doBigTest) {
      for (int attempt = 0; attempt < 2; attempt++) {
        try {
          // forward slash in huge directory
          time = System.currentTimeMillis();
          table = FileVisitorDNLS.oneStep("/data/gtspp/temp", ".*\\.nc", false, tPathRegex, false);
          time = System.currentTimeMillis() - time;
          // 2014-11-25 98436 files in 410ms
          StringArray directoryPA = (StringArray) table.getColumn(FileVisitorDNLS.DIRECTORY);
          String2.log("forward test: n=" + directoryPA.size() + " time=" + time + "ms");
          if (directoryPA.size() < 1000) {
            String2.log(directoryPA.size() + " files. Not a good test.");
          } else {
            Test.ensureBetween(time / (double) directoryPA.size(), 2e-3, 8e-3,
                "ms/file (4.1e-3 expected)");
            String dir0 = directoryPA.get(0);
            String2.log("forward slash test: dir0=" + dir0);
            Test.ensureTrue(dir0.indexOf('\\') < 0, "");
            Test.ensureTrue(dir0.endsWith("/"), "");
          }
        } catch (Throwable t) {
          String2.pressEnterToContinue(unexpected +
              MustBe.throwableToString(t));
        }
      }

      for (int attempt = 0; attempt < 2; attempt++) {
        try {
          // backward slash in huge directory
          time = System.currentTimeMillis();
          table = FileVisitorDNLS.oneStep("\\data\\gtspp\\temp", ".*\\.nc", false, tPathRegex, false);
          time = System.currentTimeMillis() - time;
          // 2014-11-25 98436 files in 300ms
          StringArray directoryPA = (StringArray) table.getColumn(FileVisitorDNLS.DIRECTORY);
          String2.log("backward test: n=" + directoryPA.size() + " time=" + time + "ms");
          if (directoryPA.size() < 1000) {
            String2.log(directoryPA.size() + " files. Not a good test.");
          } else {
            Test.ensureBetween(time / (double) directoryPA.size(), 1e-3, 8e-3,
                "ms/file (3e-3 expected)");
            String dir0 = directoryPA.get(0);
            String2.log("backward slash test: dir0=" + dir0);
            Test.ensureTrue(dir0.indexOf('/') < 0, "");
            Test.ensureTrue(dir0.endsWith("\\"), "");
          }
        } catch (Throwable t) {
          String2.pressEnterToContinue(unexpected +
              MustBe.throwableToString(t));
        }
      }
    }
    String2.log("\n*** FileVisitorDNLS.testLocal finished.");
  }

  /**
   * This tests this class with Amazon AWS S3 file system.
   * Your S3 credentials must be in
   * <br>
   * ~/.aws/credentials on Linux, OS X, or Unix
   * <br>
   * C:\Users\USERNAME\.aws\credentials on Windows
   * See https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingMetadata.html
   * See https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingBucket.html
   * See https://docs.aws.amazon.com/AmazonS3/latest/dev/ListingKeysHierarchy.html
   * See
   * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html#setup-credentials
   */
  @org.junit.jupiter.api.Test
  @TagAWS
  void testAWSS3() throws Throwable {
    // String2.log("\n*** FileVisitorDNLS.testAWSS3");

    // verbose = true;
    Table table;
    long time;
    int n;
    String results, expected;
    // this works in browser: http://nasanex.s3.us-west-2.amazonaws.com
    // the full parent here doesn't work in a browser.
    // But ERDDAP knows that "nasanex" is the bucket name and
    // "NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/" is the prefix.
    // See https://docs.aws.amazon.com/AmazonS3/latest/dev/ListingKeysHierarchy.html
    String parent = "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/";
    String child = "CONUS/";
    String pathRegex = null;

    {

      // !recursive and dirToo
      table = FileVisitorDNLS.oneStep(
          "https://nasanex.s3.us-west-2.amazonaws.com",
          ".*", false, ".*", true); // fileNameRegex, tRecursive, pathRegex, tDirectoriesToo
      results = table.dataToString();
      expected = "directory,name,lastModified,size\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/AVHRR/,,,\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/CMIP5/,,,\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/Landsat/,,,\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/LOCA/,,,\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/MAIAC/,,,\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/MODIS/,,,\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NAIP/,,,\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/,,,\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-GDDP/,,,\n";
      Test.ensureEqual(results, expected, "results=\n" + results);

      // !recursive and dirToo
      table = FileVisitorDNLS.oneStep(
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/",
          ".*", false, ".*", true); // fileNameRegex, tRecursive, pathRegex, tDirectoriesToo
      results = table.dataToString();
      expected = "directory,name,lastModified,size\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/,,,\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/,doi.txt,1380418295000,35\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/,nex-dcp30-s3-files.json,1473288687000,2717227\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/,,,\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/CONTRIB/,,,\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/NEX-quartile/,,,\n";
      Test.ensureEqual(results, expected, "results=\n" + results);

      // !recursive and !dirToo
      table = FileVisitorDNLS.oneStep(
          "https://nasanex.s3.us-west-2.amazonaws.com",
          ".*", false, ".*", false); // fileNameRegex, tRecursive, pathRegex, tDirectoriesToo
      results = table.dataToString();
      expected = "directory,name,lastModified,size\n";
      Test.ensureEqual(results, expected, "results=\n" + results);

      // !recursive and !dirToo
      table = FileVisitorDNLS.oneStep(
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/",
          ".*", false, ".*", false); // fileNameRegex, tRecursive, pathRegex, tDirectoriesToo
      results = table.dataToString();
      expected = "directory,name,lastModified,size\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/,doi.txt,1380418295000,35\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/,nex-dcp30-s3-files.json,1473288687000,2717227\n";
      Test.ensureEqual(results, expected, "results=\n" + results);

      // recursive and dirToo
      table = FileVisitorDNLS.oneStep(parent, ".*\\.nc", true, pathRegex, true);
      results = table.dataToString();
      expected = "directory,name,lastModified,size\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/,,,\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,,,\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,tasmin_amon_BCSD_rcp26_r1i1p1_CONUS_bcc-csm1-1_200601-201012.nc,1380652638000,1368229240\n"
          +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,tasmin_amon_BCSD_rcp26_r1i1p1_CONUS_bcc-csm1-1_201101-201512.nc,1380649780000,1368487462\n"
          +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,tasmin_amon_BCSD_rcp26_r1i1p1_CONUS_bcc-csm1-1_201601-202012.nc,1380651065000,1368894133\n";
      if (expected.length() > results.length())
        String2.log("results=\n" + results);
      Test.ensureEqual(results.substring(0, expected.length()), expected, "results=\n" + results);

      // recursive and !dirToo
      table = FileVisitorDNLS.oneStep(parent, ".*\\.nc", true, pathRegex, false);
      results = table.dataToString();
      expected = "directory,name,lastModified,size\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,tasmin_amon_BCSD_rcp26_r1i1p1_CONUS_bcc-csm1-1_200601-201012.nc,1380652638000,1368229240\n"
          +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,tasmin_amon_BCSD_rcp26_r1i1p1_CONUS_bcc-csm1-1_201101-201512.nc,1380649780000,1368487462\n"
          +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,tasmin_amon_BCSD_rcp26_r1i1p1_CONUS_bcc-csm1-1_201601-202012.nc,1380651065000,1368894133\n";
      if (expected.length() > results.length())
        String2.log("results=\n" + results);
      Test.ensureEqual(results.substring(0, expected.length()), expected, "results=\n" + results);

      // !recursive and dirToo
      table = FileVisitorDNLS.oneStep(parent + child, ".*\\.nc", false, pathRegex, true);
      results = table.dataToString();
      expected = "directory,name,lastModified,size\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,,,\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,tasmin_amon_BCSD_rcp26_r1i1p1_CONUS_bcc-csm1-1_200601-201012.nc,1380652638000,1368229240\n"
          +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,tasmin_amon_BCSD_rcp26_r1i1p1_CONUS_bcc-csm1-1_201101-201512.nc,1380649780000,1368487462\n"
          +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,tasmin_amon_BCSD_rcp26_r1i1p1_CONUS_bcc-csm1-1_201601-202012.nc,1380651065000,1368894133\n";
      if (expected.length() > results.length())
        String2.log("results=\n" + results);
      Test.ensureEqual(results.substring(0, expected.length()), expected, "results=\n" + results);

      // !recursive and !dirToo
      table = FileVisitorDNLS.oneStep(parent + child, ".*\\.nc", false, pathRegex, false);
      results = table.dataToString();
      expected = "directory,name,lastModified,size\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,tasmin_amon_BCSD_rcp26_r1i1p1_CONUS_bcc-csm1-1_200601-201012.nc,1380652638000,1368229240\n"
          +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,tasmin_amon_BCSD_rcp26_r1i1p1_CONUS_bcc-csm1-1_201101-201512.nc,1380649780000,1368487462\n"
          +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,tasmin_amon_BCSD_rcp26_r1i1p1_CONUS_bcc-csm1-1_201601-202012.nc,1380651065000,1368894133\n";
      if (expected.length() > results.length())
        String2.log("results=\n" + results);
      Test.ensureEqual(results.substring(0, expected.length()), expected, "results=\n" + results);
    } /* */

    // recursive and dirToo
    // reallyVerbose = true;
    // debugMode = true;
    parent = "https://noaa-goes17.s3.us-east-1.amazonaws.com/ABI-L1b-RadC/2018/338/";
    pathRegex = ".*";
    table = FileVisitorDNLS.oneStep(parent, ".*\\.nc", true, pathRegex, true);
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        "https://noaa-goes17.s3.us-east-1.amazonaws.com/ABI-L1b-RadC/2018/338/,,,\n" +
        "https://noaa-goes17.s3.us-east-1.amazonaws.com/ABI-L1b-RadC/2018/338/00/,,,\n" +
        "https://noaa-goes17.s3.us-east-1.amazonaws.com/ABI-L1b-RadC/2018/338/00/,OR_ABI-L1b-RadC-M3C01_G17_s20183380002190_e20183380004563_c20183380004595.nc,1582123265000,12524368\n"
        +
        "https://noaa-goes17.s3.us-east-1.amazonaws.com/ABI-L1b-RadC/2018/338/00/,OR_ABI-L1b-RadC-M3C01_G17_s20183380007190_e20183380009563_c20183380009597.nc,1582123265000,12357541\n"
        +
        "https://noaa-goes17.s3.us-east-1.amazonaws.com/ABI-L1b-RadC/2018/338/00/,OR_ABI-L1b-RadC-M3C01_G17_s20183380012190_e20183380014503_c20183380014536.nc,1582123255000,12187253\n";
    // before 2020-03-03 these were the first returned rows:
    // "https://noaa-goes17.s3.us-east-1.amazonaws.com/ABI-L1b-RadC/2018/338/16/,,,\n"
    // +
    // "https://noaa-goes17.s3.us-east-1.amazonaws.com/ABI-L1b-RadC/2018/338/16/,OR_ABI-L1b-RadC-M3C01_G17_s20183381637189_e20183381639562_c20183381639596.nc,1543941659000,9269699\n"
    // +
    // "https://noaa-goes17.s3.us-east-1.amazonaws.com/ABI-L1b-RadC/2018/338/16/,OR_ABI-L1b-RadC-M3C01_G17_s20183381642189_e20183381644502_c20183381644536.nc,1543942123000,9585452\n"
    // +
    // "https://noaa-goes17.s3.us-east-1.amazonaws.com/ABI-L1b-RadC/2018/338/16/,OR_ABI-L1b-RadC-M3C01_G17_s20183381647189_e20183381649562_c20183381649596.nc,1543942279000,9894495\n"
    // +
    // "https://noaa-goes17.s3.us-east-1.amazonaws.com/ABI-L1b-RadC/2018/338/16/,OR_ABI-L1b-RadC-M3C01_G17_s20183381652189_e20183381654562_c20183381654595.nc,1543942520000,10195765\n";
    if (expected.length() > results.length())
      String2.log("results=\n" + results);
    Test.ensureEqual(results.substring(0, expected.length()), expected, "results=\n" + results.substring(0, 1000));

    String2.log("\n*** FileVisitorDNLS.testAWSS3 finished.");

  }

  /**
   * This tests this class with Amazon AWS S3 file system and reading all from a
   * big directory.
   * Your S3 credentials must be in
   * <br>
   * ~/.aws/credentials on Linux, OS X, or Unix
   * <br>
   * C:\Users\USERNAME\.aws\credentials on Windows
   * See https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingMetadata.html
   * See https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingBucket.html
   * See https://docs.aws.amazon.com/AmazonS3/latest/dev/ListingKeysHierarchy.html
   * See
   * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html#setup-credentials
   */
  @org.junit.jupiter.api.Test
  @TagAWS
  void testBigAWSS3() throws Throwable {
    // String2.log("\n*** FileVisitorDNLS.testBigAWSS3");

    // verbose = true;
    // debugMode = true;
    Table table;
    long time;
    int n;
    String results, expected;
    // this works in browser: http://nasanex.s3.us-west-2.amazonaws.com
    // the full parent here doesn't work in a browser.
    // But ERDDAP knows that "nasanex" is the bucket name and
    // "NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/" is the prefix.
    // See https://docs.aws.amazon.com/AmazonS3/latest/dev/ListingKeysHierarchy.html
    String parent = "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/";
    String child = "CONUS/";
    String pathRegex = null;
    String fullResults = null;

    // test that the results are identical regardless of page size
    int maxKeys[] = new int[] { 10, 100, 1000 }; // expectedCount is ~870.
    int oS3Chunk = FileVisitorDNLS.S3_CHUNK_TO_FILE;
    FileVisitorDNLS.S3_CHUNK_TO_FILE = 8; // small, to test chunking to temp file
    for (int mk = 0; mk < maxKeys.length; mk++) {
      FileVisitorDNLS.S3_MAX_KEYS = maxKeys[mk];

      // recursive and dirToo
      table = FileVisitorDNLS.oneStep(parent, ".*\\.nc", true, pathRegex, true); // there is a .nc.md5 for each .nc, so
                                                                                 // oneStep filters client-side
      results = table.dataToString();
      expected = "directory,name,lastModified,size\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/,,,\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,,,\n" +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,tasmin_amon_BCSD_rcp26_r1i1p1_CONUS_bcc-csm1-1_200601-201012.nc,1380652638000,1368229240\n"
          +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,tasmin_amon_BCSD_rcp26_r1i1p1_CONUS_bcc-csm1-1_201101-201512.nc,1380649780000,1368487462\n"
          +
          "https://nasanex.s3.us-west-2.amazonaws.com/NEX-DCP30/BCSD/rcp26/mon/atmos/tasmin/r1i1p1/v1.0/CONUS/,tasmin_amon_BCSD_rcp26_r1i1p1_CONUS_bcc-csm1-1_201601-202012.nc,1380651065000,1368894133\n";
      if (expected.length() > results.length())
        String2.log("results=\n" + results);
      Test.ensureEqual(results.substring(0, expected.length()), expected, "results=\n" + results);

      // test that results are the same each time
      if (fullResults == null)
        fullResults = results;
      Test.ensureEqual(results, fullResults, "results=\n" + results + "\nfullResults=\n" + fullResults);

      String2.log("nLines=" + String2.countAll(results, "\n")); // 2021-11-30 441 but it processes ~890 items (half .nc,
                                                                // half .nc.md5)

    }
    // debugMode = false;
    FileVisitorDNLS.S3_CHUNK_TO_FILE = oS3Chunk;
  }

  /**
   * This tests this class with Amazon AWS S3 file system.
   * Your S3 credentials must be in
   * <br>
   * ~/.aws/credentials on Linux, OS X, or Unix
   * <br>
   * C:\Users\USERNAME\.aws\credentials on Windows
   * See https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingMetadata.html
   * See https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingBucket.html
   * See https://docs.aws.amazon.com/AmazonS3/latest/dev/ListingKeysHierarchy.html
   * See
   * https://docs.aws.amazon.com/sdk-for-java/?id=docs_gateway#aws-sdk-for-java,-version-1
   * .
   * https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html#credentials-file-format
   */
  @org.junit.jupiter.api.Test
  @TagAWS
  void testPrivateAWSS3() throws Throwable {
    // String2.log("\n*** FileVisitorDNLS.testPrivateAWSS3");

    // verbose = true;
    Table table;
    long time;
    int n;
    String results, expected;
    // 2021-04-16 created a bucket:
    // log into AWS console as root (or IAM?)
    // Services : S3 : Create bucket: name=bobsimonsdata, region us-east-1, block
    // all public access, versioning=disabled, encryption=false,
    // upload files: Create folder, then Upload files

    String bucket = "https://bobsimonsdata.s3.us-east-1.amazonaws.com/";
    String dir = bucket + "erdQSwind1day/";
    String pathRegex = null;

    // !recursive and dirToo
    table = FileVisitorDNLS.oneStep(bucket, ".*", false, ".*", true); // fileNameRegex, tRecursive, pathRegex,
                                                                      // tDirectoriesToo
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/ascii/,,,\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,,,\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/testMediaFiles/,,,\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // !recursive and dirToo
    table = FileVisitorDNLS.oneStep(dir, ".*", false, ".*", true); // fileNameRegex, tRecursive, pathRegex,
                                                                   // tDirectoriesToo
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,,,\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,bad.nc,1620243280000,39102\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,BadFileNoExtension,1620243280000,39102\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,erdQSwind1day_20080101_03.nc.gz,1620243280000,10478645\n"
        +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,erdQSwind1day_20080104_07.nc,1620243281000,49790172\n"
        +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/subfolder/,,,\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // !recursive and !dirToo
    table = FileVisitorDNLS.oneStep(bucket, ".*", false, ".*", false); // fileNameRegex, tRecursive, pathRegex,
                                                                       // tDirectoriesToo
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // !recursive and !dirToo
    table = FileVisitorDNLS.oneStep(dir, ".*", false, ".*", false); // fileNameRegex, tRecursive, pathRegex,
                                                                    // tDirectoriesToo
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,bad.nc,1620243280000,39102\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,BadFileNoExtension,1620243280000,39102\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,erdQSwind1day_20080101_03.nc.gz,1620243280000,10478645\n"
        +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,erdQSwind1day_20080104_07.nc,1620243281000,49790172\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // recursive and dirToo
    table = FileVisitorDNLS.oneStep(bucket, ".*\\.nc", true, pathRegex, true);
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/,,,\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/ascii/,,,\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,,,\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,bad.nc,1620243280000,39102\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,erdQSwind1day_20080104_07.nc,1620243281000,49790172\n"
        +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/subfolder/,,,\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/subfolder/,erdQSwind1day_20080108_10.nc,1620243280000,37348564\n"
        +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/testMediaFiles/,,,\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // recursive and !dirToo
    table = FileVisitorDNLS.oneStep(bucket, ".*\\.nc", true, pathRegex, false);
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,bad.nc,1620243280000,39102\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,erdQSwind1day_20080104_07.nc,1620243281000,49790172\n"
        +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/subfolder/,erdQSwind1day_20080108_10.nc,1620243280000,37348564\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // !recursive and dirToo
    table = FileVisitorDNLS.oneStep(dir, ".*\\.nc", false, pathRegex, true);
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,,,\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,bad.nc,1620243280000,39102\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,erdQSwind1day_20080104_07.nc,1620243281000,49790172\n"
        +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/subfolder/,,,\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // !recursive and !dirToo
    table = FileVisitorDNLS.oneStep(dir, ".*\\.nc", false, pathRegex, false);
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,bad.nc,1620243280000,39102\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/,erdQSwind1day_20080104_07.nc,1620243281000,49790172\n";
    if (expected.length() > results.length())
      String2.log("results=\n" + results);
    Test.ensureEqual(results, expected, "results=\n" + results);

    // recursive and dirToo
    // reallyVerbose = true;
    // debugMode = true;
    pathRegex = ".*";
    table = FileVisitorDNLS.oneStep(dir + "subfolder/", ".*\\.nc", true, pathRegex, true);
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/subfolder/,,,\n" +
        "https://bobsimonsdata.s3.us-east-1.amazonaws.com/erdQSwind1day/subfolder/,erdQSwind1day_20080108_10.nc,1620243280000,37348564\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // String2.log("\n*** FileVisitorDNLS.testPrivateAWSS3 finished.");

  }

  /**
   * This tests Hyrax-related methods.
   */
  @org.junit.jupiter.api.Test
  @TagExternalOther
  void testHyrax() throws Throwable {
    // String2.log("\n*** FileVisitorDNLS.testHyrax()\n");
    // reallyVerbose = true;
    // debugMode=true;

    // before 2018-08-17 podaac-opendap caused
    // "javax.net.ssl.SSLProtocolException: handshake alert: unrecognized_name"
    // error
    // String url =
    // "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/";
    // //contents.html
    // so I used domain name shown on digital certificate: opendap.jpl.nasa.gov for
    // tests below
    // But now podaac-opendap works.
    String url = "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/"; // contents.html
    String fileNameRegex = "month_198(8|9).*flk\\.nc\\.gz";
    boolean recursive = true;
    String pathRegex = null;
    boolean dirsToo = true;
    StringArray childUrls = new StringArray();
    DoubleArray lastModified = new DoubleArray();
    LongArray size = new LongArray();
    String results, expected;

    // test error via addToHyraxUrlList
    // (yes, logged message includes directory name)
    String2.log("\nIntentional error:");
    results = FileVisitorDNLS.addToHyraxUrlList(url + "testInvalidUrl", fileNameRegex,
        recursive, pathRegex, dirsToo, childUrls, lastModified, size);
    expected = "java.io.IOException: HTTP status code=404 java.io.FileNotFoundException: https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/testInvalidUrl/contents.html\n";
    Test.ensureEqual(results.substring(0, expected.length()), expected, "results=\n" + results);

    // test addToHyraxUrlList
    childUrls = new StringArray();
    lastModified = new DoubleArray();
    size = new LongArray();
    results = FileVisitorDNLS.addToHyraxUrlList(url, fileNameRegex, recursive,
        pathRegex, dirsToo, childUrls, lastModified, size);
    Test.ensureEqual(results, "", "results=\n" + results);
    Table table = new Table();
    table.addColumn("URL", childUrls);
    table.addColumn("lastModified", lastModified);
    table.addColumn("size", size);
    results = table.dataToString();
    expected = "URL,lastModified,size\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880101_v11l35flk.nc.gz,1.336863115E9,4981045\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880201_v11l35flk.nc.gz,1.336723222E9,5024372\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880301_v11l35flk.nc.gz,1.336546575E9,5006043\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880401_v11l35flk.nc.gz,1.336860015E9,4948285\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880501_v11l35flk.nc.gz,1.336835143E9,4914250\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880601_v11l35flk.nc.gz,1.336484405E9,4841084\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880701_v11l35flk.nc.gz,1.336815079E9,4837417\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880801_v11l35flk.nc.gz,1.336799789E9,4834242\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880901_v11l35flk.nc.gz,1.336676042E9,4801865\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19881001_v11l35flk.nc.gz,1.336566352E9,4770289\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19881101_v11l35flk.nc.gz,1.336568382E9,4769160\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19881201_v11l35flk.nc.gz,1.336838712E9,4866335\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890101_v11l35flk.nc.gz,1.336886548E9,5003981\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890201_v11l35flk.nc.gz,1.336268373E9,5054907\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890301_v11l35flk.nc.gz,1.336605483E9,4979393\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890401_v11l35flk.nc.gz,1.336350339E9,4960865\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890501_v11l35flk.nc.gz,1.336551575E9,4868541\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890601_v11l35flk.nc.gz,1.336177278E9,4790364\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890701_v11l35flk.nc.gz,1.336685187E9,4854943\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890801_v11l35flk.nc.gz,1.336534686E9,4859216\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890901_v11l35flk.nc.gz,1.33622953E9,4838390\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19891001_v11l35flk.nc.gz,1.336853599E9,4820645\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19891101_v11l35flk.nc.gz,1.336882933E9,4748166\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19891201_v11l35flk.nc.gz,1.336748115E9,4922858\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1990/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1991/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1992/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1993/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1994/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1995/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1996/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1997/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1998/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1999/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/2000/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/2001/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/2002/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/2003/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/2004/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/2005/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/2006/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/2007/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/2008/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/2009/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/2010/,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/2011/,,\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // test getUrlsFromHyraxCatalog
    String resultsAr[] = FileVisitorDNLS.getUrlsFromHyraxCatalog(url, fileNameRegex, recursive,
        pathRegex);
    String expectedAr[] = new String[] {
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880101_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880201_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880301_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880401_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880501_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880601_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880701_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880801_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19880901_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19881001_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19881101_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1988/month_19881201_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890101_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890201_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890301_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890401_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890501_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890601_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890701_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890801_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19890901_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19891001_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19891101_v11l35flk.nc.gz",
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1989/month_19891201_v11l35flk.nc.gz" };
    Test.ensureEqual(resultsAr, expectedAr, "results=\n" + results);

    // different test of addToHyraxUrlList
    childUrls = new StringArray();
    lastModified = new DoubleArray();
    LongArray fSize = new LongArray(); // test that it will call setMaxIsMV(true)
    url = "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/"; // startUrl,
    fileNameRegex = "month_[0-9]{8}_v11l35flk\\.nc\\.gz"; // fileNameRegex,
    recursive = true;
    results = FileVisitorDNLS.addToHyraxUrlList(url, fileNameRegex, recursive, pathRegex, dirsToo,
        childUrls, lastModified, fSize);
    Test.ensureEqual(results, "", "results=\n" + results);

    results = childUrls.toNewlineString();
    expected = "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/month_19870701_v11l35flk.nc.gz\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/month_19870801_v11l35flk.nc.gz\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/month_19870901_v11l35flk.nc.gz\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/month_19871001_v11l35flk.nc.gz\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/month_19871101_v11l35flk.nc.gz\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/month_19871201_v11l35flk.nc.gz\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    results = lastModified.toString();
    expected = "NaN, 1.336609915E9, 1.336785444E9, 1.336673639E9, 1.336196561E9, 1.336881763E9, 1.336705731E9";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // test via oneStep -- dirs
    table = FileVisitorDNLS.oneStep(url, fileNameRegex, recursive, pathRegex, true);
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/,,,\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/,month_19870701_v11l35flk.nc.gz,1336609915000,4807310\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/,month_19870801_v11l35flk.nc.gz,1336785444000,4835774\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/,month_19870901_v11l35flk.nc.gz,1336673639000,4809582\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/,month_19871001_v11l35flk.nc.gz,1336196561000,4803285\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/,month_19871101_v11l35flk.nc.gz,1336881763000,4787239\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/,month_19871201_v11l35flk.nc.gz,1336705731000,4432696\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // test via oneStep -- no dirs
    table = FileVisitorDNLS.oneStep(url, fileNameRegex, recursive, pathRegex, false);
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/,month_19870701_v11l35flk.nc.gz,1336609915000,4807310\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/,month_19870801_v11l35flk.nc.gz,1336785444000,4835774\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/,month_19870901_v11l35flk.nc.gz,1336673639000,4809582\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/,month_19871001_v11l35flk.nc.gz,1336196561000,4803285\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/,month_19871101_v11l35flk.nc.gz,1336881763000,4787239\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ccmp/L3.5a/monthly/flk/1987/,month_19871201_v11l35flk.nc.gz,1336705731000,4432696\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

  }

  /**
   * This tests Hyrax-related methods with the JPL MUR dataset.
   */
  @org.junit.jupiter.api.Test
  @TagExternalOther
  void testHyraxMUR() throws Throwable {
    // String2.log("\n*** FileVisitorDNLS.testHyraxMUR()\n");
    // reallyVerbose = true;
    // debugMode=true;

    String url = "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ghrsst/data/GDS2/L4/GLOB/JPL/MUR/v4.1/"; // contents.html
    String fileNameRegex = "[0-9]{14}-JPL-L4_GHRSST-SSTfnd-MUR-GLOB-v02\\.0-fv04\\.1\\.nc";
    boolean recursive = true;
    String pathRegex = ".*/v4\\.1/2018/(|01./)"; // for test, just get 01x dirs/files. Read regex:
                                                 // "(|2018/(|/[0-9]{3}/))";
    boolean dirsToo = false;
    StringArray childUrls = new StringArray();

    // test via oneStep -- no dirs
    Table table = FileVisitorDNLS.oneStep(url, fileNameRegex, recursive, pathRegex, false);
    String results = table.dataToString();
    String expected = "directory,name,lastModified,size\n" + // lastMod is epochMillis
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ghrsst/data/GDS2/L4/GLOB/JPL/MUR/v4.1/2018/010/,20180110090000-JPL-L4_GHRSST-SSTfnd-MUR-GLOB-v02.0-fv04.1.nc,1526396428000,400940089\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ghrsst/data/GDS2/L4/GLOB/JPL/MUR/v4.1/2018/011/,20180111090000-JPL-L4_GHRSST-SSTfnd-MUR-GLOB-v02.0-fv04.1.nc,1526400024000,402342953\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ghrsst/data/GDS2/L4/GLOB/JPL/MUR/v4.1/2018/012/,20180112090000-JPL-L4_GHRSST-SSTfnd-MUR-GLOB-v02.0-fv04.1.nc,1526403626000,407791965\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ghrsst/data/GDS2/L4/GLOB/JPL/MUR/v4.1/2018/013/,20180113090000-JPL-L4_GHRSST-SSTfnd-MUR-GLOB-v02.0-fv04.1.nc,1526407232000,410202577\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ghrsst/data/GDS2/L4/GLOB/JPL/MUR/v4.1/2018/014/,20180114090000-JPL-L4_GHRSST-SSTfnd-MUR-GLOB-v02.0-fv04.1.nc,1526410828000,412787416\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ghrsst/data/GDS2/L4/GLOB/JPL/MUR/v4.1/2018/015/,20180115090000-JPL-L4_GHRSST-SSTfnd-MUR-GLOB-v02.0-fv04.1.nc,1526414431000,408049023\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ghrsst/data/GDS2/L4/GLOB/JPL/MUR/v4.1/2018/016/,20180116090000-JPL-L4_GHRSST-SSTfnd-MUR-GLOB-v02.0-fv04.1.nc,1526418038000,398060630\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ghrsst/data/GDS2/L4/GLOB/JPL/MUR/v4.1/2018/017/,20180117090000-JPL-L4_GHRSST-SSTfnd-MUR-GLOB-v02.0-fv04.1.nc,1526421637000,388221460\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ghrsst/data/GDS2/L4/GLOB/JPL/MUR/v4.1/2018/018/,20180118090000-JPL-L4_GHRSST-SSTfnd-MUR-GLOB-v02.0-fv04.1.nc,1526425240000,381433871\n"
        +
        "https://podaac-opendap.jpl.nasa.gov/opendap/allData/ghrsst/data/GDS2/L4/GLOB/JPL/MUR/v4.1/2018/019/,20180119090000-JPL-L4_GHRSST-SSTfnd-MUR-GLOB-v02.0-fv04.1.nc,1526428852000,390892954\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // debugMode=false;
  }

  /**
   * This tests a WAF-related (Web Accessible Folder) methods on an ERDDAP "files"
   * directory.
   */
  @org.junit.jupiter.api.Test
  void testErddapFilesWAF() throws Throwable {
    String2.log("\n*** FileVisitorDNLS.testErddapFilesWAF()\n");

    // test with trailing /
    // This also tests redirect to https!
    String url = "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/";
    String tFileNameRegex = "194\\d\\.nc";
    boolean tRecursive = true;
    String tPathRegex = ".*/(3|4)/.*";
    boolean tDirsToo = true;
    Table table = FileVisitorDNLS.makeEmptyTable();
    StringArray dirs = (StringArray) table.getColumn(0);
    StringArray names = (StringArray) table.getColumn(1);
    LongArray lastModifieds = (LongArray) table.getColumn(2);
    LongArray sizes = (LongArray) table.getColumn(3);
    String results, expected;
    Table tTable;

    // * test all features
    results = FileVisitorDNLS.addToWAFUrlList( // returns a list of errors or ""
        url, tFileNameRegex, tRecursive, tPathRegex, tDirsToo,
        dirs, names, lastModifieds, sizes);
    Test.ensureEqual(results, "", "results=\n" + results);
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,,,\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1940.nc,1262881740000,14916\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1941.nc,1262881740000,17380\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1942.nc,1262881740000,20548\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1943.nc,1262881740000,17280\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1944.nc,1262881740000,12748\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1945.nc,1262881740000,15692\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1946.nc,1262881740000,17028\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1947.nc,1262881740000,11576\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1948.nc,1262881740000,12876\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1949.nc,1262881740000,15268\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,,,\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1940.nc,1262881740000,285940\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1941.nc,1262881740000,337768\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1942.nc,1262881740000,298608\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1943.nc,1262881740000,175940\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1944.nc,1262881740000,215864\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1945.nc,1262881740000,195056\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1946.nc,1262881740000,239444\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1947.nc,1262881740000,190272\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1948.nc,1262881740000,263084\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1949.nc,1262881740000,352240\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    Test.ensureEqual(Calendar2.epochSecondsToIsoStringTZ(1262881740),
        "2010-01-07T16:29:00Z", "");

    // test via oneStep
    tTable = FileVisitorDNLS.oneStep(url, tFileNameRegex, tRecursive, tPathRegex, tDirsToo);
    results = tTable.dataToString();
    Test.ensureEqual(results, expected, "results=\n" + results);

    // * test !dirsToo
    table.removeAllRows();
    results = FileVisitorDNLS.addToWAFUrlList( // returns a list of errors or ""
        url, tFileNameRegex, tRecursive, tPathRegex, false,
        dirs, names, lastModifieds, sizes);
    Test.ensureEqual(results, "", "results=\n" + results);
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1940.nc,1262881740000,14916\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1941.nc,1262881740000,17380\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1942.nc,1262881740000,20548\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1943.nc,1262881740000,17280\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1944.nc,1262881740000,12748\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1945.nc,1262881740000,15692\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1946.nc,1262881740000,17028\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1947.nc,1262881740000,11576\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1948.nc,1262881740000,12876\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1949.nc,1262881740000,15268\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1940.nc,1262881740000,285940\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1941.nc,1262881740000,337768\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1942.nc,1262881740000,298608\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1943.nc,1262881740000,175940\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1944.nc,1262881740000,215864\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1945.nc,1262881740000,195056\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1946.nc,1262881740000,239444\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1947.nc,1262881740000,190272\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1948.nc,1262881740000,263084\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,1949.nc,1262881740000,352240\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // test via oneStep
    tTable = FileVisitorDNLS.oneStep(url, tFileNameRegex, tRecursive, tPathRegex, false);
    results = tTable.dataToString();
    Test.ensureEqual(results, expected, "results=\n" + results);

    // * test subdir
    table.removeAllRows();
    results = FileVisitorDNLS.addToWAFUrlList( // returns a list of errors or ""
        url + "3", // test no trailing /
        tFileNameRegex, tRecursive, tPathRegex, tDirsToo,
        dirs, names, lastModifieds, sizes);
    Test.ensureEqual(results, "", "results=\n" + results);
    results = table.dataToString();
    expected = "directory,name,lastModified,size\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1940.nc,1262881740000,14916\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1941.nc,1262881740000,17380\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1942.nc,1262881740000,20548\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1943.nc,1262881740000,17280\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1944.nc,1262881740000,12748\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1945.nc,1262881740000,15692\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1946.nc,1262881740000,17028\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1947.nc,1262881740000,11576\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1948.nc,1262881740000,12876\n" +
        "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,1949.nc,1262881740000,15268\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // test via oneStep
    tTable = FileVisitorDNLS.oneStep(url + "3", tFileNameRegex, tRecursive, tPathRegex, tDirsToo);
    results = tTable.dataToString();
    Test.ensureEqual(results, expected, "results=\n" + results);

    // * test file regex that won't match
    table.removeAllRows();
    results = FileVisitorDNLS.addToWAFUrlList( // returns a list of errors or ""
        url, // test no trailing /
        "zztop", tRecursive, tPathRegex, tDirsToo,
        dirs, names, lastModifieds, sizes);
    Test.ensureEqual(results, "", "results=\n" + results);
    results = table.dataToString();
    expected = // just dirs
        "directory,name,lastModified,size\n" +
            "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/3/,,,\n" +
            "https://coastwatch.pfeg.noaa.gov/erddap/files/fedCalLandings/4/,,,\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // test via oneStep
    tTable = FileVisitorDNLS.oneStep(url, "zztop", tRecursive, tPathRegex, tDirsToo);
    results = tTable.dataToString();
    Test.ensureEqual(results, expected, "results=\n" + results);

    // * that should be the same as !recursive
    table.removeAllRows();
    results = FileVisitorDNLS.addToWAFUrlList( // returns a list of errors or ""
        url, // test no trailing /
        tFileNameRegex, false, tPathRegex, tDirsToo,
        dirs, names, lastModifieds, sizes);
    Test.ensureEqual(results, "", "results=\n" + results);
    results = table.dataToString();
    Test.ensureEqual(results, expected, "results=\n" + results);

    // test via oneStep
    tTable = FileVisitorDNLS.oneStep(url, tFileNameRegex, false, tPathRegex, tDirsToo);
    results = tTable.dataToString();
    Test.ensureEqual(results, expected, "results=\n" + results);

  }

  /**
   * This tests a WAF-related (Web Accessible Folder) methods on an ERDDAP "files"
   * directory.
   */
  @org.junit.jupiter.api.Test
  @TagLocalERDDAP
  void testErddap1FilesWAF2() throws Throwable {
    // String2.log("\n*** FileVisitorDNLS.testErddapFilesWAF2()\n");

    // *** test localhost
    String2.log("\nThis test requires erdMWchla1day in localhost erddap.");
    String url = "http://localhost:8080/cwexperimental/files/erdMWchla1day/";
    String tFileNameRegex = "MW200219.*\\.nc(|\\.gz)";
    boolean tRecursive = true;
    String tPathRegex = ".*";
    boolean tDirsToo = true;
    Table table = FileVisitorDNLS.makeEmptyTable();
    StringArray dirs = (StringArray) table.getColumn(0);
    StringArray names = (StringArray) table.getColumn(1);
    LongArray lastModifieds = (LongArray) table.getColumn(2);
    LongArray sizes = (LongArray) table.getColumn(3);

    // * test all features
    String results = FileVisitorDNLS.addToWAFUrlList( // returns a list of errors or ""
        url, tFileNameRegex, tRecursive, tPathRegex, tDirsToo,
        dirs, names, lastModifieds, sizes);
    Test.ensureEqual(results, "", "results=\n" + results);
    results = table.dataToString();
    String expected = "directory,name,lastModified,size\n" +
        "http://localhost:8080/cwexperimental/files/erdMWchla1day/,MW2002190_2002190_chla.nc.gz,1535062380000,3541709\n"
        +
        "http://localhost:8080/cwexperimental/files/erdMWchla1day/,MW2002191_2002191_chla.nc.gz,1535062380000,2661568\n"
        +
        "http://localhost:8080/cwexperimental/files/erdMWchla1day/,MW2002192_2002192_chla.nc.gz,1535062380000,2680618\n"
        +
        "http://localhost:8080/cwexperimental/files/erdMWchla1day/,MW2002193_2002193_chla.nc.gz,1535062380000,2392851\n"
        +
        "http://localhost:8080/cwexperimental/files/erdMWchla1day/,MW2002194_2002194_chla.nc.gz,1535062380000,2209197\n"
        +
        "http://localhost:8080/cwexperimental/files/erdMWchla1day/,MW2002195_2002195_chla.nc.gz,1535062380000,2246841\n"
        +
        "http://localhost:8080/cwexperimental/files/erdMWchla1day/,MW2002196_2002196_chla.nc.gz,1535062380000,1543949\n"
        +
        "http://localhost:8080/cwexperimental/files/erdMWchla1day/,MW2002197_2002197_chla.nc.gz,1535062380000,1846579\n"
        +
        "http://localhost:8080/cwexperimental/files/erdMWchla1day/,MW2002198_2002198_chla.nc.gz,1535062380000,2252800\n"
        +
        "http://localhost:8080/cwexperimental/files/erdMWchla1day/,MW2002199_2002199_chla.nc.gz,1535062380000,2547736\n";
    Test.ensureEqual(results, expected, "results=\n" + results);
  }

  /**
   * This tests GPCP.
   */
  @org.junit.jupiter.api.Test
  void testGpcp() throws Throwable {
    String2.log("\n*** FileVisitorDNLS.testGpcp()\n");

    // * Test ncei WAF
    Table tTable = FileVisitorDNLS.oneStep(
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/",
        "gpcp_v01r03_daily_d2000010.*\\.nc",
        true, ".*", true); // tDirsToo,
    // debugMode = false;
    String results = tTable.dataToString();
    String expected = "directory,name,lastModified,size\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/1996/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/1997/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/1998/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/1999/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2000/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2000/,gpcp_v01r03_daily_d20000101_c20170530.nc,1496163420000,289792\n"
        +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2000/,gpcp_v01r03_daily_d20000102_c20170530.nc,1496163420000,289792\n"
        +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2000/,gpcp_v01r03_daily_d20000103_c20170530.nc,1496163420000,289792\n"
        +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2000/,gpcp_v01r03_daily_d20000104_c20170530.nc,1496163420000,289792\n"
        +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2000/,gpcp_v01r03_daily_d20000105_c20170530.nc,1496163420000,289792\n"
        +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2000/,gpcp_v01r03_daily_d20000106_c20170530.nc,1496163420000,289792\n"
        +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2000/,gpcp_v01r03_daily_d20000107_c20170530.nc,1496163420000,289792\n"
        +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2000/,gpcp_v01r03_daily_d20000108_c20170530.nc,1496163420000,289792\n"
        +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2000/,gpcp_v01r03_daily_d20000109_c20170530.nc,1496163420000,289792\n"
        +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2001/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2002/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2003/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2004/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2005/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2006/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2007/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2008/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2009/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2010/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2011/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2012/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2013/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2014/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2015/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2016/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2017/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2018/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2019/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2020/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2021/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2022/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/access/2023/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/doc/,,,\n" +
        "https://www.ncei.noaa.gov/data/global-precipitation-climatology-project-gpcp-daily/src/,,,\n";
    Test.ensureEqual(results, expected, "results=\n" + results);
  }

  /**
   * This tests the ERSST directory.
   */
  @org.junit.jupiter.api.Test
  void testErsst() throws Throwable {
    String2.log("\n*** FileVisitorDNLS.testErsst()\n");
    Table table = FileVisitorDNLS.makeEmptyTable();
    StringArray dirs = (StringArray) table.getColumn(0);
    StringArray names = (StringArray) table.getColumn(1);
    LongArray lastModifieds = (LongArray) table.getColumn(2);
    LongArray sizes = (LongArray) table.getColumn(3);

    String url = "https://www1.ncdc.noaa.gov/pub/data/cmb/ersst/v4/netcdf/";
    String tFileNameRegex = "ersst.v4.19660.*\\.nc";
    boolean tRecursive = false;
    String tPathRegex = ".*";
    boolean tDirsToo = true;
    String results = FileVisitorDNLS.addToWAFUrlList( // returns a list of errors or ""
        url, tFileNameRegex, tRecursive, tPathRegex, tDirsToo,
        dirs, names, lastModifieds, sizes);
    Test.ensureEqual(results, "", "results=\n" + results);
    results = table.dataToString();
    String expected = "directory,name,lastModified,size\n" +
        "https://www1.ncdc.noaa.gov/pub/data/cmb/ersst/v4/netcdf/,ersst.v4.196601.nc,1479909540000,135168\n" +
        "https://www1.ncdc.noaa.gov/pub/data/cmb/ersst/v4/netcdf/,ersst.v4.196602.nc,1479909540000,135168\n" +
        "https://www1.ncdc.noaa.gov/pub/data/cmb/ersst/v4/netcdf/,ersst.v4.196603.nc,1479909540000,135168\n" +
        "https://www1.ncdc.noaa.gov/pub/data/cmb/ersst/v4/netcdf/,ersst.v4.196604.nc,1479909540000,135168\n" +
        "https://www1.ncdc.noaa.gov/pub/data/cmb/ersst/v4/netcdf/,ersst.v4.196605.nc,1479909540000,135168\n" +
        "https://www1.ncdc.noaa.gov/pub/data/cmb/ersst/v4/netcdf/,ersst.v4.196606.nc,1479909540000,135168\n" +
        "https://www1.ncdc.noaa.gov/pub/data/cmb/ersst/v4/netcdf/,ersst.v4.196607.nc,1479909540000,135168\n" +
        "https://www1.ncdc.noaa.gov/pub/data/cmb/ersst/v4/netcdf/,ersst.v4.196608.nc,1479909540000,135168\n" +
        "https://www1.ncdc.noaa.gov/pub/data/cmb/ersst/v4/netcdf/,ersst.v4.196609.nc,1479909540000,135168\n";
    Test.ensureEqual(results, expected, "results=\n" + results);
  }

  /**
   * This tests oneStepToString().
   */
  @org.junit.jupiter.api.Test
  void testOneStepToString() throws Throwable {
    // String2.log("\n*** FileVisitorDNLS.testOneStepToString()");

    String results = FileVisitorDNLS.oneStepToString(
        Path.of(FileVisitorDNLSTests.class.getResource("/CFPointConventions/timeSeries").toURI()).toString(),
        ".*", true, ".*");
    String expected =
        // this tests that all files are before all dirs
        "timeSeries-Incomplete-MultiDimensional-MultipleStations-H.2.2.rb 2024-01-18T14:31:37Z          5173\n" +
            "timeSeries-Orthogonal-Multidimenstional-MultipleStations-H.2.1.rb 2024-01-18T14:31:37Z          2979\n" +
            "timeSeries-Incomplete-MultiDimensional-MultipleStations-H.2.2\\\n" +
            "  README                                                         2024-01-18T14:31:37Z            87\n" +
            "  timeSeries-Incomplete-MultiDimensional-MultipleStations-H.2.2.cdl 2024-01-18T14:31:37Z          1793\n" +
            "  timeSeries-Incomplete-MultiDimensional-MultipleStations-H.2.2.nc 2024-01-18T14:31:37Z          4796\n" +
            "  timeSeries-Incomplete-MultiDimensional-MultipleStations-H.2.2.ncml 2024-01-18T14:31:37Z          3026\n"
            +
            "timeSeries-Orthogonal-Multidimenstional-MultipleStations-H.2.1\\\n" +
            "  README                                                         2024-01-18T14:31:37Z           538\n" +
            "  timeSeries-Orthogonal-Multidimenstional-MultipleStations-H.2.1.cdl 2024-01-18T14:31:37Z          1515\n"
            +
            "  timeSeries-Orthogonal-Multidimenstional-MultipleStations-H.2.1.nc 2024-01-18T14:31:37Z         10436\n" +
            "  timeSeries-Orthogonal-Multidimenstional-MultipleStations-H.2.1.ncml 2024-01-18T14:31:37Z          2625\n";
    Test.ensureEqual(results, expected, "results=\n" + results);
  }

  /**
   * This tests pathRegex().
   */
  @org.junit.jupiter.api.Test
  void testPathRegex() throws Throwable {
    // String2.log("\n*** FileVisitorDNLS.testPathRegex()");

    String results = FileVisitorDNLS.oneStepToString(
        Path.of(FileVisitorDNLSTests.class.getResource("/CFPointConventions/timeSeries").toURI()).toString(),
        ".*", true, // all files
        ".*H\\.2\\.1.*"); // but only H.2.1 dirs
    String expected = "timeSeries-Incomplete-MultiDimensional-MultipleStations-H.2.2.rb 2024-01-18T14:31:37Z          5173\n"
        +
        "timeSeries-Orthogonal-Multidimenstional-MultipleStations-H.2.1.rb 2024-01-18T14:31:37Z          2979\n" +
        "timeSeries-Orthogonal-Multidimenstional-MultipleStations-H.2.1\\\n" +
        "  README                                                         2024-01-18T14:31:37Z           538\n" +
        "  timeSeries-Orthogonal-Multidimenstional-MultipleStations-H.2.1.cdl 2024-01-18T14:31:37Z          1515\n" +
        "  timeSeries-Orthogonal-Multidimenstional-MultipleStations-H.2.1.nc 2024-01-18T14:31:37Z         10436\n" +
        "  timeSeries-Orthogonal-Multidimenstional-MultipleStations-H.2.1.ncml 2024-01-18T14:31:37Z          2625\n";
    Test.ensureEqual(results, expected, "results=\n" + results);
  }

  /**
   * This tests following symbolic links / soft links.
   * THIS DOESN'T WORK on Windows, because Java doesn't follow Windows .lnk's.
   * Windows links are not easily parsed files. It would be hard to add support
   * for .lnk's to this class.
   * This class now works as expected with symbolic links on Linux.
   * I tested manually on coastwatch Linux with FileVisitorDNLS.sh and main()
   * below and
   * ./FileVisitorDNLS.sh /u00/satellite/MUR41/anom/1day/ ....0401.\*
   */
  @org.junit.jupiter.api.Test
  @TagLargeFile
  void testSymbolicLinks() throws Throwable {
    // String2.log("\n*** FileVisitorDNLS.testSymbolicLinks()");
    // boolean oDebugMode = debugMode;
    // debugMode = true;

    String results = FileVisitorDNLS.oneStepToString(
        "/u00/satellite/MUR41/anom/1day/", ".*", true, ".*");
    String expected =
        // 2002 are files. 2003 is a shortcut to files
        "zztop\n";
    Test.ensureEqual(results, expected, "results=\n" + results);
    // debugMode = oDebugMode;
  }

  /**
   * This tests reduceDnlsTableToOneDir().
   */
  @org.junit.jupiter.api.Test
  void testReduceDnlsTableToOneDir() throws Exception {
    // String2.log("\n*** FileVisitorDNLS.testReduceDnlsTableToOneDir\n");
    String tableString = "directory, name, lastModified, size\n" +
        "/u00/, , , \n" +
        "/u00/, nothing, 60, 1\n" +
        "/u00/a/, , , \n" +
        "/u00/a/, AA, 60, 2\n" +
        "/u00/a/, A, 60, 3\n" +
        "/u00/a/q/,  ,   , \n" +
        "/u00/a/q/, D, 60, 4\n" +
        "/u00/a/b/, B, 60, 5\n" +
        "/u00/a/b/c/, C, 60, 6\n";
    Table table = new Table();
    table.readASCII("testReduceDnlsTableToOneDir",
        new BufferedReader(new StringReader(tableString)),
        "", "", 0, 1, ",", null, null, null, null, true);
    String subDirs[] = FileVisitorDNLS.reduceDnlsTableToOneDir(table, "/u00/a/");

    String results = table.dataToString();
    String expected = "directory,name,lastModified,size\n" +
        "/u00/a/,A,60,3\n" + // files are sorted, dirs are removed
        "/u00/a/,AA,60,2\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    results = String2.toCSSVString(subDirs);
    expected = "b, q";
    Test.ensureEqual(results, expected, "results=\n" + results);
  }
}