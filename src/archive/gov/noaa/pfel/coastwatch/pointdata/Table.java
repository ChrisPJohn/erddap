public class Table {
    /**
   * This add the value of one datum as a String to one of the columns, thereby increasing the
   * number of rows in that column.
   *
   * @param col the column number (0 ... nColumns-1 )
   * @param s the value of one datum as a String.
   * @throws Exception if trouble (e.g., row or col out of range)
   */
  public void addStringData(int col, String s) {
    getColumn(col).addString(s);
  }

  /**
   * This add the value of one datum as a float to one of the columns, thereby increasing the number
   * of rows in that column.
   *
   * @param col the column number (0 ... nColumns-1 )
   * @param d the value of one datum as a float.
   * @throws Exception if trouble (e.g., row or col out of range)
   */
  public void addFloatData(int col, float d) {
    getColumn(col).addFloat(d);
  }

  /**
   * This add the value of one datum as a double to one of the columns, thereby increasing the
   * number of rows in that column.
   *
   * @param col the column number (0 ... nColumns-1 )
   * @param d the value of one datum as a double.
   * @throws Exception if trouble (e.g., row or col out of range)
   */
  public void addDoubleData(int col, double d) {
    getColumn(col).addDouble(d);
  }

  /**
   * This add the value of one datum as an int to one of the columns, thereby increasing the number
   * of rows in that column.
   *
   * @param col the column number (0 ... nColumns-1 )
   * @param d the value of one datum as an int.
   * @throws Exception if trouble (e.g., row or col out of range)
   */
  public void addIntData(int col, int d) {
    getColumn(col).addInt(d);
  }

  /**
   * This tests that the value in a column is as expected.
   *
   * @throws Exception if trouble
   */
  public void test1(String columnName, int row, String expected) {
    String observed = findColumn(columnName).getString(row);
    // ensureEqual deals with nulls
    Test.ensureEqual(observed, expected, "colName=" + columnName + " row=" + row);
  }

  /**
   * This tests that the value in a column is as expected.
   *
   * @throws Exception if trouble
   */
  public void test1(String columnName, int row, int expected) {
    int observed = findColumn(columnName).getInt(row);
    if (observed != expected) throw new RuntimeException("colName=" + columnName + " row=" + row);
  }

  /**
   * This tests that the value in a column is as expected.
   *
   * @throws Exception if trouble
   */
  public void test1(String columnName, int row, float expected) {
    float observed = findColumn(columnName).getFloat(row);
    // ensureEqual does fuzzy test
    Test.ensureEqual(observed, expected, "colName=" + columnName + " row=" + row);
  }

  /**
   * This tests that the value in a column is as expected.
   *
   * @throws Exception if trouble
   */
  public void test1(String columnName, int row, double expected) {
    double observed = findColumn(columnName).getDouble(row);
    // ensureEqual does fuzzy test
    Test.ensureEqual(observed, expected, "colName=" + columnName + " row=" + row);
  }

  /**
   * This tests that the value in a column is as expected. The table's column should have epoch
   * seconds values.
   *
   * @param expected value formatted with Calendar2.safeEpochSecondsToIsoStringTZ(seconds, "")
   * @throws Exception if trouble
   */
  public void test1Time(String columnName, int row, String expected) {
    double seconds = findColumn(columnName).getDouble(row);
    String observed = Calendar2.safeEpochSecondsToIsoStringTZ(seconds, "");
    if (!observed.equals(expected))
      throw new RuntimeException("colName=" + columnName + " row=" + row);
  }

  /** This checks that the value in a column is as expected and prints PASS/FAIL and the test. */
  public String check1(String columnName, int row, String expected) {
    String observed = findColumn(columnName).getString(row);
    // Test.equal deals with nulls
    return (Test.equal(observed, expected) ? "PASS" : "FAIL")
        + ": col="
        + String2.left(columnName, 15)
        + " row="
        + String2.left("" + row, 2)
        + " observed="
        + observed
        + " expected="
        + expected;
  }

  /** This checks that the value in a column is as expected and prints PASS/FAIL and the test. */
  public String check1(String columnName, int row, int expected) {
    int observed = findColumn(columnName).getInt(row);
    return (Test.equal(observed, expected) ? "PASS" : "FAIL")
        + ": col="
        + String2.left(columnName, 15)
        + " row="
        + String2.left("" + row, 2)
        + " observed="
        + observed
        + " expected="
        + expected;
  }

  /** This checks that the value in a column is as expected and prints PASS/FAIL and the test. */
  public String check1(String columnName, int row, float expected) {
    float observed = findColumn(columnName).getFloat(row);
    return (Test.equal(observed, expected) ? "PASS" : "FAIL")
        + ": col="
        + String2.left(columnName, 15)
        + " row="
        + String2.left("" + row, 2)
        + " observed="
        + observed
        + " expected="
        + expected;
  }

  /** This checks that the value in a column is as expected and prints PASS/FAIL and the test. */
  public String check1(String columnName, int row, double expected) {
    double observed = findColumn(columnName).getDouble(row);
    return (Test.equal(observed, expected) ? "PASS" : "FAIL")
        + ": col="
        + String2.left(columnName, 15)
        + " row="
        + String2.left("" + row, 2)
        + " observed="
        + observed
        + " expected="
        + expected;
  }

  /**
   * This checks that the value in a column is as expected and prints PASS/FAIL and the test. The
   * table's column should have epoch seconds values.
   *
   * @param expected value formatted with Calendar2.safeEpochSecondsToIsoStringTZ(seconds, "")
   */
  public String check1Time(String columnName, int row, String expected) {
    double seconds = findColumn(columnName).getDouble(row);
    String observed = Calendar2.safeEpochSecondsToIsoStringTZ(seconds, "");
    return (Test.equal(observed, expected) ? "PASS" : "FAIL")
        + ": col="
        + String2.left(columnName, 15)
        + " row="
        + String2.left("" + row, 2)
        + " observed="
        + observed
        + " expected="
        + expected;
  }

  /**
   * Like rank, but StringArrays are ranked in a case-insensitive way.
   *
   * @param keyColumns the numbers of the key columns (first is most important)
   * @param ascending try if a given key column should be ranked ascending
   */
  public int[] rankIgnoreCase(int keyColumns[], boolean ascending[]) {
    return PrimitiveArray.rankIgnoreCase(columns, keyColumns, ascending);
  }

  /**
   * This removes rows in which the value in 'column' is less than the value in the previous row.
   * Rows with values of NaN or bigger than 1e300 are also removed. !!!Trouble: one erroneous big
   * value will cause all subsequent valid values to be tossed.
   *
   * @param column the column which should be ascending
   * @return the number of rows removed
   */
  public int ensureAscending(int column) {
    return PrimitiveArray.ensureAscending(columns, column);
  }
  
  /**
   * This removes all rows of data at the end of the table that have just missing_value or
   * _FillValue or the PrimitiveArray's native MV.
   *
   * <p>CF 1.6 Discrete Sampling Geometry and Incomplete Multidimensional Arrays: the spec doesn't
   * say which attribute is to be used: missing_value or _FillValue.
   *
   * @return the number of rows remaining
   */
  public int removeRowsAtEndWithoutData() {
    int nRemain = lastRowWithData() + 1;
    removeRows(nRemain, nRows());
    return nRemain;
  }

    /**
   * This reads the 1D variables from a .nc file *and* the scalar (0D) values (duplicated to have
   * the same number of rows).
   *
   * <p>This always converts to standard missing values (MAX_INT, ..., NaN).
   *
   * @param fullName This may be a local file name, an "http:" address of a .nc file, or an opendap
   *     url.
   * @param loadColumns The 1D variables to be loaded. If null, this searches for the
   *     (pseudo)structure variables. (The scalar variables are always loaded.)
   * @param standardizeWhat see Attributes.unpackVariable's standardizeWhat, or 0 to do nothing, or
   *     -1 to convertToStandardMissingValues (e.g., -999 to Int.MAX_VALUE)
   * @param lastRow the last row to be read (inclusive). If lastRow = -1, the entire var is read.
   * @throws Exception if trouble
   */
  public void readFlat0Nc(String fullName, String loadColumns[], int standardizeWhat, int lastRow)
  throws Exception {

// read the 1D variables
long time = System.currentTimeMillis();
lowReadFlatNc(fullName, loadColumns, standardizeWhat, true, lastRow); // doAltStandardization
int tnRows = nRows();
String msg = "  Table.readFlat0Nc " + fullName;

// read the scalar variables
// getGridMappingAtts() handled by lowReadFlatNc above
int insertAt = 0;
try (NetcdfFile netcdfFile = NcHelper.openFile(fullName)) {
  Group rootGroup = netcdfFile.getRootGroup();
  List<Variable> rootGroupVariables = rootGroup.getVariables();
  for (Variable rootGroupVariable : rootGroupVariables) {
    boolean isChar = rootGroupVariable.getDataType() == DataType.CHAR;
    if (rootGroupVariable.getRank() + (isChar ? -1 : 0) == 0) {
      PrimitiveArray pa = NcHelper.getPrimitiveArray(rootGroupVariable);
      // unpack is done at end of method
      // nc allows strings to be 0-terminated or padded with spaces, so always trimEnd
      if (pa instanceof StringArray) pa.setString(0, String2.trimEnd(pa.getString(0)));
      if (tnRows > 1) {
        if (pa instanceof StringArray) pa.addNStrings(tnRows - 1, pa.getString(0));
        else pa.addNDoubles(tnRows - 1, pa.getDouble(0));
      }

      Attributes atts = new Attributes();
      NcHelper.getVariableAttributes(rootGroupVariable, atts);
      addColumn(insertAt++, rootGroupVariable.getShortName(), pa, atts);
    }
  }

  // unpack
  decodeCharsAndStrings();
  if (standardizeWhat > 0) {
    convertToUnsignedPAs();
    standardize(standardizeWhat);
  } else if (standardizeWhat == 0) {
    // do nothing
  } else {
    convertToUnsignedPAs();
    convertToStandardMissingValues();
  }

  if (reallyVerbose)
    String2.log(
        msg
            + " finished. nColumns="
            + nColumns()
            + " nRows="
            + nRows()
            + " TIME="
            + (System.currentTimeMillis() - time)
            + "ms");
}
}

/**
   * This appends okRows rows from a .nc file by doing one big read and then removing unwanted rows
   * -- thus it may be more suited for opendap since it may avoid huge numbers of separate reads.
   * This doesn't read global attributes or variable attributes. This doesn't unpack packed
   * variables. If the table initially has no columns, this creates columns and reads column names;
   * otherwise it doesn't read column names.
   *
   * @param loadVariables the variables to be loaded. They must all be ArrayXxx.D1 or ArrayChar.D2
   *     variables and use the same, one, dimension as the first dimension. This must not be null or
   *     empty.
   * @param okRows
   * @throws Exception if trouble
   */
  public void blockAppendNcRows(Variable loadVariables[], BitSet okRows) throws Exception {
    // this is tested in PointSubset
    long time = System.currentTimeMillis();

    // !!****THIS HASN'T BEEN MODIFIED TO DO BLOCK READ YET

    // get the desired rows   (first call adds pa's to data and adds columnNames)
    int n = okRows.size();
    int firstRow = okRows.nextSetBit(0);
    int nAppendCalls = 0;
    while (firstRow >= 0) {
      // find end of sequence of okRows
      int endRow = firstRow == n - 1 ? n : okRows.nextClearBit(firstRow + 1);
      if (endRow == -1) endRow = n;

      // get the data
      appendNcRows(loadVariables, firstRow, endRow - 1);
      nAppendCalls++;

      // first start of next sequence of okRows
      if (endRow >= n - 1) firstRow = -1;
      else firstRow = okRows.nextSetBit(endRow + 1);
    }
    String2.log(
        "Table.blockAppendNcRows done. nAppendCalls="
            + nAppendCalls
            + " nRows="
            + nRows()
            + " TIME="
            + (System.currentTimeMillis() - time)
            + "ms");
  }

   /**
   * This writes the table's data structure (as if it were a DODS Sequence) to the outputStream as
   * an DODS DataDDS (see www.opendap.org, DAP 2.0, 7.2.3).
   *
   * <p>This sends missing values as is. This doesn't call convertToFakeMissingValues. Do it
   * beforehand if you need to.
   *
   * @param outputStream the outputStream to receive the results. Afterwards, it is flushed, not
   *     closed.
   * @param sequenceName e.g., "erd_opendap_globec_bottle"
   * @throws Exception if trouble.
   */
  public void saveAsDODS(OutputStream outputStream, String sequenceName) throws Exception {
    if (reallyVerbose) String2.log("  Table.saveAsDODS");
    long time = System.currentTimeMillis();

    // write the dds    //DAP 2.0, 7.2.3
    saveAsDDS(outputStream, sequenceName);

    // write the connector  //DAP 2.0, 7.2.3
    // see EOL definition for comments
    outputStream.write(
        (OpendapHelper.EOL + "Data:" + OpendapHelper.EOL).getBytes(StandardCharsets.UTF_8));

    // write the data  //DAP 2.0, 7.3.2.3
    // write elements of the sequence, in dds order
    int nColumns = nColumns();
    int nRows = nRows();
    DataOutputStream dos = new DataOutputStream(outputStream);
    for (int row = 0; row < nRows; row++) {
      dos.writeInt(0x5A << 24); // start of instance
      for (int col = 0; col < nColumns; col++) getColumn(col).externalizeForDODS(dos, row);
    }
    dos.writeInt(0xA5 << 24); // end of sequence; so if nRows=0, this is all that is sent

    dos.flush(); // essential

    if (reallyVerbose)
      String2.log("  Table.saveAsDODS done. TIME=" + (System.currentTimeMillis() - time) + "ms");
  }

/**
   * This writes the table's data structure (as if it were a DODS Sequence) to the outputStream as
   * DODS ASCII data (which is not defined in DAP 2.0, but which is very close to saveAsDODS below).
   * This mimics
   * https://oceanwatch.pfeg.noaa.gov/opendap/GLOBEC/GLOBEC_bottle.asc?lon,ship,cast,t0,NO3&lon<-125.7
   *
   * <p>This sends missing values as is. This doesn't call convertToFakeMissingValues. Do it
   * beforehand if you need to.
   *
   * @param outputStream the outputStream to receive the results. Afterwards, it is flushed, not
   *     closed.
   * @param sequenceName e.g., "erd_opendap_globec_bottle"
   * @throws Exception if trouble.
   */
  public void saveAsDodsAscii(OutputStream outputStream, String sequenceName) throws Exception {

    if (reallyVerbose) String2.log("  Table.saveAsDodsAscii");
    long time = System.currentTimeMillis();

    // write the dds    //DAP 2.0, 7.2.3
    saveAsDDS(outputStream, sequenceName);

    // write the connector
    Writer writer = File2.getBufferedWriter88591(outputStream);
    writer.write(
        "---------------------------------------------"
            + OpendapHelper.EOL); // see EOL definition for comments

    // write the column names
    int nColumns = nColumns();
    int nRows = nRows();
    boolean isCharOrString[] = new boolean[nColumns];
    for (int col = 0; col < nColumns; col++) {
      isCharOrString[col] =
          getColumn(col).elementType() == PAType.CHAR
              || getColumn(col).elementType() == PAType.STRING;
      writer.write(getColumnName(col) + (col == nColumns - 1 ? OpendapHelper.EOL : ", "));
    }

    // write the data  //DAP 2.0, 7.3.2.3
    // write elements of the sequence, in dds order
    for (int row = 0; row < nRows; row++) {
      for (int col = 0; col < nColumns; col++) {
        String s = getColumn(col).getString(row);
        if (isCharOrString[col])
          // see DODS Appendix A, quoted-string.
          // It just talks about " to \", but Json format is implied and better
          s = String2.toJson(s);
        writer.write(s + (col == nColumns - 1 ? OpendapHelper.EOL : ", "));
      }
    }

    writer.flush(); // essential

    if (reallyVerbose)
      String2.log(
          "  Table.saveAsDodsAscii done. TIME=" + (System.currentTimeMillis() - time) + "ms");
  }

  /**
   * This converts the specified column from epochSeconds doubles to ISO 8601 Strings.
   *
   * @param timeIndex
   */
  public void convertEpochSecondsColumnToIso8601(int timeIndex) {
    int tnRows = nRows();
    PrimitiveArray pa = getColumn(timeIndex);
    StringArray sa = new StringArray(tnRows, false);
    for (int row = 0; row < tnRows; row++)
      sa.add(Calendar2.safeEpochSecondsToIsoStringTZ(pa.getDouble(row), ""));
    setColumn(timeIndex, sa);

    if (String2.isSomething(columnAttributes(timeIndex).getString("units")))
      columnAttributes(timeIndex).set("units", Calendar2.ISO8601TZ_FORMAT);
  }

    /**
   * THIS IS NOT YET FINISHED. This converts the specified String column with date (with e.g.,
   * "2006-01-02"), time (with e.g., "23:59:59"), or timestamp values (with e.g., "2006-01-02
   * 23:59:59" with any character between the date and time) into a double column with
   * secondSinceEpoch (1970-01-01 00:00:00 UTC time zone). No metadata is changed by this method.
   *
   * @param col the number of the column (0..) with the date, time, or timestamp strings.
   * @param type indicates the type of data in the column: 0=date, 1=time, 2=timestamp.
   * @param timeZoneOffset this identifies the time zone associated with col (e.g., 0 if already
   *     UTC, -7 for California in summer (DST), and -8 for California in winter, so that the data
   *     can be converted to UTC timezone.
   * @param strict If true, this throws an exception if a value is improperly formatted. (Missing
   *     values of "" or null are allowed.) If false, improperly formatted values are silently
   *     converted to missing values (Double.NaN). Regardless of 'strict', the method rolls
   *     components as needed, for example, Jan 32 becomes Feb 1.
   * @return the number of valid values.
   * @throws Exception if trouble (and no changes will have been made)
   */
  public int isoStringToEpochSeconds(int col, int type, int timeZoneOffset, boolean strict)
  throws Exception {

String errorInMethod = String2.ERROR + " in Table.isoStringToEpochSeconds(col=" + col + "):\n";
Test.ensureTrue(
    type >= 0 && type <= 2, errorInMethod + "type=" + type + " must be between 0 and 2.");
String isoDatePattern = "[1-2][0-9]{3}\\-[0-1][0-9]\\-[0-3][0-9]";
String isoTimePattern = "[0-2][0-9]\\:[0-5][0-9]\\:[0-5][0-9]";
String stringPattern =
    type == 0
        ? isoDatePattern
        : type == 1 ? isoTimePattern : isoDatePattern + "." + isoTimePattern;
Pattern pattern = Pattern.compile(stringPattern);
StringArray sa = (StringArray) getColumn(col);
int n = sa.size();
DoubleArray da = new DoubleArray(n, true);
int nGood = 0;
int adjust = timeZoneOffset * Calendar2.SECONDS_PER_HOUR;
for (int row = 0; row < n; row++) {
  String s = sa.get(row);

  // catch allowed missing values
  if (s == null || s.length() == 0) {
    da.array[row] = Double.NaN;
    continue;
  }

  // catch improperly formatted values (stricter than Calendar2.isoStringToEpochSeconds below)
  if (strict && !pattern.matcher(s).matches())
    throw new SimpleException(
        errorInMethod + "value=" + s + " on row=" + row + " is improperly formatted.");

  // parse the string
  if (type == 1) s = "1970-01-01 " + s;
  double d = Calendar2.isoStringToEpochSeconds(s); // throws exception
  if (!Double.isNaN(d)) {
    nGood++;
    d -= adjust;
  }
  da.array[row] = d;
}
setColumn(col, da);
return nGood;
}

/**  THIS IS NOT YET FINISHED.
   * This reads all rows of all of the specified columns from an opendap
   * dataset.
   * This also reads global and variable attributes.
   * The data is always unpacked.
   *
   * <p>If the fullName is an http address, the name needs to start with "http:\\"
   * (upper or lower case) and the server needs to support "byte ranges"
   * (see ucar.nc2.NetcdfFile documentation).
   *
   * @param fullName This may be a local file name, an "http:" address of a
   *    .nc file, or an opendap url.
   * @param loadColumns if null, this searches for the (pseudo)structure variables
   * @throws Exception if trouble
   */
  public void readOpendap(String fullName, String loadColumns[]) throws Exception {

    // get information
    String msg = "  Table.readOpendap " + fullName;
    long time = System.currentTimeMillis();
    Attributes gridMappingAtts = null;
    try (NetcdfFile netcdfFile = NcHelper.openFile(fullName)) {
      Variable loadVariables[] = NcHelper.findVariables(netcdfFile, loadColumns);

      // fill the table
      clear();
      appendNcRows(loadVariables, 0, -1);
      NcHelper.getGroupAttributes(netcdfFile.getRootGroup(), globalAttributes());
      for (int col = 0; col < loadVariables.length; col++) {
        NcHelper.getVariableAttributes(loadVariables[col], columnAttributes(col));

        // does this var point to the pseudo-data var with CF grid_mapping (projection) information?
        if (gridMappingAtts == null) {
          gridMappingAtts =
              NcHelper.getGridMappingAtts(
                  netcdfFile, columnAttributes(col).getString("grid_mapping"));
          if (gridMappingAtts != null) globalAttributes.add(gridMappingAtts);
        }
      }

      if (reallyVerbose)
        String2.log(
            msg
                + " finished. nColumns="
                + nColumns()
                + " nRows="
                + nRows()
                + " TIME="
                + (System.currentTimeMillis() - time)
                + "ms");
    }
  }

/**
   * THIS IS NOT FINISHED.
   *
   * @param standardizeWhat see Attributes.unpackVariable's standardizeWhat
   */
  public void readArgoProfile(String fileName, int standardizeWhat) throws Exception {

    String msg = "  Table.readArgoProfile " + fileName;
    long tTime = System.currentTimeMillis();
    // Attributes gridMappingAtts = null; //method is unfinished
    try (NetcdfFile nc = NcHelper.openFile(fileName)) {
      //   DATE_TIME = 14;
      //   N_PROF = 632;
      //   N_PARAM = 3;
      //   N_LEVELS = 71;
      //   N_CALIB = 1;
      //   N_HISTORY = UNLIMITED;   // (0 currently)
      Variable var;
      PrimitiveArray pa;
      int col;
      NcHelper.getGroupAttributes(nc.getRootGroup(), globalAttributes);

      // The plan is: make minimal changes here. Change metadata etc in ERDDAP.

      var = nc.findVariable("DATA_TYPE");
      if (var != null) {
        col = addColumn("dataType", NcHelper.getPrimitiveArray(var));
        NcHelper.getVariableAttributes(var, columnAttributes(col));
      }

      // skip char FORMAT_VERSION(STRING4=4);   :comment = "File format version";

      var = nc.findVariable("HANDBOOK_VERSION");
      if (var != null) {
        col = addColumn("handbookVersion", NcHelper.getPrimitiveArray(var));
        NcHelper.getVariableAttributes(var, columnAttributes(col));
      }

      var = nc.findVariable("REFERENCE_DATE_TIME"); // "YYYYMMDDHHMISS";
      if (var != null) {
        pa = NcHelper.getPrimitiveArray(var);
        double time = Double.NaN;
        try {
          time = Calendar2.gcToEpochSeconds(Calendar2.parseCompactDateTimeZulu(pa.getString(0)));
        } catch (Exception e) {
          String2.log(e.getMessage());
        }
        col = addColumn("time", PrimitiveArray.factory(new double[] {time}));
        NcHelper.getVariableAttributes(var, columnAttributes(col));
        columnAttributes(col).set("units", Calendar2.SECONDS_SINCE_1970);
      }

      var = nc.findVariable("PLATFORM_NUMBER");
      if (var != null) {
        col = addColumn("platformNumber", NcHelper.getPrimitiveArray(var));
        NcHelper.getVariableAttributes(var, columnAttributes(col));
      }

      var = nc.findVariable("PROJECTPLATFORM_NUMBER");
      if (var != null) {
        col = addColumn("platformNumber", NcHelper.getPrimitiveArray(var));
        NcHelper.getVariableAttributes(var, columnAttributes(col));
      }

      /*   char PROJECT_NAME(N_PROF=632, STRING64=64);
        :comment = "Name of the project";
        :_FillValue = " ";
      char PI_NAME(N_PROF=632, STRING64=64);
        :comment = "Name of the principal investigator";
        :_FillValue = " ";
      char STATION_PARAMETERS(N_PROF=632, N_PARAM=3, STRING16=16);
        :long_name = "List of available parameters for the station";
        :conventions = "Argo reference table 3";
        :_FillValue = " ";
      int CYCLE_NUMBER(N_PROF=632);
        :long_name = "Float cycle number";
        :conventions = "0..N, 0 : launch cycle (if exists), 1 : first complete cycle";
        :_FillValue = 99999; // int
      char DIRECTION(N_PROF=632);
        :long_name = "Direction of the station profiles";
        :conventions = "A: ascending profiles, D: descending profiles";
        :_FillValue = " ";
      char DATA_CENTRE(N_PROF=632, STRING2=2);
        :long_name = "Data centre in charge of float data processing";
        :conventions = "Argo reference table 4";
        :_FillValue = " ";
      char DATE_CREATION(DATE_TIME=14);
        :comment = "Date of file creation";
        :conventions = "YYYYMMDDHHMISS";
        :_FillValue = " ";
      char DATE_UPDATE(DATE_TIME=14);
        :long_name = "Date of update of this file";
        :conventions = "YYYYMMDDHHMISS";
        :_FillValue = " ";
      char DC_REFERENCE(N_PROF=632, STRING32=32);
        :long_name = "Station unique identifier in data centre";
        :conventions = "Data centre convention";
        :_FillValue = " ";
      char DATA_STATE_INDICATOR(N_PROF=632, STRING4=4);
        :long_name = "Degree of processing the data have passed through";
        :conventions = "Argo reference table 6";
        :_FillValue = " ";
      char DATA_MODE(N_PROF=632);
        :long_name = "Delayed mode or real time data";
        :conventions = "R : real time; D : delayed mode; A : real time with adjustment";
        :_FillValue = " ";
      char INST_REFERENCE(N_PROF=632, STRING64=64);
        :long_name = "Instrument type";
        :conventions = "Brand, type, serial number";
        :_FillValue = " ";
      char WMO_INST_TYPE(N_PROF=632, STRING4=4);
        :long_name = "Coded instrument type";
        :conventions = "Argo reference table 8";
        :_FillValue = " ";
      double JULD(N_PROF=632);
        :long_name = "Julian day (UTC) of the station relative to REFERENCE_DATE_TIME";
        :units = "days since 1950-01-01 00:00:00 UTC";
        :conventions = "Relative julian days with decimal part (as parts of day)";
        :_FillValue = 999999.0; // double
      char JULD_QC(N_PROF=632);
        :long_name = "Quality on Date and Time";
        :conventions = "Argo reference table 2";
        :_FillValue = " ";
      double JULD_LOCATION(N_PROF=632);
        :long_name = "Julian day (UTC) of the location relative to REFERENCE_DATE_TIME";
        :units = "days since 1950-01-01 00:00:00 UTC";
        :conventions = "Relative julian days with decimal part (as parts of day)";
        :_FillValue = 999999.0; // double
      double LATITUDE(N_PROF=632);
        :long_name = "Latitude of the station, best estimate";
        :units = "degree_north";
        :_FillValue = 99999.0; // double
        :valid_min = -90.0; // double
        :valid_max = 90.0; // double
      double LONGITUDE(N_PROF=632);
        :long_name = "Longitude of the station, best estimate";
        :units = "degree_east";
        :_FillValue = 99999.0; // double
        :valid_min = -180.0; // double
        :valid_max = 180.0; // double
      char POSITION_QC(N_PROF=632);
        :long_name = "Quality on position (latitude and longitude)";
        :conventions = "Argo reference table 2";
        :_FillValue = " ";
      char POSITIONING_SYSTEM(N_PROF=632, STRING8=8);
        :long_name = "Positioning system";
        :_FillValue = " ";
      char PROFILE_PRES_QC(N_PROF=632);
        :long_name = "Global quality flag of PRES profile";
        :conventions = "Argo reference table 2a";
        :_FillValue = " ";
      char PROFILE_TEMP_QC(N_PROF=632);
        :long_name = "Global quality flag of TEMP profile";
        :conventions = "Argo reference table 2a";
        :_FillValue = " ";
      char PROFILE_PSAL_QC(N_PROF=632);
        :long_name = "Global quality flag of PSAL profile";
        :conventions = "Argo reference table 2a";
        :_FillValue = " ";
      float PRES(N_PROF=632, N_LEVELS=71);
        :long_name = "SEA PRESSURE";
        :_FillValue = 99999.0f; // float
        :units = "decibar";
        :valid_min = 0.0f; // float
        :valid_max = 12000.0f; // float
        :comment = "In situ measurement, sea surface = 0";
        :C_format = "%7.1f";
        :FORTRAN_format = "F7.1";
        :resolution = 0.1f; // float
      char PRES_QC(N_PROF=632, N_LEVELS=71);
        :long_name = "quality flag";
        :conventions = "Argo reference table 2";
        :_FillValue = " ";
      float PRES_ADJUSTED(N_PROF=632, N_LEVELS=71);
        :long_name = "SEA PRESSURE";
        :_FillValue = 99999.0f; // float
        :units = "decibar";
        :valid_min = 0.0f; // float
        :valid_max = 12000.0f; // float
        :comment = "In situ measurement, sea surface = 0";
        :C_format = "%7.1f";
        :FORTRAN_format = "F7.1";
        :resolution = 0.1f; // float
      char PRES_ADJUSTED_QC(N_PROF=632, N_LEVELS=71);
        :long_name = "quality flag";
        :conventions = "Argo reference table 2";
        :_FillValue = " ";
      float PRES_ADJUSTED_ERROR(N_PROF=632, N_LEVELS=71);
        :long_name = "SEA PRESSURE";
        :_FillValue = 99999.0f; // float
        :units = "decibar";
        :comment = "Contains the error on the adjusted values as determined by the delayed mode QC process.";
        :C_format = "%7.1f";
        :FORTRAN_format = "F7.1";
        :resolution = 0.1f; // float
      float TEMP(N_PROF=632, N_LEVELS=71);
        :long_name = "SEA TEMPERATURE IN SITU ITS-90 SCALE";
        :_FillValue = 99999.0f; // float
        :units = "degree_Celsius";
        :valid_min = -2.0f; // float
        :valid_max = 40.0f; // float
        :comment = "In situ measurement";
        :C_format = "%9.3f";
        :FORTRAN_format = "F9.3";
        :resolution = 0.001f; // float
      char TEMP_QC(N_PROF=632, N_LEVELS=71);
        :long_name = "quality flag";
        :conventions = "Argo reference table 2";
        :_FillValue = " ";
      float TEMP_ADJUSTED(N_PROF=632, N_LEVELS=71);
        :long_name = "SEA TEMPERATURE IN SITU ITS-90 SCALE";
        :_FillValue = 99999.0f; // float
        :units = "degree_Celsius";
        :valid_min = -2.0f; // float
        :valid_max = 40.0f; // float
        :comment = "In situ measurement";
        :C_format = "%9.3f";
        :FORTRAN_format = "F9.3";
        :resolution = 0.001f; // float
      char TEMP_ADJUSTED_QC(N_PROF=632, N_LEVELS=71);
        :long_name = "quality flag";
        :conventions = "Argo reference table 2";
        :_FillValue = " ";
      float TEMP_ADJUSTED_ERROR(N_PROF=632, N_LEVELS=71);
        :long_name = "SEA TEMPERATURE IN SITU ITS-90 SCALE";
        :_FillValue = 99999.0f; // float
        :units = "degree_Celsius";
        :comment = "Contains the error on the adjusted values as determined by the delayed mode QC process.";
        :C_format = "%9.3f";
        :FORTRAN_format = "F9.3";
        :resolution = 0.001f; // float
      float PSAL(N_PROF=632, N_LEVELS=71);
        :long_name = "PRACTICAL SALINITY";
        :_FillValue = 99999.0f; // float
        :units = "psu";
        :valid_min = 0.0f; // float
        :valid_max = 42.0f; // float
        :comment = "In situ measurement";
        :C_format = "%9.3f";
        :FORTRAN_format = "F9.3";
        :resolution = 0.001f; // float
      char PSAL_QC(N_PROF=632, N_LEVELS=71);
        :long_name = "quality flag";
        :conventions = "Argo reference table 2";
        :_FillValue = " ";
      float PSAL_ADJUSTED(N_PROF=632, N_LEVELS=71);
        :long_name = "PRACTICAL SALINITY";
        :_FillValue = 99999.0f; // float
        :units = "psu";
        :valid_min = 0.0f; // float
        :valid_max = 42.0f; // float
        :comment = "In situ measurement";
        :C_format = "%9.3f";
        :FORTRAN_format = "F9.3";
        :resolution = 0.001f; // float
      char PSAL_ADJUSTED_QC(N_PROF=632, N_LEVELS=71);
        :long_name = "quality flag";
        :conventions = "Argo reference table 2";
        :_FillValue = " ";
      float PSAL_ADJUSTED_ERROR(N_PROF=632, N_LEVELS=71);
        :long_name = "PRACTICAL SALINITY";
        :_FillValue = 99999.0f; // float
        :units = "psu";
        :comment = "Contains the error on the adjusted values as determined by the delayed mode QC process.";
        :C_format = "%9.3f";
        :FORTRAN_format = "F9.3";
        :resolution = 0.001f; // float
        */
      if (reallyVerbose)
        String2.log(
            msg
                + " finished. nColumns="
                + nColumns()
                + " nRows="
                + nRows()
                + " TIME="
                + (System.currentTimeMillis() - tTime)
                + "ms");
    }
  }

   /**
   * This saves this table as an NCCSV DataOutputStream. This doesn't change representation of time
   * (e.g., as seconds or as String). This never calls dos.flush();
   *
   * @param catchScalars If true, this looks at the data for scalars (just 1 value).
   * @param writeMetadata If true, this writes the metadata section. This adds a *DATA_TYPE* or
   *     *SCALAR* attribute to each column.
   * @param writeDataRows This is the maximum number of data rows to write. Use Integer.MAX_VALUE to
   *     write all.
   * @throws Exception if trouble. No_data is not an error.
   */
  /* project not finished or tested
      public void writeNccsvDos(DataOutputStream dos,   //should be Writer to a UTF-8 file
          boolean catchScalars,
          boolean writeMetadata, int writeDataRows) throws Exception {

          //figure out what's what
          int nc = nColumns();
          int nr = Integer.MAX_VALUE;  //shortest non-scalar pa (may be scalars have 1, others 0 or many)
          boolean isScalar[] = new boolean[nc];
          boolean allScalar = true;
          int firstNonScalar = nc;
          for (int c = 0; c < nc; c++) {
              PrimitiveArray pa = columns.get(c);
              isScalar[c] = catchScalars && pa.size() > 0 && pa.allSame();
              if (!isScalar[c]) {
                  nr = Math.min(nr, pa.size());
                  allScalar = false;
                  if (firstNonScalar == nc)
                      firstNonScalar = c;
              }
          }

          //write metadata
          if (writeMetadata) {
              globalAttributes.writeNccsvDos(dos, String2.NCCSV_GLOBAL);

              for (int c = 0; c < nc; c++) {
                  //scalar
                  if (isScalar[c]) {
                      String2.writeNccsvDos(dos, getColumnName(c));
                      String2.writeNccsvDos(dos, String2.NCCSV_SCALAR);
                      columns.get(c).subset(0, 1, 0).writeNccsvDos(dos);
                  } else {
                      String2.writeNccsvDos(dos, getColumnName(c));
                      String2.writeNccsvDos(dos, String2.NCCSV_DATATYPE);
                      StringArray sa = new StringArray();
                      sa.add(columns.get(c).elementTypeString());
                      sa.writeNccsvDos(dos);
                  }
                  columnAttributes(c).writeNccsvDos(dos, getColumnName(c));
              }
              String2.writeNccsvDos(dos, String2.NCCSV_END_METADATA);
          }

          if (writeDataRows <= 0)
              return;

          //write the non-scalar column data
          if (!allScalar) {
              //column names
              for (int c = firstNonScalar; c < nc; c++) {
                  if (isScalar[c])
                      continue;
                  String2.writeNccsvDos(dos, getColumnName(c));
              }

              //csv data
              int tnr = Math.min(nr, writeDataRows);
              for (int r = 0; r < tnr; r++) {
                  for (int c = firstNonScalar; c < nc; c++) {
                      if (isScalar[c])
                          continue;

                      columns.get(c).writeNccsvDos(dos, r);
                  }
              }
          }
          //String2.writeNccsvDos(dos, String2.NCCSV_END_DATA);
      }
  */

  /**
   * 2021: NOT FINISHED. This reads and flattens a group of variables in a sequence or nested
   * sequence in a .nc or .bufr file. <br>
   * For strings, this always calls String2.trimEnd(s)
   *
   * @param fullName This may be a local file name, an "http:" address of a .nc or .bufr file, an
   *     .ncml file (which must end with ".ncml"), or an opendap url.
   *     <p>If the fullName is an http address, the name needs to start with "http://" or "https://"
   *     (upper or lower case) and the server needs to support "byte ranges" (see
   *     ucar.nc2.NetcdfFile documentation). But this is very slow, so not recommended.
   * @param loadVarNames Use the format sequenceName.name or sequenceName.sequenceName.name. If
   *     loadVarNames is specified, those variables will be loaded. If loadVarNames isn't specified,
   *     this method reads vars which use the specified loadDimNames and scalar vars. <br>
   *     If a specified var isn't in the file, there won't be a column in the results table for it
   *     and it isn't an error.
   * @param loadSequenceNames. If loadVarNames is specified, this is ignored. If loadSequenceNames
   *     is used, only this outer sequence and/or nested sequence is read. If loadDimNames isn't
   *     specified (or size=0), this method finds the first sequence (and nested sequence). So if
   *     you want to get just the scalar vars, request a nonexistent dimension (e.g., ZZTOP).
   * @param getMetadata if true, global and variable metadata is read
   * @param standardizeWhat see Attributes.unpackVariable's standardizeWhat
   * @param conVars the names of the constraint variables. May be null. It is up to this method how
   *     much they will be used. Currently, the constraints are just used for *quick* tests to see
   *     if the file has no matching data. If a conVar isn't in the loadVarNames (provided or
   *     derived), then the constraint isn't used. If standardizeWhat != 0, the constaints are
   *     applied to the unpacked variables.
   * @param conOps the operators for the constraints. All ERDDAP ops are supported. May be null.
   * @param conVals the values of the constraints. May be null.
   * @throws Exception if unexpected trouble. But if none of the specified loadVariableNames are
   *     present or a requested dimension's size=0, it is not an error and it returns an empty
   *     table.
   */
  /*    public void readNcSequence(String fullName,
          StringArray loadVarNames,
          StringArray loadSequenceNames,
          boolean getMetadata,
          int standardizeWhat,
          StringArray conVars, StringArray conOps, StringArray conVals) throws Exception {

          //clear the table
          clear();
          HashSet<String> loadVarNamesSet = null;
          if (loadVarNames != null && loadVarNames.size() > 0) {
              loadVarNamesSet = new HashSet();
              for (int i = 0; i < loadVarNames.size(); i++)
                  loadVarNamesSet.add(loadVarNames.get(i));
          }
          HashSet<String> loadSequenceNamesSet = null;
          if (loadSequenceNames != null && loadSequenceNames.size() > 0) {
              loadSequenceNamesSet = new HashSet();
              for (int i = 0; i < loadSequenceNames.size(); i++)
                  loadSequenceNamesSet.add(loadSequenceNames.get(i));
          }
          if (standardizeWhat != 0)
              getMetadata = true;
          String msg = "  Table.readNcSequence " + fullName +
              "\n  loadVars=" + loadVarNames;
          long time = System.currentTimeMillis();
          String warningInMethod = "Table.readNcSequence read " + fullName + ":\n";
          boolean haveConstraints =
              conVars != null && conVars.size() > 0 &&
              conOps  != null && conOps.size() == conVars.size() &&
              conVals != null && conVals.size() == conVars.size();

          //read the file
          Attributes gridMappingAtts = null;
          NetcdfFile ncFile = NcHelper.openFile(fullName);
          try {

              //load the global metadata
              if (getMetadata)
                  NcHelper.getGroupAttributes(ncFile.getRootGroup(), globalAttributes());

              //go through all the variables
              List<Variable> allVars = ncFile.getVariables();
              int nAllVars = allVars.size();
              StringArray seqNames = new StringArray();
              boolean printSeq3Error = true;
              for (Variable outerVar : allVars) {

                  String outerVarName = outerVar.getFullName();
                  String2.log("outerVar=" + outerVarName);


                  //is it a sequence???
                  if (outerVar instanceof Sequence seq1) {
  // new attempt
                      ArraySequence arSeq = (ArraySequence)seq1.read(); //reads all, in memory
                      List<StructureMembers.Member> memberList1 = arSeq.getMembers();
                      for (StructureMembers.Member mem1 : memberList1) {
                          String memName1 = outerVarName + "." + mem1.getFullName();
                          if (loadVarNamesSet == null || loadVarNamesSet.contains(memName1)) {
                              //add it
                              Array tar = arSeq.extractMemberArray(mem1);
   String2.log("mem1=" + memName1 + "=" + tar.getClass().getCanonicalName());
                              if (tar instanceof ArrayObject.D1 seq2) {
                                  //member is a sequence
                                  String2.log("[0]=" + seq2.get(0).getClass().getCanonicalName());


                              } else {
                                  //simple member
                                  addColumn(nColumns(), memName1,
                                      NcHelper.getPrimitiveArray(tar, false, tar.isUnsigned()), //buildStringFromChar?, tar in .nc4 may be unsigned
                                      new Attributes());
                              }
                          }
                      }




  /*
                      //e.g., "obs" in the test file
                      StructureDataIterator seqIter1 = seq1.getStructureIterator(65536); //go through the rows
                      int rowNum1 = -1;
                      try {
                          while (seqIter1.hasNext()) {
                              StructureData sd1 = seqIter1.next();  //a row
                              rowNum1++;
                              int memberNum1 = -1;
                              ArrayList<PrimitiveArray> pas1 = new ArrayList(); //the pa in this table for each member (or null)
                              for (Iterator sdIter1 = sd1.getMembers().iterator(); sdIter1.hasNext(); ) { //go through members/columns
                                  StructureMembers.Member m1 = (StructureMembers.Member)sdIter1.next();
                                  memberNum1++;
                                  String mFullName1 = outerVarName + "." + m1.getFullName(); //getFullName isn't full name
      String2.log("mFullName1=" + mFullName1);

                                  //is it a sequence???
                                  if (m1 instanceof Sequence seq2) {
                                      //e.g., "seq1" in the test file
                                      StructureDataIterator seqIter2 = seq2.getStructureIterator(65536); //go through the rows
                                      int rowNum2 = -1;
                                      try {
                                          while (seqIter2.hasNext()) {
                                              StructureData sd2 = seqIter2.next();  //a row
                                              rowNum2++;
                                              int memberNum2 = -1;
                                              ArrayList<PrimitiveArray> pas2 = new ArrayList(); //the pa in this table for each member (or null)
                                              for (Iterator sdIter2 = sd2.getMembers().iterator(); sdIter2.hasNext(); ) { //go through members/columns
                                                  StructureMembers.Member m2 = (StructureMembers.Member)sdIter2.next();
                                                  memberNum2++;

                                                  //is it a sequence???
                                                  if (m2 instanceof Sequence)
                                                      throw new RuntimeException("3+ levels of sequences isn't supported.");

                                                  if (rowNum2 == 0) {
                                                      //first row of the seq2
                                                      String mFullName2 = mFullName1 + "." + m2.getFullName(); //getFullName isn't full name
                  String2.log("mFullName2=" + mFullName2);
                                                      PrimitiveArray tpa2 = NcHelper.getPrimitiveArray(sd2.getArray(m2), false); //buildStringsFromChar

                                                      int col = findColumnNumber(mFullName2);
                                                      if (col >= 0) {
                                                          PrimitiveArray pa = getColumn(col);
                                                          pa.append(tpa2);
                                                          pas2.add(pa);
                                                      } else if (loadVarNamesSet == null || loadVarNamesSet.contains(mFullName1)) {
                                                          //add it
                                                          addColumn(nColumns(), mFullName2, tpa2, new Attributes());
                                                          pas2.add(tpa2);
                                                      } else {
                                                          //don't store this member's data
                                                          pas2.add(null);
                                                      }

                                                  } else {
                                                      //subsequent rows: append the data to known pa
                                                      PrimitiveArray pa = pas2.get(memberNum2);
                                                      if (pa != null)
                                                          pa.append(NcHelper.getPrimitiveArray(m2.getDataArray(), false)); //buildStringsFromChar
                                                  }
                                              } //end sdIter2 for loop
                                          } //end seqIter2 while loop
                                      } finally {
                                          seqIter2.close();
                                      }

                                  } else { //m1 isn't a sequence
                                      if (rowNum1 == 0) {
                                          //first row of the seq1
                                          PrimitiveArray tpa1 = NcHelper.getPrimitiveArray(sd1.getArray(m1), false); //buildStringsFromChar

                                          int col = findColumnNumber(mFullName1);
                                          if (col >= 0) {
                                              PrimitiveArray pa = getColumn(col);
                                              pa.append(tpa1);
                                              pas1.add(pa);
                                          } else if (loadVarNamesSet == null || loadVarNamesSet.contains(mFullName1)) {
                                              //add it
                                              addColumn(nColumns(), mFullName1, tpa1, new Attributes());
                                              pas1.add(tpa1);
                                          } else {
                                              //don't store this member's data
                                              pas1.add(null);
                                          }

                                      } else {
                                          //subsequent rows: append the data to known pa
                                          PrimitiveArray pa = pas1.get(memberNum1);
                                          if (pa != null)
                                              pa.append(NcHelper.getPrimitiveArray(m1.getDataArray(), false)); //buildStringsFromChar
                                      }
                                  }
                              } //end sdIter1 for loop
                          } //end seqIter1 while loop
                      } finally {
                          seqIter1.close();
                      }
  */
  /*
                  } else { //outerVar isn't a sequence
                      if (loadVarNamesSet == null || loadVarNamesSet.contains(outerVarName)) {
                          PrimitiveArray tpa = NcHelper.getPrimitiveArray(outerVar.read(), false, NcHelper.isUnsigned(outerVar)); //buildStringsFromChar
                          addColumn(nColumns(), outerVarName, tpa, new Attributes());
                      }
                  }

                  ensureColumnsAreSameSize_LastValue();
                  String2.log(toString());
              }

              if (reallyVerbose)
                  String2.log(msg +
                      " finished. nRows=" + nRows() + " nCols=" + nColumns() +
                      " time=" + (System.currentTimeMillis() - time) + "ms");
          } finally {
              try {if (ncFile != null) ncFile.close(); } catch (Exception e9) {}
          }
      }
  */

  /**
   * THIS IS NOT FINISHED. This creates or appends the data to a flat .nc file with an unlimited
   * dimension. If the file doesn't exist, it will be created. If the file does exist, the data will
   * be appended.
   *
   * <p>If the file is being created, the attributes are used (otherwise, they aren't). String
   * variables should have an integer "strlen" attribute which specifies the maximum number of
   * characters for the column (otherwise, strlen will be calculated from the longest String in the
   * current chunk of data and written as the strlen attribute).
   *
   * <p>If the file exists and a colName in this table isn't in it, the column is ignored.
   *
   * <p>If the file exists and this table lacks a column in it, the column will be filled with the
   * file-defined _FillValue (first choice) or missing_value or the standard PrimitiveArray missing
   * value (last choice).
   *
   * @param fileName
   * @param dimName e.g., "time" or
   * @throws Exception if trouble (but the file will be closed in all cases)
   */
  /*    public static void saveAsUnlimitedNc(String fileName, String dimName) throws Exception {

          String msg = "  Table.saveAsUnlimited " + fileName;
          long time = System.currentTimeMillis();
          int nCols = nColumns();
          int nRows = nRows();
          int strlens[] = new int[nCols];  //all 0's
          boolean fileExists = File2.isFile(fileName);
          NetcdfFormatWriter ncWriter = null;
          Dimension dim;

          try {
              NetcdfFormatWriter.Builder file = null;
              Group.Builder rootGroup = null;

              if (fileExists) {
                  file = NetcdfFormatWriter.openExisting(fileName);
                  rootGroup = file.getRootGroup();
                  dim = file.findDimension(dimName);

              } else {
                  //create the file
                  file = NetcdfFormatWriter.createNewNetcdf3(fileName);
                  boolean nc3Mode = true;
                  rootGroup = file.getRootGroup();

                  NcHelper.setAttributes(rootGroup, globalAttributes());

                  //define unlimited dimension
                  dim = file.addUnlimitedDimension(dimName);
                  ArrayList<Dimension> dims = new ArrayList();
                  dims.add(dim);

                  //define Variables
                  Variable.Builder colVars[] = new Variable.Builder[nCols];
                  for (int col = 0; col < nCols; col++) {
                      String colName = getColumnName(col);
                      PrimitiveArray pa = column(col);
                      Attributes atts = new Attributes(columnAttributes(col)); //use a copy
                      if (pa.elementType() == PAType.STRING) {
                          //create a string variable
                          int strlen = atts.getInt("strlen");
                          if (strlen <= 0 || strlen == Integer.MAX_VALUE) {
                              strlen = Math.max(1, ((StringArray)pa). maximumLength());
                              atts.set("strlen", strlen);
                          }
                          strlens[col] = strlen;
                          Dimension tDim = file.addUnlimitedDimension(colName + "_strlen");
                          ArrayList<Dimension> tDims = new ArrayList();
                          tDims.add(dim);
                          tDims.add(tDim);
                          colVars[col] = NcHelper.addNc3StringVariable(rootGroup, colName, dims, strlen);

                      } else {
                          //create a non-string variable
                          colVars[col] = NcHelper.addVariable(rootGroup, colName,
                              NcHelper.getNc3DataType(pa.elementType()), dims);
                      }

                      if (pa.elementType() == PAType.CHAR)
                          atts.add(String2.CHARSET, File2.ISO_8859_1);
                      else if (pa.elementType() == PAType.STRING)
                          atts.add(File2.ENCODING, File2.ISO_8859_1);
                      NcHelper.setAttributes(colVars[col], atts);
                  }

                  //switch to create mode
                  ncWriter = file.build();
              }

              //add the data
              int fileNRows = dim.getLength();
              int[] origin1 = new int[] {row};
              int[] origin2 = new int[] {row, 0};
              vars
              while (...) {

                  Variable var = vars.get  ;
                  class elementPAType = NcHelper.  var.
                  //ArrayList<Dimension> tDims = var.getDimensions();
                  String colName = var.getFullName();
                  Attributes atts = new Attributes();
                  NcHelper.getAttributes(colName, atts);
                  int col = findColumnNumber(colName);
                  PrimitiveArray pa = null;
                  if (col < 0) {
                      //the var has nothing comparable in this table,
                      //so make a pa filled with missing values
                      if (ndims > 1) {
                          //string vars always use "" as mv
                          continue;
                      }
                      //make a primitive array
                      PrimitiveArray pa = PrimitiveArray.factory(type, 1, false);
                      String mv = atts.getString("_FillValue");
                      if (mv == null)
                          mv = atts.getString("missing_value");
                      if (mv == null)
                          mv = pa.getMV();
                      pa.addNStrings(nRows, mv);

                  } else {
                      //get data from this table
                      pa = getColumn(col);
                  }

                  //write the data
                  if (pa.elementType() == PAType.STRING) {
                      //write string data
                      if (fileExists) {
                          ..just get one att from file
                          Attributes atts = columnAttributes(col);
                          strlens[col] = NcHelper.getAttribute ts.getInt("strlen");
                      }
                      if (strlens[col] <= 0 || strlens[col] == Integer.MAX_VALUE)
                          throw new SimpleException("\"strlen\" attribute not found for variable=" + colName);

                      ArrayChar.D2 ac = new ArrayChar.D2(2, strlens[col]);
                      int n = pa.size();
                      for (int i = 0; i < n; i++)
                          ac.setString(i, pa.getString(i));
                      ncWriter.write(colVars[col].getFullName(), origin2, ac);

                  } else {
                      //write non-string data
                      ncWriter.write(colVars[col].getFullName(), origin1, Array.factory(pa.toArray()));
                  }
              }
              ncWriter.close();
              ncWriter = null;

              if (reallyVerbose) msg +=
                  " finished. nColumns=" + nColumns() + " nRows=" + nRows() +
                  " TIME=" + (System.currentTimeMillis() - time) + "ms";

          } catch (Throwable t) {
              String2.log(NcHelper.ERROR_WHILE_CREATING_NC_FILE + MustBe.throwableToString(t));
              if (ncWriter != null) {
                  try {ncWriter.abort(); } catch (Exception e9) {}
                  File2.delete(fileName);
                  ncWriter = null;
              }

              if (!reallyVerbose) String2.log(msg);
              throw t;

          }
      }
  */

  /* *  THIS IS INACTIVE.
   * This reads a NetCDF file with no structure or groups, but with
   * at least one dimension and at least 1 1D array variable that uses that
   * dimension, and populates the public variables.
   * Suitable files include LAS Intermediate NetCDF files.
   *
   * <p>If there is a time variable with attribute "units"="seconds",
   *   and storing seconds since 1970-01-01T00:00:00Z.
   *   See [COARDS] "Time or date dimension".
   * <p>The file may have a lat variable with attribute "units"="degrees_north"
   *   to identify the latitude variable. See [COARDS] "Latitude Dimension".
   *   It can be of any numeric data type.
   * <p>The file may have a lon variable with attribute "units"="degrees_east"
   *   to identify the longitude variable. See [COARDS] "Longitude Dimension".
   *   It can be of any numeric data type.
   *
   * <p>netcdf files are read with code in
   * netcdf-X.X.XX.jar which is part of the
   * <a href="https://www.unidata.ucar.edu/software/netcdf-java/"
   * >NetCDF Java Library</a>
   * renamed as netcdf-latest.jar.
   * Put it in the classpath for the compiler and for Java.
   *
   * <p>This sets globalAttributes and columnAttributes.
   *
   * @param fullFileName the full name of the file, for diagnostic messages.
   * @param ncFile an open ncFile
   * @param standardizeWhat see Attributes.unpackVariable's standardizeWhat
   * @param okRows indicates which rows should be kept.
   *   This is used as the starting point for the tests (the tests may reject
   *   rows which are initially ok) or can be used without tests. It may be null.
   * @param testColumns the names of the columns to be tested (null = no tests).
   *   All of the test columns must use the same, one, dimension that the
   *   loadColumns use.
   *   Ideally, the first tests will greatly restrict the range of valid rows.
   * @param testMin the minimum allowed value for each testColumn (null = no tests)
   * @param testMax the maximum allowed value for each testColumn (null = no tests)
   * @param loadColumns the names of the columns to be loaded.
   *     They must all be ArrayXxx.D1 or ArrayChar.D2 variables and use the
   *     same, one, dimension as the first dimension.
   *     If loadColumns is null, this will read all of the variables in the
   *     main group which use the biggest rootGroup dimension as their
   *     one and only dimension.
   * @throws Exception if trouble
   */
  /*    public void readNetCDF(String fullFileName, NetcdfFile ncFile,
      int standardizeWhat, BitSet okRows,
      String testColumns[], double testMin[], double testMax[],
      String loadColumns[]) throws Exception {

      //if (reallyVerbose) String2.log(File2.hexDump(fullFileName, 300));
      if (reallyVerbose) String2.log("Table.readNetCDF" +
          "\n  testColumns=" + String2.toCSSVString(testColumns) +
          "\n  testMin=" + String2.toCSSVString(testMin) +
          "\n  testMax=" + String2.toCSSVString(testMax) +
          "\n  loadColumns=" + String2.toCSSVString(loadColumns));

      //setup
      long time = System.currentTimeMillis();
      clear();
      String errorInMethod = String2.ERROR + " in Table.readNetCDF(" + fullFileName + "):\n";

      //*** ncdump  //this is very slow for big files
      //if (reallyVerbose) String2.log(NcHelper.ncdump(fullFileName, "-h"));

      //read the globalAttributes
      if (reallyVerbose) String2.log("  read the globalAttributes");
      globalAttributes = new ArrayList();
      List globalAttList = ncFile.globalAttributes();
      for (int att = 0; att < globalAttList.size(); att++) {
          Attribute gAtt = (Attribute)globalAttList.get(att);
          globalAttributes.add(gAtt.getShortName());
          globalAttributes.add(PrimitiveArray.factory(
              DataHelper.getArray(gAtt.getValues())));
      }

      //find the mainDimension
      Dimension mainDimension = null;
      if (loadColumns == null) {
          //assume mainDimension is the biggest dimension
          //FUTURE: better to look for 1d arrays and find the largest?
          //   Not really, because lat and lon could have same number
          //   but they are different dimension.
          List dimensions = ncFile.getDimensions(); //next nc version: rootGroup.getDimensions();
          if (dimensions.size() == 0)
              throw new SimpleException(errorInMethod + "the file has no dimensions.");
          mainDimension = (Dimension)dimensions.get(0);
          if (!mainDimension.isUnlimited()) {
              for (int i = 1; i < dimensions.size(); i++) {
                  if (reallyVerbose) String2.log("  look for biggest dimension, check " + i);
                  Dimension tDimension = (Dimension)dimensions.get(i);
                  if (tDimension.isUnlimited()) {
                      mainDimension = tDimension;
                      break;
                  }
                  if (tDimension.getLength() > mainDimension.getLength())
                      mainDimension = tDimension;
              }
          }
      } else {
          //if loadColumns was specified, get mainDimension from loadColumns[0]
          if (reallyVerbose) String2.log("  get mainDimension from loadColumns[0]");
          Variable v = ncFile.findVariable(loadColumns[0]);
          mainDimension = v.getDimension(0);
      }


      //make a list of the needed variables (loadColumns and testColumns)
      ArrayList<Variable> allVariables = new ArrayList();
      if (loadColumns == null) {
          //get a list of all variables which use just mainDimension
          List variableList = ncFile.getVariables();
          for (int i = 0; i < variableList.size(); i++) {
              if (reallyVerbose) String2.log("  get all variables which use mainDimension, check " + i);
              Variable tVariable = (Variable)variableList.get(i);
              List tDimensions = tVariable.getDimensions();
              int nDimensions = tDimensions.size();
              if (reallyVerbose) String2.log("i=" + i + " name=" + tVariable.getFullName() +
                  " type=" + tVariable.getDataType());
              if ((nDimensions == 1 && tDimensions.get(0).equals(mainDimension)) ||
                  (nDimensions == 2 && tDimensions.get(0).equals(mainDimension) &&
                       tVariable.getDataType() == DataType.CHAR)) {
                      allVariables.add(tVariable);
              }
          }
      } else {
          //make the list from the loadColumns and testColumns
          for (int i = 0; i < loadColumns.length; i++) {
              if (reallyVerbose) String2.log("  getLoadColumns " + i);
              allVariables.add(ncFile.findVariable(loadColumns[i]));
          }
          if (testColumns != null) {
              for (int i = 0; i < testColumns.length; i++) {
                  if (String2.indexOf(loadColumns, testColumns[i]) < 0) {
                      if (reallyVerbose) String2.log("  getTestColumns " + i);
                      allVariables.add(ncFile.findVariable(testColumns[i]));
                  }
              }
          }
      }
      if (reallyVerbose) String2.log("  got AllVariables " + allVariables.size());

      //get the data
      getNetcdfSubset(errorInMethod, allVariables, standardizeWhat, okRows,
          testColumns, testMin, testMax, loadColumns);

      if (reallyVerbose)
          String2.log("Table.readNetCDF nColumns=" + nColumns() +
              " nRows=" + nRows() + " time=" + (System.currentTimeMillis() - time) + "ms");

  }*/
}
