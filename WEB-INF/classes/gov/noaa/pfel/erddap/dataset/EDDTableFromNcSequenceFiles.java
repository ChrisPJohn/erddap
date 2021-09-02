/* 
 * EDDTableFromNcSequenceFiles Copyright 2021, NOAA.
 * See the LICENSE.txt file in this file's directory.
 */
package gov.noaa.pfel.erddap.dataset;

import com.cohort.array.Attributes;
import com.cohort.array.ByteArray;
import com.cohort.array.CharArray;
import com.cohort.array.DoubleArray;
import com.cohort.array.FloatArray;
import com.cohort.array.IntArray;
import com.cohort.array.PAOne;
import com.cohort.array.PAType;
import com.cohort.array.PrimitiveArray;
import com.cohort.array.ShortArray;
import com.cohort.array.StringArray;
import com.cohort.util.Calendar2;
import com.cohort.util.File2;
import com.cohort.util.Math2;
import com.cohort.util.MustBe;
import com.cohort.util.SimpleException;
import com.cohort.util.String2;
import com.cohort.util.Test;
import com.cohort.util.XML;

import gov.noaa.pfel.coastwatch.griddata.NcHelper;
import gov.noaa.pfel.coastwatch.pointdata.Table;
import gov.noaa.pfel.coastwatch.sgt.SgtUtil;
import gov.noaa.pfel.coastwatch.util.FileVisitorDNLS;
import gov.noaa.pfel.coastwatch.util.HtmlWidgets;
import gov.noaa.pfel.coastwatch.util.RegexFilenameFilter;
import gov.noaa.pfel.coastwatch.util.SSR;
import gov.noaa.pfel.coastwatch.util.Tally;

import gov.noaa.pfel.erddap.GenerateDatasetsXml;
import gov.noaa.pfel.erddap.util.EDStatic;
import gov.noaa.pfel.erddap.variable.*;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

// from netcdfAll-x.jar
import ucar.ma2.*;
import ucar.nc2.*;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dataset.NetcdfDatasets;
//import ucar.nc2.dods.*;
import ucar.nc2.util.*;
import ucar.nc2.write.NetcdfFormatWriter;

/** 
 * This class represents a table of data from a collection of .nc data files with a sequence or nested sequences.
 *
 * @author Bob Simons (bob.simons@noaa.gov) 2021-08-23
 */
public class EDDTableFromNcSequenceFiles extends EDDTableFromFiles { 

    /**
     * This returns the default value for standardizeWhat for this subclass.
     * See Attributes.unpackVariable for options.
     * The default was chosen to mimic the subclass' behavior from
     * before support for standardizeWhat options was added.
     */
    public int defaultStandardizeWhat() {return DEFAULT_STANDARDIZEWHAT; } 
    public static int DEFAULT_STANDARDIZEWHAT = 0;


    /** 
     * The constructor just calls the super constructor. 
     *
     * <p>The sortedColumnSourceName can't be for a char/String variable
     *   because NcHelper binary searches are currently set up for numeric vars only.
     *
     * @param tAccessibleTo is a comma separated list of 0 or more
     *    roles which will have access to this dataset.
     *    <br>If null, everyone will have access to this dataset (even if not logged in).
     *    <br>If "", no one will have access to this dataset.
     * @param tFgdcFile This should be the fullname of a file with the FGDC
     *    that should be used for this dataset, or "" (to cause ERDDAP not
     *    to try to generate FGDC metadata for this dataset), or null (to allow
     *    ERDDAP to try to generate FGDC metadata for this dataset).
     * @param tIso19115 This is like tFgdcFile, but for the ISO 19119-2/19139 metadata.
     */
    public EDDTableFromNcSequenceFiles(String tDatasetID, 
        String tAccessibleTo, String tGraphsAccessibleTo,
        StringArray tOnChange, String tFgdcFile, String tIso19115File, 
        String tSosOfferingPrefix,
        String tDefaultDataQuery, String tDefaultGraphQuery, 
        Attributes tAddGlobalAttributes,
        Object[][] tDataVariables,
        int tReloadEveryNMinutes, int tUpdateEveryNMillis,
        String tFileDir, String tFileNameRegex, boolean tRecursive, String tPathRegex, 
        String tMetadataFrom, String tCharset, 
        String tSkipHeaderToRegex, String tSkipLinesRegex,
        int tColumnNamesRow, int tFirstDataRow, String tColumnSeparator,
        String tPreExtractRegex, String tPostExtractRegex, String tExtractRegex, 
        String tColumnNameForExtract,
        String tSortedColumnSourceName, String tSortFilesBySourceNames,
        boolean tSourceNeedsExpandedFP_EQ, boolean tFileTableInMemory, 
        boolean tAccessibleViaFiles, boolean tRemoveMVRows, 
        int tStandardizeWhat, int tNThreads, 
        String tCacheFromUrl, int tCacheSizeGB, String tCachePartialPathRegex,
        String tAddVariablesWhere) 
        throws Throwable {

        super("EDDTableFromNcSequenceFiles", tDatasetID, 
            tAccessibleTo, tGraphsAccessibleTo, 
            tOnChange, tFgdcFile, tIso19115File, tSosOfferingPrefix, 
            tDefaultDataQuery, tDefaultGraphQuery,
            tAddGlobalAttributes, 
            tDataVariables, tReloadEveryNMinutes, tUpdateEveryNMillis,
            tFileDir, tFileNameRegex, tRecursive, tPathRegex, tMetadataFrom,
            tCharset, tSkipHeaderToRegex, tSkipLinesRegex,
            tColumnNamesRow, tFirstDataRow, tColumnSeparator,
            tPreExtractRegex, tPostExtractRegex, tExtractRegex, tColumnNameForExtract,
            tSortedColumnSourceName, tSortFilesBySourceNames,
            tSourceNeedsExpandedFP_EQ, tFileTableInMemory, tAccessibleViaFiles,
            tRemoveMVRows, tStandardizeWhat, 
            tNThreads, tCacheFromUrl, tCacheSizeGB, tCachePartialPathRegex,
            tAddVariablesWhere);

    }

    /** 
     * The constructor for subclasses.
     */
    public EDDTableFromNcSequenceFiles(String tClassName, 
        String tDatasetID, String tAccessibleTo, String tGraphsAccessibleTo,
        StringArray tOnChange, String tFgdcFile, String tIso19115File, 
        String tSosOfferingPrefix,
        String tDefaultDataQuery, String tDefaultGraphQuery, 
        Attributes tAddGlobalAttributes,
        Object[][] tDataVariables,
        int tReloadEveryNMinutes, int tUpdateEveryNMillis,
        String tFileDir, String tFileNameRegex, boolean tRecursive, String tPathRegex, 
        String tMetadataFrom, String tCharset, 
        String tSkipHeaderToRegex, String tSkipLinesRegex,
        int tColumnNamesRow, int tFirstDataRow, String tColumnSeparator,
        String tPreExtractRegex, String tPostExtractRegex, String tExtractRegex, 
        String tColumnNameForExtract,
        String tSortedColumnSourceName, String tSortFilesBySourceNames,
        boolean tSourceNeedsExpandedFP_EQ, boolean tFileTableInMemory, 
        boolean tAccessibleViaFiles, boolean tRemoveMVRows, 
        int tStandardizeWhat, int tNThreads, 
        String tCacheFromUrl, int tCacheSizeGB, String tCachePartialPathRegex,
        String tAddVariablesWhere) 
        throws Throwable {

        super(tClassName, tDatasetID, tAccessibleTo, tGraphsAccessibleTo, 
            tOnChange, tFgdcFile, tIso19115File, tSosOfferingPrefix, 
            tDefaultDataQuery, tDefaultGraphQuery,
            tAddGlobalAttributes, 
            tDataVariables, tReloadEveryNMinutes, tUpdateEveryNMillis,
            tFileDir, tFileNameRegex, tRecursive, tPathRegex, tMetadataFrom,
            tCharset, tSkipHeaderToRegex, tSkipLinesRegex,
            tColumnNamesRow, tFirstDataRow, tColumnSeparator,
            tPreExtractRegex, tPostExtractRegex, tExtractRegex, tColumnNameForExtract,
            tSortedColumnSourceName, tSortFilesBySourceNames,
            tSourceNeedsExpandedFP_EQ, tFileTableInMemory, tAccessibleViaFiles,
            tRemoveMVRows, tStandardizeWhat, 
            tNThreads, tCacheFromUrl, tCacheSizeGB, tCachePartialPathRegex,
            tAddVariablesWhere);

    }


    /**
     * This gets source data from one file.
     * See documentation in EDDTableFromFiles.
     *
     * @throws an exception if too much data.
     *  This won't throw an exception if no data.
     */
    public Table lowGetSourceDataFromFile(String tFileDir, String tFileName, 
        StringArray sourceDataNames, String sourceDataTypes[],
        double sortedSpacing, double minSorted, double maxSorted, 
        StringArray sourceConVars, StringArray sourceConOps, StringArray sourceConValues,
        boolean getMetadata, boolean mustGetData) 
        throws Throwable {

        //read the file
        Table table = new Table();
        String decompFullName = FileVisitorDNLS.decompressIfNeeded(
            tFileDir + tFileName, fileDir, decompressedDirectory(), 
            EDStatic.decompressedCacheMaxGB, true); //reuseExisting
        if (mustGetData) {
            table.readNcSequence(decompFullName, sourceDataNames.toArray(),
                standardizeWhat,
                sortedSpacing >= 0 && !Double.isNaN(minSorted)? sortedColumnSourceName : null,
                minSorted, maxSorted);
            //String2.log("  EDDTableFromNcSequenceFiles.lowGetSourceDataFromFile table.nRows=" + table.nRows());
            //table.saveAsDDS(System.out, "s");
        } else {
            //Just return a table with globalAtts, columns with atts, but no rows.
            table.readNcSequenceMetadata(decompFullName, sourceDataNames.toArray(), sourceDataTypes,
                standardizeWhat);
        }

        return table;
    }


    /** 
     * This generates a ready-to-use datasets.xml entry for an EDDTableFromNcSequenceFiles.
     * The XML can then be edited by hand and added to the datasets.xml file.
     *
     * <p>This can't be made into a web service because it would allow any user
     * to looks at (possibly) private .nc files on the server.
     *
     * @param tFileDir the starting (parent) directory for searching for files
     * @param tFileNameRegex  the regex that each filename (no directory info) must match 
     *    (e.g., ".*\\.nc")  (usually only 1 backslash; 2 here since it is Java code). 
     *    If null or "", it is generated to catch the same extension as the sampleFileName
     *    (usually ".*\\.nc").
     * @param sampleFileName the full file name of one of the files in the collection
     * @param useDimensionsCSV If null or "", this finds the group of variables sharing the
     *    highest number of dimensions. Otherwise, it find the variables using
     *    these dimensions (plus related char variables).
     * @param tReloadEveryNMinutes  e.g., 10080 for weekly
     * @param tPreExtractRegex       part of info for extracting e.g., stationName from file name. Set to "" if not needed.
     * @param tPostExtractRegex      part of info for extracting e.g., stationName from file name. Set to "" if not needed.
     * @param tExtractRegex          part of info for extracting e.g., stationName from file name. Set to "" if not needed.
     * @param tColumnNameForExtract  part of info for extracting e.g., stationName from file name. Set to "" if not needed.
     * @param tSortedColumnSourceName   use "" if not known or not needed. 
     * @param tSortFilesBySourceNames   This is useful, because it ultimately determines default results order.
     * @param tInfoUrl       or "" if in externalAddGlobalAttributes or if not available
     * @param tInstitution   or "" if in externalAddGlobalAttributes or if not available
     * @param tSummary       or "" if in externalAddGlobalAttributes or if not available
     * @param tTitle         or "" if in externalAddGlobalAttributes or if not available
     * @param externalAddGlobalAttributes  These attributes are given priority.  Use null in none available.
     * @return a suggested chunk of xml for this dataset for use in datasets.xml 
     * @throws Throwable if trouble, e.g., if no Grid or Array variables are found.
     *    If no trouble, then a valid dataset.xml chunk has been returned.
     */
    public static String generateDatasetsXml(
        String tFileDir, String tFileNameRegex, String sampleFileName, 
        String useDimensionsCSV, int tReloadEveryNMinutes, 
        String tPreExtractRegex, String tPostExtractRegex, String tExtractRegex,
        String tColumnNameForExtract, String tSortedColumnSourceName,
        String tSortFilesBySourceNames, 
        String tInfoUrl, String tInstitution, String tSummary, String tTitle,
        int tStandardizeWhat, String tCacheFromUrl,
        Attributes externalAddGlobalAttributes) throws Throwable {

        String2.log("\n*** EDDTableFromNcSequenceFiles.generateDatasetsXml" +
            "\nfileDir=" + tFileDir + " fileNameRegex=" + tFileNameRegex +
            "\nsampleFileName=" + sampleFileName +
            "\nuseDimensionsCSV=" + useDimensionsCSV + 
            " reloadEveryNMinutes=" + tReloadEveryNMinutes +
            "\nextract pre=" + tPreExtractRegex + " post=" + tPostExtractRegex + " regex=" + tExtractRegex +
            " colName=" + tColumnNameForExtract +
            "\nsortedColumn=" + tSortedColumnSourceName + 
            " sortFilesBy=" + tSortFilesBySourceNames + 
            "\ninfoUrl=" + tInfoUrl + 
            "\ninstitution=" + tInstitution +
            "\nsummary=" + tSummary +
            "\ntitle=" + tTitle +
            "\nexternalAddGlobalAttributes=" + externalAddGlobalAttributes);

        if (!String2.isSomething(tFileDir))
            throw new IllegalArgumentException("fileDir wasn't specified.");
        tFileDir = File2.addSlash(tFileDir); //ensure it has trailing slash
        tFileNameRegex = String2.isSomething(tFileNameRegex)? 
            tFileNameRegex.trim() : ".*";
        if (String2.isRemote(tCacheFromUrl)) 
            FileVisitorDNLS.sync(tCacheFromUrl, tFileDir, tFileNameRegex,
                true, ".*", false); //not fullSync
        String[] useDimensions = StringArray.arrayFromCSV(useDimensionsCSV);
        tColumnNameForExtract = String2.isSomething(tColumnNameForExtract)?
            tColumnNameForExtract.trim() : "";
        tSortedColumnSourceName = String2.isSomething(tSortedColumnSourceName)?
            tSortedColumnSourceName.trim() : "";
        if (!String2.isSomething(sampleFileName)) 
            String2.log("Found/using sampleFileName=" +
                (sampleFileName = FileVisitorDNLS.getSampleFileName(
                    tFileDir, tFileNameRegex, true, ".*"))); //recursive, pathRegex

        //show structure of sample file
        String2.log("Let's see if netcdf-java can tell us the structure of the sample file:");
        String2.log(NcHelper.ncdump(sampleFileName, "-h"));

        //*** basically, make a table to hold the sourceAttributes 
        //and a parallel table to hold the addAttributes
        Table dataSourceTable = new Table();
        Table dataAddTable = new Table();

        //new way
        StringArray varNames = new StringArray();
        double maxTimeES = Double.NaN;
        if (useDimensions.length > 0) {
            //find the varNames
            NetcdfFile ncFile = NcHelper.openFile(sampleFileName);
            try {

                Group rootGroup = ncFile.getRootGroup();
                List rootGroupVariables = rootGroup.getVariables(); 
                for (int v = 0; v < rootGroupVariables.size(); v++) {
                    Variable var = (Variable)rootGroupVariables.get(v);
                    boolean isChar = var.getDataType() == DataType.CHAR;
                    if (var.getRank() + (isChar? -1 : 0) == useDimensions.length) {
                        boolean matches = true;
                        for (int d = 0; d < useDimensions.length; d++) {
                            if (!var.getDimension(d).getName().equals(useDimensions[d])) {  //the full name
                                matches = false;
                                break;
                            }
                        }
                        if (matches) 
                            varNames.add(var.getFullName());
                    }
                }
                ncFile.close(); 

            } catch (Exception e) {
                //make sure ncFile is explicitly closed
                try {
                    ncFile.close(); 
                } catch (Exception e2) {
                    //don't care
                }
                String2.log(MustBe.throwableToString(e)); 
            }
            Test.ensureTrue(varNames.size() > 0, 
                "The file has no variables with dimensions: " + useDimensionsCSV);
        }

        //then read the file
        tStandardizeWhat = tStandardizeWhat < 0 || tStandardizeWhat == Integer.MAX_VALUE?
            DEFAULT_STANDARDIZEWHAT : tStandardizeWhat;
        dataSourceTable.readNDNc(sampleFileName, varNames.toStringArray(), 
            tStandardizeWhat,
            null, 0, 0); 
        for (int c = 0; c < dataSourceTable.nColumns(); c++) {
            String colName = dataSourceTable.getColumnName(c);
            Attributes sourceAtts = dataSourceTable.columnAttributes(c);
            PrimitiveArray destPA = makeDestPAForGDX(dataSourceTable.getColumn(c), sourceAtts);
            dataAddTable.addColumn(c, colName, destPA, 
                makeReadyToUseAddVariableAttributesForDatasetsXml(
                    dataSourceTable.globalAttributes(), sourceAtts, null, colName, 
                    destPA.elementType() != PAType.STRING, //tryToAddStandardName
                    destPA.elementType() != PAType.STRING, //addColorBarMinMax
                    true)); //tryToFindLLAT

            //if a variable has timeUnits, files are likely sorted by time
            //and no harm if files aren't sorted that way
            String tUnits = sourceAtts.getString("units");
            if (tSortedColumnSourceName.length() == 0 && 
                Calendar2.isTimeUnits(tUnits)) 
                tSortedColumnSourceName = colName;

            if (!Double.isFinite(maxTimeES) && Calendar2.isTimeUnits(tUnits)) {
                try {
                    if (Calendar2.isNumericTimeUnits(tUnits)) {
                        double tbf[] = Calendar2.getTimeBaseAndFactor(tUnits); //throws exception
                        maxTimeES = Calendar2.unitsSinceToEpochSeconds(
                            tbf[0], tbf[1], destPA.getDouble(destPA.size() - 1));
                    } else { //string time units
                        maxTimeES = Calendar2.tryToEpochSeconds(destPA.getString(destPA.size() - 1)); //NaN if trouble
                    }
                } catch (Throwable t) {
                    String2.log("caught while trying to get maxTimeES: " + 
                        MustBe.throwableToString(t));
                }
            }
        }
        //String2.log("SOURCE COLUMN NAMES=" + dataSourceTable.getColumnNamesCSSVString());
        //String2.log("DEST   COLUMN NAMES=" + dataSourceTable.getColumnNamesCSSVString());

        //add missing_value and/or _FillValue if needed
        addMvFvAttsIfNeeded(dataSourceTable, dataAddTable);

        //globalAttributes
        if (externalAddGlobalAttributes == null)
            externalAddGlobalAttributes = new Attributes();
        if (tInfoUrl     != null && tInfoUrl.length()     > 0) externalAddGlobalAttributes.add("infoUrl",     tInfoUrl);
        if (tInstitution != null && tInstitution.length() > 0) externalAddGlobalAttributes.add("institution", tInstitution);
        if (tSummary     != null && tSummary.length()     > 0) externalAddGlobalAttributes.add("summary",     tSummary);
        if (tTitle       != null && tTitle.length()       > 0) externalAddGlobalAttributes.add("title",       tTitle);
        externalAddGlobalAttributes.setIfNotAlreadySet("sourceUrl", 
            "(" + (String2.isTrulyRemote(tFileDir)? "remote" : "local") + " files)");

        //tryToFindLLAT
        tryToFindLLAT(dataSourceTable, dataAddTable);

        //externalAddGlobalAttributes.setIfNotAlreadySet("subsetVariables", "???");
        //after dataVariables known, add global attributes in the dataAddTable
        dataAddTable.globalAttributes().set(
            makeReadyToUseAddGlobalAttributesForDatasetsXml(
                dataSourceTable.globalAttributes(), 
                //another cdm_data_type could be better; this is ok
                hasLonLatTime(dataAddTable)? "Point" : "Other",
                tFileDir, externalAddGlobalAttributes, 
                suggestKeywords(dataSourceTable, dataAddTable)));

        //subsetVariables
        if (dataSourceTable.globalAttributes().getString("subsetVariables") == null &&
               dataAddTable.globalAttributes().getString("subsetVariables") == null) 
            dataAddTable.globalAttributes().add("subsetVariables",
                suggestSubsetVariables(dataSourceTable, dataAddTable, false));

        //add the columnNameForExtract variable
        if (tColumnNameForExtract.length() > 0) {
            Attributes atts = new Attributes();
            atts.add("ioos_category", "Identifier");
            atts.add("long_name", EDV.suggestLongName(null, tColumnNameForExtract, null));
            //no units or standard_name
            dataSourceTable.addColumn(0, tColumnNameForExtract, new StringArray(), new Attributes());
            dataAddTable.addColumn(   0, tColumnNameForExtract, new StringArray(), atts);
        }

        //useMaxTimeES
        if (tReloadEveryNMinutes <= 0 || tReloadEveryNMinutes == Integer.MAX_VALUE)
            tReloadEveryNMinutes = 1440;  //1440 works well with suggestedUpdateEveryNMillis 

        String tTestOutOfDate = EDD.getAddOrSourceAtt(
            dataSourceTable.globalAttributes(), 
            dataAddTable.globalAttributes(), "testOutOfDate", null);
        if (Double.isFinite(maxTimeES) && !String2.isSomething(tTestOutOfDate)) {
            tTestOutOfDate = suggestTestOutOfDate(maxTimeES);
            if (String2.isSomething(tTestOutOfDate))
                dataAddTable.globalAttributes().set("testOutOfDate", tTestOutOfDate);
        }

        //write the information
        StringBuilder sb = new StringBuilder();
        String suggestedRegex = (tFileNameRegex == null || tFileNameRegex.length() == 0)? 
            ".*\\" + File2.getExtension(sampleFileName) :
            tFileNameRegex;
        if (tSortFilesBySourceNames.length() == 0) {
            if (tColumnNameForExtract.length() > 0 &&
                tSortedColumnSourceName.length() > 0 &&
                !tColumnNameForExtract.equals(tSortedColumnSourceName))
                tSortFilesBySourceNames = tColumnNameForExtract + ", " + tSortedColumnSourceName;
            else if (tColumnNameForExtract.length() > 0)
                tSortFilesBySourceNames = tColumnNameForExtract;
            else 
                tSortFilesBySourceNames = tSortedColumnSourceName;
        }
        sb.append(
            "<dataset type=\"EDDTableFromNcSequenceFiles\" datasetID=\"" + 
                suggestDatasetID(tFileDir + suggestedRegex) +  //dirs can't be made public
                "\" active=\"true\">\n" +
            "    <reloadEveryNMinutes>" + tReloadEveryNMinutes + "</reloadEveryNMinutes>\n" +  
            (String2.isUrl(tCacheFromUrl)? 
              "    <cacheFromUrl>" + XML.encodeAsXML(tCacheFromUrl) + "</cacheFromUrl>\n" :
              "    <updateEveryNMillis>" + suggestUpdateEveryNMillis(tFileDir) + "</updateEveryNMillis>\n") +  
            "    <fileDir>" + XML.encodeAsXML(tFileDir) + "</fileDir>\n" +
            "    <fileNameRegex>" + XML.encodeAsXML(suggestedRegex) + "</fileNameRegex>\n" +
            "    <recursive>true</recursive>\n" +
            "    <pathRegex>.*</pathRegex>\n" +
            "    <metadataFrom>last</metadataFrom>\n" +
            "    <standardizeWhat>" + tStandardizeWhat + "</standardizeWhat>\n" +
            (String2.isSomething(tColumnNameForExtract)? //Discourage Extract. Encourage sourceName=***fileName,...
              "    <preExtractRegex>" + XML.encodeAsXML(tPreExtractRegex) + "</preExtractRegex>\n" +
              "    <postExtractRegex>" + XML.encodeAsXML(tPostExtractRegex) + "</postExtractRegex>\n" +
              "    <extractRegex>" + XML.encodeAsXML(tExtractRegex) + "</extractRegex>\n" +
              "    <columnNameForExtract>" + XML.encodeAsXML(tColumnNameForExtract) + "</columnNameForExtract>\n" : "") +
            "    <sortedColumnSourceName>" + XML.encodeAsXML(tSortedColumnSourceName) + "</sortedColumnSourceName>\n" +
            "    <sortFilesBySourceNames>" + XML.encodeAsXML(tSortFilesBySourceNames) + "</sortFilesBySourceNames>\n" +
            "    <fileTableInMemory>false</fileTableInMemory>\n");
        sb.append(writeAttsForDatasetsXml(false, dataSourceTable.globalAttributes(), "    "));
        sb.append(cdmSuggestion());
        sb.append(writeAttsForDatasetsXml(true,     dataAddTable.globalAttributes(), "    "));

        //last 2 params: includeDataType, questionDestinationName
        sb.append(writeVariablesForDatasetsXml(dataSourceTable, dataAddTable, 
            "dataVariable", true, false));
        sb.append(
            "</dataset>\n" +
            "\n");

        String2.log("\n\n*** generateDatasetsXml finished successfully.\n\n");
        return sb.toString();
        
    }


    /**
     * testGenerateDatasetsXml
     */
    public static void testGenerateDatasetsXml() throws Throwable {
        testVerboseOn();

        String2.log("\n*** EDDTableFromNcSequenceFiles.testGenerateDatasetsXml");

        try {
            String results = generateDatasetsXml(
                "C:/u00/data/points/ndbcMet2", ".*\\.nc",
                "C:/u00/data/points/ndbcMet2/nrt/NDBC_41004_met.nc",
                "",
                -1,
                "^.{5}", ".{7}$", ".*", "stationID", //just for test purposes; station is already a column in the file
                "TIME", "stationID TIME", 
                "", "", "", "", 
                -1, //defaultStandardizeWhat
                null,
                null) + "\n";
            results = results.replaceAll(
                "<att name=\"actual_range\" type=\"floatList\">.*</att>", 
                "<att name=\"actual_range\" type=\"floatList\">... ...</att>");
            results = results.replaceAll(
                "<att name=\"actual_range\" type=\"shortList\">.*</att>", 
                "<att name=\"actual_range\" type=\"shortList\">... ...</att>");

            //GenerateDatasetsXml
            String gdxResults = (new GenerateDatasetsXml()).doIt(new String[]{"-verbose", 
                "EDDTableFromNcSequenceFiles",
                "C:/u00/data/points/ndbcMet2", ".*\\.nc",
                "C:/u00/data/points/ndbcMet2/nrt/NDBC_41004_met.nc",
                "",
                "-1",
                "^.{5}", ".{7}$", ".*", "stationID", //just for test purposes; station is already a column in the file
                "TIME", "stationID TIME", 
                "", "", "", "",
                "-1", ""}, //defaultStandardizeWhat
                false); //doIt loop?
            gdxResults = gdxResults.replaceAll(
                "<att name=\"actual_range\" type=\"floatList\">.*</att>", 
                "<att name=\"actual_range\" type=\"floatList\">... ...</att>");
            gdxResults = gdxResults.replaceAll(
                "<att name=\"actual_range\" type=\"shortList\">.*</att>",
                "<att name=\"actual_range\" type=\"shortList\">... ...</att>");

            Test.ensureEqual(gdxResults, results, "Unexpected results from GenerateDatasetsXml.doIt.");

String expected = 
"<dataset type=\"EDDTableFromNcSequenceFiles\" datasetID=\"ndbcMet2_73a7_3fce_afec\" active=\"true\">\n" +
"    <reloadEveryNMinutes>1440</reloadEveryNMinutes>\n" +
"    <updateEveryNMillis>10000</updateEveryNMillis>\n" +
"    <fileDir>C:/u00/data/points/ndbcMet2/</fileDir>\n" +
"    <fileNameRegex>.*\\.nc</fileNameRegex>\n" +
"    <recursive>true</recursive>\n" +
"    <pathRegex>.*</pathRegex>\n" +
"    <metadataFrom>last</metadataFrom>\n" +
"    <standardizeWhat>0</standardizeWhat>\n" +
"    <preExtractRegex>^.{5}</preExtractRegex>\n" +
"    <postExtractRegex>.{7}$</postExtractRegex>\n" +
"    <extractRegex>.*</extractRegex>\n" +
"    <columnNameForExtract>stationID</columnNameForExtract>\n" +
"    <sortedColumnSourceName>TIME</sortedColumnSourceName>\n" +
"    <sortFilesBySourceNames>stationID TIME</sortFilesBySourceNames>\n" +
"    <fileTableInMemory>false</fileTableInMemory>\n" +
"    <!-- sourceAttributes>\n" +
"        <att name=\"cdm_data_type\">TimeSeries</att>\n" +
"        <att name=\"cdm_timeseries_variables\">ID, LON, LAT, DEPTH</att>\n" +
"        <att name=\"contributor_name\">NOAA NDBC</att>\n" +
"        <att name=\"contributor_role\">Source of data.</att>\n" +
"        <att name=\"Conventions\">COARDS, CF-1.6, ACDD-1.3</att>\n" +
"        <att name=\"creator_email\">erd.data@noaa.gov</att>\n" +
"        <att name=\"creator_name\">NOAA NMFS SWFSC ERD</att>\n" +
"        <att name=\"creator_type\">institution</att>\n" +
"        <att name=\"creator_url\">https://www.pfeg.noaa.gov</att>\n" +
"        <att name=\"date_created\">2020-11-19</att>\n" +  //changes
"        <att name=\"date_issued\">2020-11-19</att>\n" +   //changes     and see other changes below
"        <att name=\"Easternmost_Easting\" type=\"float\">-79.099</att>\n" +
"        <att name=\"geospatial_lat_max\" type=\"float\">32.501</att>\n" +
"        <att name=\"geospatial_lat_min\" type=\"float\">32.501</att>\n" +
"        <att name=\"geospatial_lat_units\">degrees_north</att>\n" +
"        <att name=\"geospatial_lon_max\" type=\"float\">-79.099</att>\n" +
"        <att name=\"geospatial_lon_min\" type=\"float\">-79.099</att>\n" +
"        <att name=\"geospatial_lon_units\">degrees_east</att>\n" +
"        <att name=\"geospatial_vertical_max\" type=\"float\">0.0</att>\n" +
"        <att name=\"geospatial_vertical_min\" type=\"float\">0.0</att>\n" +
"        <att name=\"geospatial_vertical_positive\">down</att>\n" +
"        <att name=\"geospatial_vertical_units\">m</att>\n" +
"        <att name=\"history\">Around the 25th of each month, erd.data@noaa.gov downloads the latest yearly and monthly historical .txt.gz files from https://www.ndbc.noaa.gov/data/historical/stdmet/ and generates one historical .nc file for each station. erd.data@noaa.gov also downloads all of the 45day near real time .txt files from https://www.ndbc.noaa.gov/data/realtime2/ and generates one near real time .nc file for each station.\n" +
"Every 5 minutes, erd.data@noaa.gov downloads the list of latest data from all stations for the last 2 hours from https://www.ndbc.noaa.gov/data/latest_obs/latest_obs.txt and updates the near real time .nc files.</att>\n" +
"        <att name=\"id\">NDBC_41004_met</att>\n" +
"        <att name=\"infoUrl\">https://www.ndbc.noaa.gov/</att>\n" +
"        <att name=\"institution\">NOAA NDBC, NOAA NMFS SWFSC ERD</att>\n" +
"        <att name=\"keywords\">Earth Science &gt; Atmosphere &gt; Air Quality &gt; Visibility,\n" +
"Earth Science &gt; Atmosphere &gt; Altitude &gt; Planetary Boundary Layer Height,\n" +
"Earth Science &gt; Atmosphere &gt; Atmospheric Pressure &gt; Atmospheric Pressure Measurements,\n" +
"Earth Science &gt; Atmosphere &gt; Atmospheric Pressure &gt; Pressure Tendency,\n" +
"Earth Science &gt; Atmosphere &gt; Atmospheric Pressure &gt; Sea Level Pressure,\n" +
"Earth Science &gt; Atmosphere &gt; Atmospheric Pressure &gt; Static Pressure,\n" +
"Earth Science &gt; Atmosphere &gt; Atmospheric Temperature &gt; Air Temperature,\n" +
"Earth Science &gt; Atmosphere &gt; Atmospheric Temperature &gt; Dew Point Temperature,\n" +
"Earth Science &gt; Atmosphere &gt; Atmospheric Water Vapor &gt; Dew Point Temperature,\n" +
"Earth Science &gt; Atmosphere &gt; Atmospheric Winds &gt; Surface Winds,\n" +
"Earth Science &gt; Oceans &gt; Ocean Temperature &gt; Sea Surface Temperature,\n" +
"Earth Science &gt; Oceans &gt; Ocean Waves &gt; Significant Wave Height,\n" +
"Earth Science &gt; Oceans &gt; Ocean Waves &gt; Swells,\n" +
"Earth Science &gt; Oceans &gt; Ocean Waves &gt; Wave Period,\n" +
"air, air_pressure_at_sea_level, air_temperature, atmosphere, atmospheric, average, boundary, buoy, coastwatch, data, dew point, dew_point_temperature, direction, dominant, eastward, eastward_wind, from, gust, height, identifier, layer, level, measurements, meridional, meteorological, meteorology, name, ndbc, noaa, northward, northward_wind, ocean, oceans, period, planetary, pressure, quality, sea, sea level, sea_surface_swell_wave_period, sea_surface_swell_wave_significant_height, sea_surface_swell_wave_to_direction, sea_surface_temperature, seawater, significant, speed, sst, standard, static, station, surface, surface waves, surface_altitude, swell, swells, temperature, tendency, tendency_of_air_pressure, time, vapor, visibility, visibility_in_air, water, wave, waves, wcn, wind, wind_from_direction, wind_speed, wind_speed_of_gust, winds, zonal</att>\n" +
"        <att name=\"keywords_vocabulary\">GCMD Science Keywords</att>\n" +
"        <att name=\"license\">The data may be used and redistributed for free but is not intended\n" +
"for legal use, since it may contain inaccuracies. Neither the data\n" +
"Contributor, ERD, NOAA, nor the United States Government, nor any\n" +
"of their employees or contractors, makes any warranty, express or\n" +
"implied, including warranties of merchantability and fitness for a\n" +
"particular purpose, or assumes any legal liability for the accuracy,\n" +
"completeness, or usefulness, of this information.</att>\n" +
"        <att name=\"naming_authority\">gov.noaa.pfeg.coastwatch</att>\n" +
"        <att name=\"Northernmost_Northing\" type=\"float\">32.501</att>\n" +
"        <att name=\"project\">NOAA NDBC and NOAA NMFS SWFSC ERD</att>\n" +
"        <att name=\"publisher_email\">erd.data@noaa.gov</att>\n" +
"        <att name=\"publisher_name\">NOAA NMFS SWFSC ERD</att>\n" +
"        <att name=\"publisher_type\">institution</att>\n" +
"        <att name=\"publisher_url\">https://www.pfeg.noaa.gov</att>\n" +
"        <att name=\"quality\">Automated QC checks with periodic manual QC</att>\n" +
"        <att name=\"source\">station observation</att>\n" +
"        <att name=\"sourceUrl\">https://www.ndbc.noaa.gov/</att>\n" +
"        <att name=\"Southernmost_Northing\" type=\"float\">32.501</att>\n" +
"        <att name=\"standard_name_vocabulary\">CF Standard Name Table v70</att>\n" +
"        <att name=\"subsetVariables\">ID, LON, LAT, DEPTH</att>\n" +
"        <att name=\"summary\">The National Data Buoy Center (NDBC) distributes meteorological data from\n" +
"moored buoys maintained by NDBC and others. Moored buoys are the weather\n" +
"sentinels of the sea. They are deployed in the coastal and offshore waters\n" +
"from the western Atlantic to the Pacific Ocean around Hawaii, and from the\n" +
"Bering Sea to the South Pacific. NDBC&#39;s moored buoys measure and transmit\n" +
"barometric pressure; wind direction, speed, and gust; air and sea\n" +
"temperature; and wave energy spectra from which significant wave height,\n" +
"dominant wave period, and average wave period are derived. Even the\n" +
"direction of wave propagation is measured on many moored buoys. See\n" +
"https://www.ndbc.noaa.gov/measdes.shtml for a description of the measurements.\n" +
"\n" +
"The source data from NOAA NDBC has different column names, different units,\n" +
"and different missing values in different files, and other problems (notably,\n" +
"lots of rows with duplicate or different values for the same time point).\n" +
"This dataset is a standardized, reformatted, and lightly edited version of\n" +
"that source data, created by NOAA NMFS SWFSC ERD (email: erd.data at noaa.gov).\n" +
"Before 2020-01-29, this dataset only had the data that was closest to a given\n" +
"hour, rounded to the nearest hour. Now, this dataset has all of the data\n" +
"available from NDBC with the original time values. If there are multiple\n" +
"source rows for a given buoy for a given time, only the row with the most\n" +
"non-NaN data values is kept. If there is a gap in the data, a row of missing\n" +
"values is inserted (which causes a nice gap when the data is graphed). Also,\n" +
"some impossible data values are removed, but this data is not perfectly clean.\n" +
"This dataset is now updated every 5 minutes.\n" +
"\n" +
"This dataset has both historical data (quality controlled) and near real time\n" +
"data (less quality controlled).</att>\n" +
"        <att name=\"testOutOfDate\">now-25minutes</att>\n" +
"        <att name=\"time_coverage_end\">2020-11-19T15:00:00Z</att>\n" + //changes. Don't regex it -- I want to see it change.
"        <att name=\"time_coverage_start\">2020-11-01T00:00:00Z</att>\n" +  //changes since it is from an nrt file
"        <att name=\"title\">NDBC Standard Meteorological Buoy Data, 1970-present</att>\n" +
"        <att name=\"Westernmost_Easting\" type=\"float\">-79.099</att>\n" +
"    </sourceAttributes -->\n" +
cdmSuggestion() +
"    <addAttributes>\n" +
"        <att name=\"keywords\">1970-present, air, air_pressure_at_sea_level, air_temperature, altitude, APD, atmosphere, atmospheric, ATMP, average, BAR, boundary, buoy, center, coast, coastwatch, control, data, depth, dew, dew point, dew_point_temperature, DEWP, dewpoint, direction, dominant, DPD, earth, Earth Science &gt; Atmosphere &gt; Air Quality &gt; Visibility, Earth Science &gt; Atmosphere &gt; Altitude &gt; Planetary Boundary Layer Height, Earth Science &gt; Atmosphere &gt; Atmospheric Pressure &gt; Atmospheric Pressure Measurements, Earth Science &gt; Atmosphere &gt; Atmospheric Pressure &gt; Pressure Tendency, Earth Science &gt; Atmosphere &gt; Atmospheric Pressure &gt; Sea Level Pressure, Earth Science &gt; Atmosphere &gt; Atmospheric Pressure &gt; Static Pressure, Earth Science &gt; Atmosphere &gt; Atmospheric Temperature &gt; Air Temperature, Earth Science &gt; Atmosphere &gt; Atmospheric Temperature &gt; Dew Point Temperature, Earth Science &gt; Atmosphere &gt; Atmospheric Temperature &gt; Surface Air Temperature, Earth Science &gt; Atmosphere &gt; Atmospheric Water Vapor &gt; Dew Point Temperature, Earth Science &gt; Atmosphere &gt; Atmospheric Winds &gt; Surface Winds, Earth Science &gt; Oceans &gt; Ocean Temperature &gt; Sea Surface Temperature, Earth Science &gt; Oceans &gt; Ocean Waves &gt; Significant Wave Height, Earth Science &gt; Oceans &gt; Ocean Waves &gt; Swells, Earth Science &gt; Oceans &gt; Ocean Waves &gt; Wave Period, Earth Science &gt; Oceans &gt; Ocean Waves &gt; Wave Speed/Direction, eastward, eastward_wind, erd, fisheries, GST, gust, height, identifier, latitude, layer, level, longitude, marine, measurements, meridional, meteorological, meteorology, MWD, name, national, ndbc, near, nmfs, noaa, node, northward, northward_wind, nrt, ocean, oceans, period, planetary, point, present, pressure, PTDY, quality, real, science, sea, sea level, sea_surface_swell_wave_period, sea_surface_swell_wave_significant_height, sea_surface_swell_wave_to_direction, sea_surface_temperature, sea_surface_wave_significant_height, sea_surface_wave_to_direction, seawater, service, significant, southwest, speed, sst, standard, static, station, surface, surface waves, surface_altitude, swell, swells, swfsc, swh, temperature, tendency, tendency_of_air_pressure, TIDE, time, vapor, VIS, visibility, visibility_in_air, water, wave, waves, wcn, west, wind, wind_from_direction, wind_speed, wind_speed_of_gust, winds, WSPD, WSPU, WSPV, WTMP, WVHT, zonal</att>\n" +
"        <att name=\"sourceUrl\">(local files)</att>\n" +
"        <att name=\"summary\">The National Data Buoy Center (NDBC) distributes meteorological data from\n" +
"moored buoys maintained by NDBC and others. Moored buoys are the weather\n" +
"sentinels of the sea. They are deployed in the coastal and offshore waters\n" +
"from the western Atlantic to the Pacific Ocean around Hawaii, and from the\n" +
"Bering Sea to the South Pacific. NDBC&#39;s moored buoys measure and transmit\n" +
"barometric pressure; wind direction, speed, and gust; air and sea\n" +
"temperature; and wave energy spectra from which significant wave height,\n" +
"dominant wave period, and average wave period are derived. Even the\n" +
"direction of wave propagation is measured on many moored buoys. See\n" +
"https://www.ndbc.noaa.gov/measdes.shtml for a description of the measurements.\n" +
"\n" +
"The source data from NOAA NDBC has different column names, different units,\n" +
"and different missing values in different files, and other problems (notably,\n" +
"lots of rows with duplicate or different values for the same time point).\n" +
"This dataset is a standardized, reformatted, and lightly edited version of\n" +
"that source data, created by NOAA National Marine Fisheries Service (NMFS) Southwest Fisheries Science Center (SWFSC) ERD (email: erd.data at noaa.gov).\n" +
"Before 2020-01-29, this dataset only had the data that was closest to a given\n" +
"hour, rounded to the nearest hour. Now, this dataset has all of the data\n" +
"available from NDBC with the original time values. If there are multiple\n" +
"source rows for a given buoy for a given time, only the row with the most\n" +
"non-NaN data values is kept. If there is a gap in the data, a row of missing\n" +
"values is inserted (which causes a nice gap when the data is graphed). Also,\n" +
"some impossible data values are removed, but this data is not perfectly clean.\n" +
"This dataset is now updated every 5 minutes.\n" +
"\n" +
"This dataset has both historical data (quality controlled) and near real time\n" +
"data (less quality controlled).</att>\n" +
"    </addAttributes>\n" +
"    <dataVariable>\n" +
"        <sourceName>stationID</sourceName>\n" +
"        <destinationName>stationID</destinationName>\n" +
"        <dataType>String</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"            <att name=\"ioos_category\">Identifier</att>\n" +
"            <att name=\"long_name\">Station ID</att>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>TIME</sourceName>\n" +
"        <destinationName>time</destinationName>\n" +
"        <dataType>double</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_CoordinateAxisType\">Time</att>\n" +
"            <att name=\"actual_range\" type=\"doubleList\">1.6041888E9 1.605798E9</att>\n" + //both change
"            <att name=\"axis\">T</att>\n" +
"            <att name=\"ioos_category\">Time</att>\n" +
"            <att name=\"long_name\">Time</att>\n" +
"            <att name=\"standard_name\">time</att>\n" +
"            <att name=\"time_origin\">01-JAN-1970 00:00:00</att>\n" +
"            <att name=\"units\">seconds since 1970-01-01T00:00:00Z</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"            <att name=\"colorBarMaximum\" type=\"double\">1.606E9</att>\n" +  //changes
"            <att name=\"colorBarMinimum\" type=\"double\">1.604E9</att>\n" + //changes
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>DEPTH</sourceName>\n" +
"        <destinationName>depth</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_CoordinateAxisType\">Height</att>\n" +
"            <att name=\"_CoordinateZisPositive\">down</att>\n" +
"            <att name=\"actual_range\" type=\"floatList\">... ...</att>\n" +
"            <att name=\"axis\">Z</att>\n" +
"            <att name=\"colorBarMaximum\" type=\"double\">0.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
"            <att name=\"comment\">The depth of the station, nominally 0 (see station information for details).</att>\n" +
"            <att name=\"ioos_category\">Location</att>\n" +
"            <att name=\"long_name\">Depth</att>\n" +
"            <att name=\"positive\">down</att>\n" +
"            <att name=\"standard_name\">depth</att>\n" +
"            <att name=\"units\">m</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">-0.01</att>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>LAT</sourceName>\n" +
"        <destinationName>latitude</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_CoordinateAxisType\">Lat</att>\n" +
"            <att name=\"actual_range\" type=\"floatList\">... ...</att>\n" +
"            <att name=\"axis\">Y</att>\n" +
"            <att name=\"colorBarMaximum\" type=\"double\">90.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">-90.0</att>\n" +
"            <att name=\"comment\">The latitude of the station.</att>\n" +
"            <att name=\"ioos_category\">Location</att>\n" +
"            <att name=\"long_name\">Latitude</att>\n" +
"            <att name=\"standard_name\">latitude</att>\n" +
"            <att name=\"units\">degrees_north</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>LON</sourceName>\n" +
"        <destinationName>longitude</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_CoordinateAxisType\">Lon</att>\n" +
"            <att name=\"actual_range\" type=\"floatList\">... ...</att>\n" +
"            <att name=\"axis\">X</att>\n" +
"            <att name=\"colorBarMaximum\" type=\"double\">180.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">-180.0</att>\n" +
"            <att name=\"comment\">The longitude of the station.</att>\n" +
"            <att name=\"ioos_category\">Location</att>\n" +
"            <att name=\"long_name\">Longitude</att>\n" +
"            <att name=\"standard_name\">longitude</att>\n" +
"            <att name=\"units\">degrees_east</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>WD</sourceName>\n" +
"        <destinationName>WD</destinationName>\n" +
"        <dataType>short</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_FillValue\" type=\"short\">32767</att>\n" +
"            <att name=\"actual_range\" type=\"shortList\">... ...</att>\n" +
"            <att name=\"colorBarMaximum\" type=\"double\">360.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
"            <att name=\"comment\">Wind direction (the direction the wind is coming from in degrees clockwise from true N) during the same period used for WSPD.</att>\n" +
"            <att name=\"ioos_category\">Wind</att>\n" +
"            <att name=\"long_name\">Wind Direction</att>\n" +
"            <att name=\"missing_value\" type=\"short\">32767</att>\n" +
"            <att name=\"standard_name\">wind_from_direction</att>\n" +
"            <att name=\"units\">degrees_true</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>WSPD</sourceName>\n" +
"        <destinationName>WSPD</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_FillValue\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"actual_range\" type=\"floatList\">... ...</att>\n" + //changes
"            <att name=\"colorBarMaximum\" type=\"double\">15.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
"            <att name=\"comment\">Average wind speed (m/s).</att>\n" +
"            <att name=\"ioos_category\">Wind</att>\n" +
"            <att name=\"long_name\">Wind Speed</att>\n" +
"            <att name=\"missing_value\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"standard_name\">wind_speed</att>\n" +
"            <att name=\"units\">m s-1</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>GST</sourceName>\n" +
"        <destinationName>GST</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_FillValue\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"actual_range\" type=\"floatList\">... ...</att>\n" + //changes
"            <att name=\"colorBarMaximum\" type=\"double\">30.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
"            <att name=\"comment\">Peak 5 or 8 second gust speed (m/s).</att>\n" +
"            <att name=\"ioos_category\">Wind</att>\n" +
"            <att name=\"long_name\">Wind Gust Speed</att>\n" +
"            <att name=\"missing_value\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"standard_name\">wind_speed_of_gust</att>\n" +
"            <att name=\"units\">m s-1</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>WVHT</sourceName>\n" +
"        <destinationName>WVHT</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_FillValue\" type=\"float\">-9999999.0</att>\n" +
//"            <att name=\"actual_range\" type=\"floatList\">... ...</att>\n" + //changes
"            <att name=\"colorBarMaximum\" type=\"double\">10.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
"            <att name=\"comment\">Significant wave height (meters) is calculated as the average of the highest one-third of all of the wave heights during the 20-minute sampling period.</att>\n" +
"            <att name=\"ioos_category\">Surface Waves</att>\n" +
"            <att name=\"long_name\">Wave Height</att>\n" +
"            <att name=\"missing_value\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"standard_name\">sea_surface_wave_significant_height</att>\n" +
"            <att name=\"units\">m</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>DPD</sourceName>\n" +
"        <destinationName>DPD</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_FillValue\" type=\"float\">-9999999.0</att>\n" +
//"            <att name=\"actual_range\" type=\"floatList\">... ...</att>\n" + //changes
"            <att name=\"colorBarMaximum\" type=\"double\">20.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
"            <att name=\"comment\">Dominant wave period (seconds) is the period with the maximum wave energy.</att>\n" +
"            <att name=\"ioos_category\">Surface Waves</att>\n" +
"            <att name=\"long_name\">Wave Period, Dominant</att>\n" +
"            <att name=\"missing_value\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"standard_name\">sea_surface_swell_wave_period</att>\n" +
"            <att name=\"units\">s</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>APD</sourceName>\n" +
"        <destinationName>APD</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_FillValue\" type=\"float\">-9999999.0</att>\n" +
//"            <att name=\"actual_range\" type=\"floatList\">... ...</att>\n" + //changes
"            <att name=\"colorBarMaximum\" type=\"double\">20.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
"            <att name=\"comment\">Average wave period (seconds) of all waves during the 20-minute period.</att>\n" +
"            <att name=\"ioos_category\">Surface Waves</att>\n" +
"            <att name=\"long_name\">Wave Period, Average</att>\n" +
"            <att name=\"missing_value\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"standard_name\">sea_surface_swell_wave_period</att>\n" +
"            <att name=\"units\">s</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>MWD</sourceName>\n" +
"        <destinationName>MWD</destinationName>\n" +
"        <dataType>short</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_FillValue\" type=\"short\">32767</att>\n" +
"            <att name=\"actual_range\" type=\"shortList\">... ...</att>\n" + //changes
"            <att name=\"colorBarMaximum\" type=\"double\">360.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
"            <att name=\"comment\">Mean wave direction corresponding to energy of the dominant period (DOMPD).</att>\n" +
"            <att name=\"ioos_category\">Surface Waves</att>\n" +
"            <att name=\"long_name\">Wave Direction</att>\n" +
"            <att name=\"missing_value\" type=\"short\">32767</att>\n" +
"            <att name=\"standard_name\">sea_surface_wave_to_direction</att>\n" +
"            <att name=\"units\">degrees_true</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>BAR</sourceName>\n" +
"        <destinationName>BAR</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_FillValue\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"actual_range\" type=\"floatList\">... ...</att>\n" + //changes
"            <att name=\"colorBarMaximum\" type=\"double\">1050.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">950.0</att>\n" +
"            <att name=\"comment\">Air pressure (hPa). (&#39;PRES&#39; on some NDBC tables.) For C-MAN sites and Great Lakes buoys, the recorded pressure is reduced to sea level using the method described in NWS Technical Procedures Bulletin 291 (11/14/80).</att>\n" +
"            <att name=\"ioos_category\">Pressure</att>\n" +
"            <att name=\"long_name\">Air Pressure</att>\n" +
"            <att name=\"missing_value\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"standard_name\">air_pressure_at_sea_level</att>\n" +
"            <att name=\"units\">hPa</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>ATMP</sourceName>\n" +
"        <destinationName>ATMP</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_FillValue\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"actual_range\" type=\"floatList\">... ...</att>\n" + //changes
"            <att name=\"colorBarMaximum\" type=\"double\">40.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">-10.0</att>\n" +
"            <att name=\"comment\">Air temperature (Celsius). For sensor heights on buoys, see Hull Descriptions. For sensor heights at C-MAN stations, see C-MAN Sensor Locations.</att>\n" +
"            <att name=\"ioos_category\">Temperature</att>\n" +
"            <att name=\"long_name\">Air Temperature</att>\n" +
"            <att name=\"missing_value\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"standard_name\">air_temperature</att>\n" +
"            <att name=\"units\">degree_C</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>WTMP</sourceName>\n" +
"        <destinationName>WTMP</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_FillValue\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"actual_range\" type=\"floatList\">... ...</att>\n" + //changes
"            <att name=\"colorBarMaximum\" type=\"double\">32.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
"            <att name=\"comment\">Sea surface temperature (Celsius). For sensor depth, see Hull Description.</att>\n" +
"            <att name=\"ioos_category\">Temperature</att>\n" +
"            <att name=\"long_name\">SST</att>\n" +
"            <att name=\"missing_value\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"standard_name\">sea_surface_temperature</att>\n" +
"            <att name=\"units\">degree_C</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>DEWP</sourceName>\n" +
"        <destinationName>DEWP</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_FillValue\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"actual_range\" type=\"floatList\">... ...</att>\n" + //changes
"            <att name=\"colorBarMaximum\" type=\"double\">40.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
"            <att name=\"comment\">Dewpoint temperature taken at the same height as the air temperature measurement.</att>\n" +
"            <att name=\"ioos_category\">Temperature</att>\n" +
"            <att name=\"long_name\">Dewpoint Temperature</att>\n" +
"            <att name=\"missing_value\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"standard_name\">dew_point_temperature</att>\n" +
"            <att name=\"units\">degree_C</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>VIS</sourceName>\n" +
"        <destinationName>VIS</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_FillValue\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"colorBarMaximum\" type=\"double\">100.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
"            <att name=\"comment\">Station visibility (km, originally nautical miles in the NDBC .txt files). Note that buoy stations are limited to reports from 0 to 1.6 nmi.</att>\n" +
"            <att name=\"ioos_category\">Meteorology</att>\n" +
"            <att name=\"long_name\">Station Visibility</att>\n" +
"            <att name=\"missing_value\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"standard_name\">visibility_in_air</att>\n" +
"            <att name=\"units\">km</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>PTDY</sourceName>\n" +
"        <destinationName>PTDY</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_FillValue\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"actual_range\" type=\"floatList\">... ...</att>\n" + //changes
"            <att name=\"colorBarMaximum\" type=\"double\">3.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">-3.0</att>\n" +
"            <att name=\"comment\">Pressure Tendency is the direction (plus or minus) and the amount of pressure change (hPa) for a three hour period ending at the time of observation.</att>\n" +
"            <att name=\"ioos_category\">Pressure</att>\n" +
"            <att name=\"long_name\">Pressure Tendency</att>\n" +
"            <att name=\"missing_value\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"standard_name\">tendency_of_air_pressure</att>\n" +
"            <att name=\"units\">hPa</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>TIDE</sourceName>\n" +
"        <destinationName>TIDE</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_FillValue\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"colorBarMaximum\" type=\"double\">5.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">-5.0</att>\n" +
"            <att name=\"comment\">The water level in meters (originally feet in the NDBC .txt files) above or below Mean Lower Low Water (MLLW).</att>\n" +
"            <att name=\"ioos_category\">Sea Level</att>\n" +
"            <att name=\"long_name\">Water Level</att>\n" +
"            <att name=\"missing_value\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"standard_name\">surface_altitude</att>\n" +
"            <att name=\"units\">m</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>WSPU</sourceName>\n" +
"        <destinationName>WSPU</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_FillValue\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"actual_range\" type=\"floatList\">... ...</att>\n" +
"            <att name=\"colorBarMaximum\" type=\"double\">15.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">-15.0</att>\n" +
"            <att name=\"comment\">The zonal wind speed (m/s) indicates the u component of where the wind is going, derived from Wind Direction and Wind Speed.</att>\n" +
"            <att name=\"ioos_category\">Wind</att>\n" +
"            <att name=\"long_name\">Wind Speed, Zonal</att>\n" +
"            <att name=\"missing_value\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"standard_name\">eastward_wind</att>\n" +
"            <att name=\"units\">m s-1</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>WSPV</sourceName>\n" +
"        <destinationName>WSPV</destinationName>\n" +
"        <dataType>float</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"_FillValue\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"actual_range\" type=\"floatList\">... ...</att>\n" +
"            <att name=\"colorBarMaximum\" type=\"double\">15.0</att>\n" +
"            <att name=\"colorBarMinimum\" type=\"double\">-15.0</att>\n" +
"            <att name=\"comment\">The meridional wind speed (m/s) indicates the v component of where the wind is going, derived from Wind Direction and Wind Speed.</att>\n" +
"            <att name=\"ioos_category\">Wind</att>\n" +
"            <att name=\"long_name\">Wind Speed, Meridional</att>\n" +
"            <att name=\"missing_value\" type=\"float\">-9999999.0</att>\n" +
"            <att name=\"standard_name\">northward_wind</att>\n" +
"            <att name=\"units\">m s-1</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"    <dataVariable>\n" +
"        <sourceName>ID</sourceName>\n" +
"        <destinationName>ID</destinationName>\n" +
"        <dataType>String</dataType>\n" +
"        <!-- sourceAttributes>\n" +
"            <att name=\"cf_role\">timeseries_id</att>\n" +
"            <att name=\"comment\">The station identifier.</att>\n" +
"            <att name=\"ioos_category\">Identifier</att>\n" +
"            <att name=\"long_name\">Station Identifier</att>\n" +
"        </sourceAttributes -->\n" +
"        <addAttributes>\n" +
"        </addAttributes>\n" +
"    </dataVariable>\n" +
"</dataset>\n" +
"\n\n";

            Test.ensureEqual(results, expected, "results=\n" + results);
            //Test.ensureEqual(results.substring(0, Math.min(results.length(), expected.length())), 
            //    expected, "");

            //2 changes to make it a valid dataset:
            String ts;
            ts = "<att name=\"cdm_timeseries_variables\">ID, LON, LAT, DEPTH</att>"; 
            int po = results.indexOf(ts);
            Test.ensureEqual(results.substring(po, po + ts.length()), ts, results);
            ts = "<att name=\"subsetVariables\">ID, LON, LAT, DEPTH</att>"; 
            po = results.indexOf(ts);
            Test.ensureEqual(results.substring(po, po + ts.length()), ts, results);
            //but they are in sourceAtts. I need to add them to addAtts:
//!!! This should be done by code that catches latitude, longitude
            results = String2.replaceAll(results, 
                 "<att name=\"sourceUrl\">(local files)</att>",
                 "<att name=\"sourceUrl\">(local files)</att>\n" +
         "        <att name=\"cdm_timeseries_variables\">ID, latitude, longitude, depth</att>\n" + 
         "        <att name=\"subsetVariables\">ID, latitude, longitude, depth</att>"); 

            ts = "<att name=\"long_name\">Station Identifier</att>";
            Test.ensureTrue(results.indexOf(ts) > 0, results);
            results = String2.replaceAll(results, ts,
                 "<att name=\"long_name\">Station Identifier</att>\n" +
    "             <att name=\"cf_role\">timeseries_id</att>");

            String2.log("results=\n" + results);
            String tDatasetID = "ndbcMet2_73a7_3fce_afec";
//            EDD.deleteCachedDatasetInfo(tDatasetID);
            EDD edd = oneFromXmlFragment(null, results);
            Test.ensureEqual(edd.datasetID(), tDatasetID, "");
            Test.ensureEqual(edd.title(), "NDBC Standard Meteorological Buoy Data, 1970-present", "");
            Test.ensureEqual(String2.toCSSVString(edd.dataVariableDestinationNames()), 
                "stationID, time, depth, latitude, longitude, WD, WSPD, GST, WVHT, " +
                "DPD, APD, MWD, BAR, ATMP, WTMP, DEWP, VIS, PTDY, TIDE, WSPU, WSPV, ID", 
                "");

        } catch (Throwable t) {
            String2.pressEnterToContinue(MustBe.throwableToString(t) + 
                "\nError using generateDatasetsXml."); 
        }

    }

    /**
     * This tests the methods in this class with a file with a nested sequence.
     *
     * @throws Throwable if trouble
     */
    public static void testNested() throws Throwable {
        String2.log("\n****************** EDDTableFromNcSequenceFiles.testNested() *****************\n");
        testVerboseOn();
        String name, tName, results, tResults, expected, userDapQuery, tQuery;
        String error = "";
        EDV edv;
        String dir = EDStatic.fullTestCacheDirectory;
        String today = Calendar2.getCurrentISODateTimeStringZulu().substring(0, 14); //14 is enough to check hour. Hard to check min:sec.

        String id = "testTableNcSequence";
        if (deleteCachedDatasetInfo)
            deleteCachedDatasetInfo(id);

        EDDTable eddTable = (EDDTable)oneFromDatasetsXml(null, id); 

        //*** test getting das for entire dataset
        String2.log("\n****************** EDDTableFromNcSequenceFiles.testNested\n");
        tName = eddTable.makeNewFileForDapQuery(null, null, "", dir, 
            eddTable.className() + "_Entire", ".das"); 
        results = String2.directReadFrom88591File(dir + tName);
        //String2.log(results);
        expected = 
"Attributes {\n" +
" s {\n" +
"  id {\n" +
"    String cf_role \"timeseries_id\";\n" +
"    String ioos_category \"Identifier\";\n" +
"    String long_name \"Station Identifier\";\n" +
"  }\n" +
"  longitude {\n" +
"    String _CoordinateAxisType \"Lon\";\n" +
"    Float64 actual_range -120.4, -118.4;\n" +
"    String axis \"X\";\n" +
"    Float64 colorBarMaximum -118.4;\n" +
"    Float64 colorBarMinimum -120.4;\n" +
"    String ioos_category \"Location\";\n" +
"    String long_name \"Longitude\";\n" +
"    String standard_name \"longitude\";\n" +
"    String units \"degrees_east\";\n" +
"  }\n" +
"  latitude {\n" +
"    String _CoordinateAxisType \"Lat\";\n" +
"    Float64 actual_range 32.8, 34.05;\n" +
"    String axis \"Y\";\n" +
"    Float64 colorBarMaximum 34.5;\n" +
"    Float64 colorBarMinimum 32.5;\n" +
"    String ioos_category \"Location\";\n" +
"    String long_name \"Latitude\";\n" +
"    String standard_name \"latitude\";\n" +
"    String units \"degrees_north\";\n" +
"  }\n" +
"  depth {\n" +
"    String _CoordinateAxisType \"Height\";\n" +
"    String _CoordinateZisPositive \"down\";\n" +
"    Float64 actual_range 5.0, 17.0;\n" +
"    String axis \"Z\";\n" +
"    Float64 colorBarMaximum 20.0;\n" +
"    Float64 colorBarMinimum 0.0;\n" +
"    String ioos_category \"Location\";\n" +
"    String long_name \"Depth\";\n" +
"    String positive \"down\";\n" +
"    String standard_name \"depth\";\n" +
"    String units \"m\";\n" +
"  }\n" +
"  time {\n" +
"    String _CoordinateAxisType \"Time\";\n" +
"    Float64 actual_range 4.89024e+8, 1.183248e+9;\n" +
"    String axis \"T\";\n" +
"    Float64 colorBarMaximum 1.183248e+9;\n" +
"    Float64 colorBarMinimum 4.89024e+8;\n" +
"    String ioos_category \"Time\";\n" +
"    String long_name \"Time\";\n" +
"    String standard_name \"time\";\n" +
"    String time_origin \"01-JAN-1970 00:00:00\";\n" +
"    String units \"seconds since 1970-01-01T00:00:00Z\";\n" +
"  }\n" +
"  common_name {\n" +
"    String ioos_category \"Taxonomy\";\n" +
"    String long_name \"Common Name\";\n" +
"  }\n" +
"  species_name {\n" +
"    String ioos_category \"Taxonomy\";\n" +
"    String long_name \"Species Name\";\n" +
"  }\n" +
"  size {\n" +
"    Int16 _FillValue 32767;\n" +
"    Int16 actual_range 1, 385;\n" +
"    String ioos_category \"Biology\";\n" +
"    String long_name \"Size\";\n" +
"    String units \"mm\";\n" +
"  }\n" +
" }\n" +
"  NC_GLOBAL {\n" +
"    String acknowledgement \"NOAA NESDIS COASTWATCH, NOAA SWFSC ERD, Channel Islands National Park, National Park Service\";\n" +
"    String cdm_data_type \"TimeSeries\";\n" +
"    String cdm_timeseries_variables \"id, longitude, latitude\";\n" +
"    String contributor_email \"David_Kushner@nps.gov\";\n" +
"    String contributor_name \"Channel Islands National Park, National Park Service\";\n" +
"    String contributor_role \"Source of data.\";\n" +
"    String Conventions \"COARDS, CF-1.6, ACDD-1.3\";\n" +
"    String creator_email \"erd.data@noaa.gov\";\n" +
"    String creator_name \"NOAA NMFS SWFSC ERD\";\n" +
"    String creator_type \"institution\";\n" +
"    String creator_url \"https://www.pfeg.noaa.gov\";\n" +
"    String date_created \"2008-06-11T21:43:28Z\";\n" +
"    String date_issued \"2008-06-11T21:43:28Z\";\n" +
"    Float64 Easternmost_Easting -118.4;\n" +
"    String featureType \"TimeSeries\";\n" +
"    Float64 geospatial_lat_max 34.05;\n" +
"    Float64 geospatial_lat_min 32.8;\n" +
"    String geospatial_lat_units \"degrees_north\";\n" +
"    Float64 geospatial_lon_max -118.4;\n" +
"    Float64 geospatial_lon_min -120.4;\n" +
"    String geospatial_lon_units \"degrees_east\";\n" +
"    Float64 geospatial_vertical_max 17.0;\n" +
"    Float64 geospatial_vertical_min 5.0;\n" +
"    String geospatial_vertical_positive \"down\";\n" +
"    String geospatial_vertical_units \"m\";\n" +
"    String history \"Channel Islands National Park, National Park Service\n" +
"2008-06-11T21:43:28Z NOAA CoastWatch (West Coast Node) and NOAA SFSC ERD\n" + //will be SWFSC when reprocessed
today;
        tResults = results.substring(0, Math.min(results.length(), expected.length()));
        Test.ensureEqual(tResults, expected, "\nresults=\n" + results);

//+ " (local files)\n" +
//today + " " + EDStatic.erddapUrl + //in tests, always use non-https url
expected =
"/tabledap/erdCinpKfmSFNH.das\";\n" +
"    String infoUrl \"https://www.nps.gov/chis/naturescience/index.htm\";\n" +
"    String institution \"CINP\";\n" +
"    String keywords \"aquatic, atmosphere, biology, biosphere, channel, cinp, coastal, common, depth, Earth Science > Biosphere > Aquatic Ecosystems > Coastal Habitat, Earth Science > Biosphere > Aquatic Ecosystems > Marine Habitat, ecosystems, forest, frequency, habitat, height, identifier, islands, kelp, marine, monitoring, name, natural, size, species, station, taxonomy, time\";\n" +
"    String keywords_vocabulary \"GCMD Science Keywords\";\n" +
"    String license \"The data may be used and redistributed for free but is not intended for legal use, since it may contain inaccuracies. Neither the data Contributor, CoastWatch, NOAA, nor the United States Government, nor any of their employees or contractors, makes any warranty, express or implied, including warranties of merchantability and fitness for a particular purpose, or assumes any legal liability for the accuracy, completeness, or usefulness, of this information.  National Park Service Disclaimer: The National Park Service shall not be held liable for improper or incorrect use of the data described and/or contained herein. These data and related graphics are not legal documents and are not intended to be used as such. The information contained in these data is dynamic and may change over time. The data are not better than the original sources from which they were derived. It is the responsibility of the data user to use the data appropriately and consistent within the limitation of geospatial data in general and these data in particular. The related graphics are intended to aid the data user in acquiring relevant data; it is not appropriate to use the related graphics as data. The National Park Service gives no warranty, expressed or implied, as to the accuracy, reliability, or completeness of these data. It is strongly recommended that these data are directly acquired from an NPS server and not indirectly through other sources which may have changed the data in some way. Although these data have been processed successfully on computer systems at the National Park Service, no warranty expressed or implied is made regarding the utility of the data on other systems for general or scientific purposes, nor shall the act of distribution constitute any such warranty. This disclaimer applies both to individual use of the data and aggregate use with other data.\";\n" +
"    String naming_authority \"gov.noaa.pfeg.coastwatch\";\n" +
"    Float64 Northernmost_Northing 34.05;\n" +
"    String observationDimension \"row\";\n" + //2012-07-27 this should disappear soon
"    String project \"NOAA NMFS SWFSC ERD (https://www.pfeg.noaa.gov/)\";\n" +
"    String references \"Channel Islands National Parks Inventory and Monitoring information: http://nature.nps.gov/im/units/medn . Kelp Forest Monitoring Protocols: http://www.nature.nps.gov/im/units/chis/Reports_PDF/Marine/KFM-HandbookVol1.pdf .\";\n" +
"    String sourceUrl \"(local files)\";\n" +
"    Float64 Southernmost_Northing 32.8;\n" +
"    String standard_name_vocabulary \"CF Standard Name Table v70\";\n" + 
"    String subsetVariables \"id, longitude, latitude, common_name, species_name\";\n" +
"    String summary \"This dataset has measurements of the size of selected animal species at selected locations in the Channel Islands National Park. Sampling is conducted annually between the months of May-October, so the Time data in this file is July 1 of each year (a nominal value). The size frequency measurements were taken within 10 meters of the transect line at each site.  Depths at the site vary some, but we describe the depth of the site along the transect line where that station's temperature logger is located, a typical depth for the site.\";\n" +
"    String time_coverage_end \"2007-07-01T00:00:00Z\";\n" +
"    String time_coverage_start \"1985-07-01T00:00:00Z\";\n" +
"    String title \"Channel Islands, Kelp Forest Monitoring, Size and Frequency, Natural Habitat, 1985-2007\";\n" +
"    Float64 Westernmost_Easting -120.4;\n" +
"  }\n" +
"}\n";
        int tPo = results.indexOf(expected.substring(0, 17));
        Test.ensureTrue(tPo >= 0, "tPo=-1 results=\n" + results);
        Test.ensureEqual(
            results.substring(tPo, Math.min(results.length(), tPo + expected.length())),
            expected, "results=\n" + results);
        
        //*** test getting dds for entire dataset
        tName = eddTable.makeNewFileForDapQuery(null, null, "", dir, 
            eddTable.className() + "_Entire", ".dds"); 
        results = String2.directReadFrom88591File(dir + tName);
        //String2.log(results);
        expected = 
"Dataset {\n" +
"  Sequence {\n" +
"    String id;\n" +
"    Float64 longitude;\n" +
"    Float64 latitude;\n" +
"    Float64 depth;\n" +
"    Float64 time;\n" +
"    String common_name;\n" +
"    String species_name;\n" +
"    Int16 size;\n" +
"  } s;\n" +
"} s;\n";
        Test.ensureEqual(results, expected, "\nresults=\n" + results);


        //*** test make data files
        String2.log("\n****************** EDDTableFromNcSequenceFiles.test 1D make DATA FILES\n");       

        //.csv    for one lat,lon,time
        userDapQuery = "" +
            "&longitude=-119.05&latitude=33.46666666666&time=2005-07-01T00:00:00";
        tName = eddTable.makeNewFileForDapQuery(null, null, userDapQuery, dir, 
            eddTable.className() + "_1Station", ".csv"); 
        results = String2.directReadFrom88591File(dir + tName);
        //String2.log(results);
        expected = 
"id,longitude,latitude,depth,time,common_name,species_name,size\n" +
",degrees_east,degrees_north,m,UTC,,,mm\n" +
"Santa Barbara (Webster's Arch),-119.05,33.4666666666667,14.0,2005-07-01T00:00:00Z,Bat star,Asterina miniata,57\n" +
"Santa Barbara (Webster's Arch),-119.05,33.4666666666667,14.0,2005-07-01T00:00:00Z,Bat star,Asterina miniata,41\n" +
"Santa Barbara (Webster's Arch),-119.05,33.4666666666667,14.0,2005-07-01T00:00:00Z,Bat star,Asterina miniata,55\n";
        Test.ensureEqual(results.substring(0, expected.length()), expected, "\nresults=\n" + results);
        expected = //last 3 lines
"Santa Barbara (Webster's Arch),-119.05,33.4666666666667,14.0,2005-07-01T00:00:00Z,Purple sea urchin,Strongylocentrotus purpuratus,15\n" +
"Santa Barbara (Webster's Arch),-119.05,33.4666666666667,14.0,2005-07-01T00:00:00Z,Purple sea urchin,Strongylocentrotus purpuratus,23\n" +
"Santa Barbara (Webster's Arch),-119.05,33.4666666666667,14.0,2005-07-01T00:00:00Z,Purple sea urchin,Strongylocentrotus purpuratus,19\n";
        Test.ensureEqual(results.substring(results.length() - expected.length()), expected, "\nresults=\n" + results);


        //.csv    for one lat,lon,time      via lon > <
        userDapQuery = "" +
            "&longitude>-119.06&longitude<=-119.04&latitude=33.46666666666&time=2005-07-01T00:00:00";
        tName = eddTable.makeNewFileForDapQuery(null, null, userDapQuery, dir, 
            eddTable.className() + "_1StationGTLT", ".csv"); 
        results = String2.directReadFrom88591File(dir + tName);
        //String2.log(results);
        expected = 
"id,longitude,latitude,depth,time,common_name,species_name,size\n" +
",degrees_east,degrees_north,m,UTC,,,mm\n" +
"Santa Barbara (Webster's Arch),-119.05,33.4666666666667,14.0,2005-07-01T00:00:00Z,Bat star,Asterina miniata,57\n" +
"Santa Barbara (Webster's Arch),-119.05,33.4666666666667,14.0,2005-07-01T00:00:00Z,Bat star,Asterina miniata,41\n" +
"Santa Barbara (Webster's Arch),-119.05,33.4666666666667,14.0,2005-07-01T00:00:00Z,Bat star,Asterina miniata,55\n";
        Test.ensureEqual(results.substring(0, expected.length()), expected, "\nresults=\n" + results);
        expected = //last 3 lines
"Santa Barbara (Webster's Arch),-119.05,33.4666666666667,14.0,2005-07-01T00:00:00Z,Purple sea urchin,Strongylocentrotus purpuratus,15\n" +
"Santa Barbara (Webster's Arch),-119.05,33.4666666666667,14.0,2005-07-01T00:00:00Z,Purple sea urchin,Strongylocentrotus purpuratus,23\n" +
"Santa Barbara (Webster's Arch),-119.05,33.4666666666667,14.0,2005-07-01T00:00:00Z,Purple sea urchin,Strongylocentrotus purpuratus,19\n";
        Test.ensureEqual(results.substring(results.length() - expected.length()), expected, "\nresults=\n" + results);


        //.csv for test requesting all stations, 1 time, 1 species
        userDapQuery = "" +
            "&time=2005-07-01&common_name=\"Red+abalone\"";
        long time = System.currentTimeMillis();
        tName = eddTable.makeNewFileForDapQuery(null, null, userDapQuery, dir, 
            eddTable.className() + "_eq", ".csv"); 
        String2.log("queryTime=" + (System.currentTimeMillis() - time));
        results = String2.directReadFrom88591File(dir + tName);
        //String2.log(results);
        expected = 
"id,longitude,latitude,depth,time,common_name,species_name,size\n" +
",degrees_east,degrees_north,m,UTC,,,mm\n" +
"San Miguel (Hare Rock),-120.35,34.05,5.0,2005-07-01T00:00:00Z,Red abalone,Haliotis rufescens,13\n" +
"San Miguel (Miracle Mile),-120.4,34.0166666666667,10.0,2005-07-01T00:00:00Z,Red abalone,Haliotis rufescens,207\n" +
"San Miguel (Miracle Mile),-120.4,34.0166666666667,10.0,2005-07-01T00:00:00Z,Red abalone,Haliotis rufescens,203\n" +
"San Miguel (Miracle Mile),-120.4,34.0166666666667,10.0,2005-07-01T00:00:00Z,Red abalone,Haliotis rufescens,193\n";
        Test.ensureEqual(results.substring(0, expected.length()), expected, "\nresults=\n" + results);
        expected = //last 3 lines
"Santa Rosa (South Point),-120.116666666667,33.8833333333333,13.0,2005-07-01T00:00:00Z,Red abalone,Haliotis rufescens,185\n" +
"Santa Rosa (Trancion Canyon),-120.15,33.9,9.0,2005-07-01T00:00:00Z,Red abalone,Haliotis rufescens,198\n" +
"Santa Rosa (Trancion Canyon),-120.15,33.9,9.0,2005-07-01T00:00:00Z,Red abalone,Haliotis rufescens,85\n";
        Test.ensureEqual(results.substring(results.length() - expected.length()), expected, "\nresults=\n" + results);


        //.csv for test requesting all stations, 1 time, 1 species    String !=
        userDapQuery = "" +
            "&time=2005-07-01&id!=\"San+Miguel+(Hare+Rock)\"&common_name=\"Red+abalone\"";
        time = System.currentTimeMillis();
        tName = eddTable.makeNewFileForDapQuery(null, null, userDapQuery, dir, 
            eddTable.className() + "_NE", ".csv"); 
        String2.log("queryTime=" + (System.currentTimeMillis() - time));
        results = String2.directReadFrom88591File(dir + tName);
        //String2.log(results);
        expected = 
"id,longitude,latitude,depth,time,common_name,species_name,size\n" +
",degrees_east,degrees_north,m,UTC,,,mm\n" +
"San Miguel (Miracle Mile),-120.4,34.0166666666667,10.0,2005-07-01T00:00:00Z,Red abalone,Haliotis rufescens,207\n" +
"San Miguel (Miracle Mile),-120.4,34.0166666666667,10.0,2005-07-01T00:00:00Z,Red abalone,Haliotis rufescens,203\n" +
"San Miguel (Miracle Mile),-120.4,34.0166666666667,10.0,2005-07-01T00:00:00Z,Red abalone,Haliotis rufescens,193\n";
        Test.ensureEqual(results.substring(0, expected.length()), expected, "\nresults=\n" + results);
        expected = //last 3 lines
"Santa Rosa (South Point),-120.116666666667,33.8833333333333,13.0,2005-07-01T00:00:00Z,Red abalone,Haliotis rufescens,185\n" +
"Santa Rosa (Trancion Canyon),-120.15,33.9,9.0,2005-07-01T00:00:00Z,Red abalone,Haliotis rufescens,198\n" +
"Santa Rosa (Trancion Canyon),-120.15,33.9,9.0,2005-07-01T00:00:00Z,Red abalone,Haliotis rufescens,85\n";
        Test.ensureEqual(results.substring(results.length() - expected.length()), expected, "\nresults=\n" + results);


        //.csv for test requesting all stations, 1 time, 1 species   String > <
        userDapQuery = "" +
            "&time=2005-07-01&id>\"San+Miguel+(G\"&id<=\"San+Miguel+(I\"&common_name=\"Red+abalone\"";
        time = System.currentTimeMillis();
        tName = eddTable.makeNewFileForDapQuery(null, null, userDapQuery, dir, 
            eddTable.className() + "_gtlt", ".csv"); 
        String2.log("queryTime=" + (System.currentTimeMillis() - time));
        results = String2.directReadFrom88591File(dir + tName);
        //String2.log(results);
        expected = 
"id,longitude,latitude,depth,time,common_name,species_name,size\n" +
",degrees_east,degrees_north,m,UTC,,,mm\n" +
"San Miguel (Hare Rock),-120.35,34.05,5.0,2005-07-01T00:00:00Z,Red abalone,Haliotis rufescens,13\n";
        Test.ensureEqual(results, expected, "\nresults=\n" + results);


        //.csv for test requesting all stations, 1 time, 1 species     REGEX
        userDapQuery = "longitude,latitude,depth,time,id,species_name,size" + //no common_name
            "&time=2005-07-01&id=~\"(zztop|.*Hare+Rock.*)\"&common_name=\"Red+abalone\"";   //but common_name here
        time = System.currentTimeMillis();
        tName = eddTable.makeNewFileForDapQuery(null, null, userDapQuery, dir, 
            eddTable.className() + "_regex", ".csv"); 
        String2.log("queryTime=" + (System.currentTimeMillis() - time));
        results = String2.directReadFrom88591File(dir + tName);
        //String2.log(results);
        expected = 
"longitude,latitude,depth,time,id,species_name,size\n" +
"degrees_east,degrees_north,m,UTC,,,mm\n" +
"-120.35,34.05,5.0,2005-07-01T00:00:00Z,San Miguel (Hare Rock),Haliotis rufescens,13\n";
        Test.ensureEqual(results, expected, "\nresults=\n" + results);

    }


    /**
     * This runs all of the interactive or not interactive tests for this class.
     *
     * @param errorSB all caught exceptions are logged to this.
     * @param interactive  If true, this runs all of the interactive tests; 
     *   otherwise, this runs all of the non-interactive tests.
     * @param doSlowTestsToo If true, this runs the slow tests, too.
     * @param firstTest The first test to be run (0...).  Test numbers may change.
     * @param lastTest The last test to be run, inclusive (0..., or -1 for the last test). 
     *   Test numbers may change.
     */
    public static void test(StringBuilder errorSB, boolean interactive, 
        boolean doSlowTestsToo, int firstTest, int lastTest) {
        if (lastTest < 0)
            lastTest = interactive? 1 : -1;
        String msg = "\n^^^ EDDTableFromNcSequenceFiles.test(" + interactive + ") test=";

        for (int test = firstTest; test <= lastTest; test++) {
            try {
                long time = System.currentTimeMillis();
                String2.log(msg + test);
            
                if (interactive) {

                } else {
                    if (test ==  0) testGenerateDatasetsXml();
                    if (test ==  1) testNested(); 
                }

                String2.log(msg + test + " finished successfully in " + (System.currentTimeMillis() - time) + " ms.");
            } catch (Throwable testThrowable) {
                String eMsg = msg + test + " caught throwable:\n" + 
                    MustBe.throwableToString(testThrowable);
                errorSB.append(eMsg);
                String2.log(eMsg);
                if (interactive) 
                    String2.pressEnterToContinue("");
            }
        }
    }
}

