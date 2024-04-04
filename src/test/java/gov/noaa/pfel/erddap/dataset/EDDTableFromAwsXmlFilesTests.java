package gov.noaa.pfel.erddap.dataset;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.cohort.array.Attributes;
import com.cohort.util.Calendar2;
import com.cohort.util.File2;
import com.cohort.util.String2;
import com.cohort.util.Test;

import gov.noaa.pfel.erddap.GenerateDatasetsXml;
import gov.noaa.pfel.erddap.util.EDStatic;
import gov.noaa.pfel.erddap.variable.EDV;
import testDataset.EDDTestDataset;

class EDDTableFromAwsXmlFilesTests {
  @BeforeAll
  static void init() {
    File2.setWebInfParentDirectory();
    System.setProperty("erddapContentDirectory", System.getProperty("user.dir") + "\\content\\erddap");
    System.setProperty("doSetupValidation", String.valueOf(false));
    EDD.debugMode = true;
  }

  /**
   * testGenerateDatasetsXml
   */
  @org.junit.jupiter.api.Test
  void testGenerateDatasetsXml() throws Throwable {
    // testVerboseOn();
    Attributes externalAddAttributes = new Attributes();
    externalAddAttributes.add("title", "New Title!");
    String dataDir = Path.of(EDDTableFromAwsXmlFilesTests.class.getResource("/data/aws/xml/").toURI()).toString();
    String results = EDDTableFromAwsXmlFiles.generateDatasetsXml(
        dataDir, ".*\\.xml", "",
        1, 2, 1440,
        "", "-.*$", ".*", "fileName", // just for test purposes; station is already a column in the file
        "ob-date", "station-id ob-date",
        "http://www.exploratorium.edu", "exploratorium", "The new summary!", "The Newer Title!",
        -1, null, // defaultStandardizeWhat
        externalAddAttributes) + "\n";

    // GenerateDatasetsXml
    String gdxResults = (new GenerateDatasetsXml()).doIt(new String[] { "-verbose",
        "EDDTableFromAwsXmlFiles",
        dataDir, ".*\\.xml", "",
        "1", "2", "1440",
        "", "-.*$", ".*", "fileName", // just for test purposes; station is already a column in the file
        "ob-date", "station-id ob-date",
        "http://www.exploratorium.edu", "exploratorium", "The new summary!", "The Newer Title!",
        "-1", "" }, // defaultStandardizeWhat
        false); // doIt loop?
    Test.ensureEqual(gdxResults, results, "Unexpected results from GenerateDatasetsXml.doIt.");
    String suggDatasetID = EDDTableFromAwsXmlFiles.suggestDatasetID(
        dataDir + "\\.*\\.xml");
    String expected = "<!-- NOTE! Since the source files don't have any metadata, you must add metadata\n" +
        "  below, notably 'units' for each of the dataVariables. -->\n" +
        "<dataset type=\"EDDTableFromAwsXmlFiles\" datasetID=\"" + suggDatasetID + "\" active=\"true\">\n" +
        "    <reloadEveryNMinutes>1440</reloadEveryNMinutes>\n" +
        "    <updateEveryNMillis>10000</updateEveryNMillis>\n" +
        "    <fileDir>" + dataDir + "\\</fileDir>\n" +
        "    <fileNameRegex>.*\\.xml</fileNameRegex>\n" +
        "    <recursive>true</recursive>\n" +
        "    <pathRegex>.*</pathRegex>\n" +
        "    <metadataFrom>last</metadataFrom>\n" +
        "    <columnNamesRow>1</columnNamesRow>\n" +
        "    <firstDataRow>2</firstDataRow>\n" +
        "    <standardizeWhat>0</standardizeWhat>\n" +
        "    <preExtractRegex></preExtractRegex>\n" +
        "    <postExtractRegex>-.*$</postExtractRegex>\n" +
        "    <extractRegex>.*</extractRegex>\n" +
        "    <columnNameForExtract>fileName</columnNameForExtract>\n" +
        "    <sortedColumnSourceName>ob-date</sortedColumnSourceName>\n" +
        "    <sortFilesBySourceNames>station-id ob-date</sortFilesBySourceNames>\n" +
        "    <fileTableInMemory>false</fileTableInMemory>\n" +
        "    <!-- sourceAttributes>\n" +
        "    </sourceAttributes -->\n" +
        "    <!-- Please specify the actual cdm_data_type (TimeSeries?) and related info below, for example...\n" +
        "        <att name=\"cdm_timeseries_variables\">station_id, longitude, latitude</att>\n" +
        "        <att name=\"subsetVariables\">station_id, longitude, latitude</att>\n" +
        "    -->\n" +
        "    <addAttributes>\n" +
        "        <att name=\"cdm_data_type\">Other</att>\n" +
        "        <att name=\"Conventions\">COARDS, CF-1.10, ACDD-1.3</att>\n" +
        "        <att name=\"creator_name\">exploratorium</att>\n" +
        "        <att name=\"creator_url\">http://www.exploratorium.edu</att>\n" +
        "        <att name=\"infoUrl\">http://www.exploratorium.edu</att>\n" +
        "        <att name=\"institution\">exploratorium</att>\n" +
        "        <att name=\"keywords\">air, altitude, atmosphere, atmospheric, aux, aux-temp, aux-temp-rate, aux_temp, aux_temp_rate, average, bulb, city, city-state, city-state-zip, city_state, city_state_zip, data, date, dew, dew point, dew_point, dew_point_temperature, direction, earth, Earth Science &gt; Atmosphere &gt; Altitude &gt; Station Height, Earth Science &gt; Atmosphere &gt; Atmospheric Temperature &gt; Air Temperature, Earth Science &gt; Atmosphere &gt; Atmospheric Temperature &gt; Dew Point Temperature, Earth Science &gt; Atmosphere &gt; Atmospheric Temperature &gt; Surface Air Temperature, Earth Science &gt; Atmosphere &gt; Atmospheric Water Vapor &gt; Dew Point Temperature, Earth Science &gt; Atmosphere &gt; Atmospheric Water Vapor &gt; Humidity, Earth Science &gt; Atmosphere &gt; Atmospheric Winds &gt; Surface Winds, exploratorium, feels, feels-like, feels_like, file, fileName, gust, gust-direction, gust-time, gust_direction, gust_speed, gust_time, height, high, humidity, humidity-rate, humidity_high, humidity_low, humidity_rate, identifier, img, indoor, indoor-temp, indoor-temp-rate, indoor_temp, indoor_temp_rate, light, light-rate, light_rate, like, low, max, meteorology, month, moon, moon-phase, moon-phase-moon-phase-img, moon_phase, moon_phase_moon_phase_img, name, newer, ob-date, phase, point, precipitation, pressure, pressure-high, pressure-low, pressure-rate, pressure_high, pressure_low, pressure_rate, rain, rain-month, rain-rate, rain-rate-max, rain-today, rain-year, rain_month, rain_rate, rain_rate_max, rain_today, rain_year, rainfall, rate, relative, relative_humidity, science, site, site-url, site_url, speed, state, station, station-id, station_id, sunrise, sunset, surface, temp-high, temp-low, temp-rate, temp_high, temp_low, temp_rate, temperature, time, title, today, vapor, water, wet, wet_bulb, wet_bulb_temperature, wind, wind-direction, wind-direction-avg, wind_direction, wind_direction_avg, wind_speed, wind_speed_avg, wind_speed_of_gust, winds, year, zip</att>\n"
        +
        "        <att name=\"keywords_vocabulary\">GCMD Science Keywords</att>\n" +
        "        <att name=\"license\">[standard]</att>\n" +
        "        <att name=\"sourceUrl\">(local files)</att>\n" +
        "        <att name=\"standard_name_vocabulary\">CF Standard Name Table v70</att>\n" +
        "        <att name=\"summary\">The new summary! exploratorium data from a local source.</att>\n" +
        "        <att name=\"title\">The Newer Title!</att>\n" +
        "    </addAttributes>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>fileName</sourceName>\n" +
        "        <destinationName>fileName</destinationName>\n" +
        "        <dataType>String</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Identifier</att>\n" +
        "            <att name=\"long_name\">File Name</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>ob-date</sourceName>\n" +
        "        <destinationName>time</destinationName>\n" +
        "        <dataType>double</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">seconds since 1970-01-01T00:00:00Z</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Time</att>\n" +
        "            <att name=\"long_name\">Ob-date</att>\n" +
        "            <att name=\"source_name\">ob-date</att>\n" +
        "            <att name=\"standard_name\">time</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>station-id</sourceName>\n" +
        "        <destinationName>station_id</destinationName>\n" +
        "        <dataType>String</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Identifier</att>\n" +
        "            <att name=\"long_name\">Station-id</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>station</sourceName>\n" +
        "        <destinationName>station</destinationName>\n" +
        "        <dataType>String</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Identifier</att>\n" +
        "            <att name=\"long_name\">Station</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>city-state-zip</sourceName>\n" +
        "        <destinationName>city_state_zip</destinationName>\n" +
        "        <dataType>String</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Location</att>\n" +
        "            <att name=\"long_name\">City-state-zip</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>city-state</sourceName>\n" +
        "        <destinationName>city_state</destinationName>\n" +
        "        <dataType>String</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Location</att>\n" +
        "            <att name=\"long_name\">City-state</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>site-url</sourceName>\n" +
        "        <destinationName>site_url</destinationName>\n" +
        "        <dataType>String</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Unknown</att>\n" +
        "            <att name=\"long_name\">Site-url</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>aux-temp</sourceName>\n" +
        "        <destinationName>aux_temp</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">degree_F</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">104.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">14.0</att>\n" +
        "            <att name=\"ioos_category\">Temperature</att>\n" +
        "            <att name=\"long_name\">Aux-temp</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>aux-temp-rate</sourceName>\n" +
        "        <destinationName>aux_temp_rate</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">degree_F</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">10.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">-10.0</att>\n" +
        "            <att name=\"ioos_category\">Temperature</att>\n" +
        "            <att name=\"long_name\">Aux-temp-rate</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>dew-point</sourceName>\n" +
        "        <destinationName>dew_point</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">degree_F</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">104.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">14.0</att>\n" +
        "            <att name=\"ioos_category\">Temperature</att>\n" +
        "            <att name=\"long_name\">Dew Point Temperature</att>\n" +
        "            <att name=\"standard_name\">dew_point_temperature</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>elevation</sourceName>\n" +
        "        <destinationName>altitude</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">ft</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"ioos_category\">Location</att>\n" +
        "            <att name=\"long_name\">Altitude</att>\n" +
        "            <att name=\"scale_factor\" type=\"float\">0.3048</att>\n" +
        "            <att name=\"source_name\">elevation</att>\n" +
        "            <att name=\"standard_name\">altitude</att>\n" +
        "            <att name=\"units\">m</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>feels-like</sourceName>\n" +
        "        <destinationName>feels_like</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">degree_F</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">104.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">14.0</att>\n" +
        "            <att name=\"ioos_category\">Temperature</att>\n" +
        "            <att name=\"long_name\">Feels-like</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>gust-time</sourceName>\n" +
        "        <destinationName>gust_time</destinationName>\n" +
        "        <dataType>double</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">seconds since 1970-01-01T00:00:00Z</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Time</att>\n" +
        "            <att name=\"long_name\">Gust-time</att>\n" +
        "            <att name=\"standard_name\">time</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>gust-direction</sourceName>\n" +
        "        <destinationName>gust_direction</destinationName>\n" +
        "        <dataType>String</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Wind</att>\n" +
        "            <att name=\"long_name\">Gust-direction</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>gust-speed</sourceName>\n" +
        "        <destinationName>gust_speed</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">mph</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">30.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
        "            <att name=\"ioos_category\">Wind</att>\n" +
        "            <att name=\"long_name\">Wind Speed Of Gust</att>\n" +
        "            <att name=\"standard_name\">wind_speed_of_gust</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>humidity</sourceName>\n" +
        "        <destinationName>humidity</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">&#37;</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">100.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
        "            <att name=\"ioos_category\">Meteorology</att>\n" +
        "            <att name=\"long_name\">Relative Humidity</att>\n" +
        "            <att name=\"standard_name\">relative_humidity</att>\n" +
        "            <att name=\"units\">percent</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>humidity-high</sourceName>\n" +
        "        <destinationName>humidity_high</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">&#37;</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">100.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
        "            <att name=\"ioos_category\">Meteorology</att>\n" +
        "            <att name=\"long_name\">Relative Humidity</att>\n" +
        "            <att name=\"standard_name\">relative_humidity</att>\n" +
        "            <att name=\"units\">percent</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>humidity-low</sourceName>\n" +
        "        <destinationName>humidity_low</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">&#37;</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">100.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
        "            <att name=\"ioos_category\">Meteorology</att>\n" +
        "            <att name=\"long_name\">Relative Humidity</att>\n" +
        "            <att name=\"standard_name\">relative_humidity</att>\n" +
        "            <att name=\"units\">percent</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>humidity-rate</sourceName>\n" +
        "        <destinationName>humidity_rate</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"ioos_category\">Meteorology</att>\n" +
        "            <att name=\"long_name\">Humidity-rate</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>indoor-temp</sourceName>\n" +
        "        <destinationName>indoor_temp</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">degree_F</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">104.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">14.0</att>\n" +
        "            <att name=\"ioos_category\">Temperature</att>\n" +
        "            <att name=\"long_name\">Indoor-temp</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>indoor-temp-rate</sourceName>\n" +
        "        <destinationName>indoor_temp_rate</destinationName>\n" +
        "        <dataType>float</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">degree_F</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">10.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">-10.0</att>\n" +
        "            <att name=\"ioos_category\">Temperature</att>\n" +
        "            <att name=\"long_name\">Indoor-temp-rate</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>light</sourceName>\n" +
        "        <destinationName>light</destinationName>\n" +
        "        <dataType>float</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Unknown</att>\n" +
        "            <att name=\"long_name\">Light</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>light-rate</sourceName>\n" +
        "        <destinationName>light_rate</destinationName>\n" +
        "        <dataType>float</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Unknown</att>\n" +
        "            <att name=\"long_name\">Light-rate</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>moon-phase-moon-phase-img</sourceName>\n" +
        "        <destinationName>moon_phase_moon_phase_img</destinationName>\n" +
        "        <dataType>String</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Unknown</att>\n" +
        "            <att name=\"long_name\">Moon-phase-moon-phase-img</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>moon-phase</sourceName>\n" +
        "        <destinationName>moon_phase</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"ioos_category\">Unknown</att>\n" +
        "            <att name=\"long_name\">Moon-phase</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>pressure</sourceName>\n" +
        "        <destinationName>pressure</destinationName>\n" +
        "        <dataType>float</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">inch_Hg</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Pressure</att>\n" +
        "            <att name=\"long_name\">Pressure</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>pressure-high</sourceName>\n" +
        "        <destinationName>pressure_high</destinationName>\n" +
        "        <dataType>float</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">inch_Hg</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Pressure</att>\n" +
        "            <att name=\"long_name\">Pressure-high</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>pressure-low</sourceName>\n" +
        "        <destinationName>pressure_low</destinationName>\n" +
        "        <dataType>float</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">inch_Hg</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Pressure</att>\n" +
        "            <att name=\"long_name\">Pressure-low</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>pressure-rate</sourceName>\n" +
        "        <destinationName>pressure_rate</destinationName>\n" +
        "        <dataType>float</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">inch_Hg/h</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Pressure</att>\n" +
        "            <att name=\"long_name\">Pressure-rate</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>rain-month</sourceName>\n" +
        "        <destinationName>rain_month</destinationName>\n" +
        "        <dataType>float</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">inches</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Meteorology</att>\n" +
        "            <att name=\"long_name\">Rain-month</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>rain-rate</sourceName>\n" +
        "        <destinationName>rain_rate</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">inches/h</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"ioos_category\">Meteorology</att>\n" +
        "            <att name=\"long_name\">Rain-rate</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>rain-rate-max</sourceName>\n" +
        "        <destinationName>rain_rate_max</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">inches/h</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"ioos_category\">Meteorology</att>\n" +
        "            <att name=\"long_name\">Rain-rate-max</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>rain-today</sourceName>\n" +
        "        <destinationName>rain_today</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">inches</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"ioos_category\">Meteorology</att>\n" +
        "            <att name=\"long_name\">Rain-today</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>rain-year</sourceName>\n" +
        "        <destinationName>rain_year</destinationName>\n" +
        "        <dataType>float</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">inches</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Meteorology</att>\n" +
        "            <att name=\"long_name\">Rain-year</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>temp</sourceName>\n" +
        "        <destinationName>temp</destinationName>\n" +
        "        <dataType>float</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">degree_F</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">104.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">14.0</att>\n" +
        "            <att name=\"ioos_category\">Temperature</att>\n" +
        "            <att name=\"long_name\">Temperature</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>temp-high</sourceName>\n" +
        "        <destinationName>temp_high</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">degree_F</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">104.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">14.0</att>\n" +
        "            <att name=\"ioos_category\">Temperature</att>\n" +
        "            <att name=\"long_name\">Temp-high</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>temp-low</sourceName>\n" +
        "        <destinationName>temp_low</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">degree_F</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">104.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">14.0</att>\n" +
        "            <att name=\"ioos_category\">Temperature</att>\n" +
        "            <att name=\"long_name\">Temp-low</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>temp-rate</sourceName>\n" +
        "        <destinationName>temp_rate</destinationName>\n" +
        "        <dataType>float</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">degree_F</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">10.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">-10.0</att>\n" +
        "            <att name=\"ioos_category\">Temperature</att>\n" +
        "            <att name=\"long_name\">Temp-rate</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>sunrise</sourceName>\n" +
        "        <destinationName>sunrise</destinationName>\n" +
        "        <dataType>double</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">seconds since 1970-01-01T00:00:00Z</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Time</att>\n" +
        "            <att name=\"long_name\">Sunrise</att>\n" +
        "            <att name=\"standard_name\">time</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>sunset</sourceName>\n" +
        "        <destinationName>sunset</destinationName>\n" +
        "        <dataType>double</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">seconds since 1970-01-01T00:00:00Z</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Time</att>\n" +
        "            <att name=\"long_name\">Sunset</att>\n" +
        "            <att name=\"standard_name\">time</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>wet-bulb</sourceName>\n" +
        "        <destinationName>wet_bulb</destinationName>\n" +
        "        <dataType>float</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">degree_F</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">104.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">14.0</att>\n" +
        "            <att name=\"ioos_category\">Temperature</att>\n" +
        "            <att name=\"long_name\">Wet Bulb Temperature</att>\n" +
        "            <att name=\"standard_name\">wet_bulb_temperature</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>wind-speed</sourceName>\n" +
        "        <destinationName>wind_speed</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">mph</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">15.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
        "            <att name=\"ioos_category\">Wind</att>\n" +
        "            <att name=\"long_name\">Wind Speed</att>\n" +
        "            <att name=\"standard_name\">wind_speed</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>wind-speed-avg</sourceName>\n" +
        "        <destinationName>wind_speed_avg</destinationName>\n" +
        "        <dataType>byte</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "            <att name=\"units\">mph</att>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"_FillValue\" type=\"byte\">127</att>\n" +
        "            <att name=\"colorBarMaximum\" type=\"double\">15.0</att>\n" +
        "            <att name=\"colorBarMinimum\" type=\"double\">0.0</att>\n" +
        "            <att name=\"ioos_category\">Wind</att>\n" +
        "            <att name=\"long_name\">Wind Speed</att>\n" +
        "            <att name=\"standard_name\">wind_speed</att>\n" +
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>wind-direction</sourceName>\n" +
        "        <destinationName>wind_direction</destinationName>\n" +
        "        <dataType>String</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Wind</att>\n" +
        "            <att name=\"long_name\">Wind-direction</att>\n" + // no standard_name because it is String
                                                                       // direction
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "    <dataVariable>\n" +
        "        <sourceName>wind-direction-avg</sourceName>\n" +
        "        <destinationName>wind_direction_avg</destinationName>\n" +
        "        <dataType>String</dataType>\n" +
        "        <!-- sourceAttributes>\n" +
        "        </sourceAttributes -->\n" +
        "        <addAttributes>\n" +
        "            <att name=\"ioos_category\">Wind</att>\n" +
        "            <att name=\"long_name\">Wind-direction-avg</att>\n" + // no standard_name because it is String
                                                                           // direction
        "        </addAttributes>\n" +
        "    </dataVariable>\n" +
        "</dataset>\n" +
        "\n\n";
    Test.ensureEqual(results, expected, "results=\n" + results);

    // ensure it is ready-to-use by making a dataset from it
    // !!! actually this will fail with a specific error which is caught below
    EDD.deleteCachedDatasetInfo(suggDatasetID);
    EDD edd = EDDTableFromAwsXmlFiles.oneFromXmlFragment(null, results);
    Test.ensureEqual(edd.datasetID(), suggDatasetID, "");
    Test.ensureEqual(edd.title(), "The Newer Title!", "");
    Test.ensureEqual(String2.toCSSVString(edd.dataVariableDestinationNames()),
        "fileName, time, station_id, station, city_state_zip, city_state, site_url, aux_temp, aux_temp_rate, dew_point, altitude, feels_like, gust_time, gust_direction, gust_speed, humidity, humidity_high, humidity_low, humidity_rate, indoor_temp, indoor_temp_rate, light, light_rate, moon_phase_moon_phase_img, moon_phase, pressure, pressure_high, pressure_low, pressure_rate, rain_month, rain_rate, rain_rate_max, rain_today, rain_year, temp, temp_high, temp_low, temp_rate, sunrise, sunset, wet_bulb, wind_speed, wind_speed_avg, wind_direction, wind_direction_avg",
        "");

  }

  /**
   * This tests the methods in this class.
   *
   * @throws Throwable if trouble
   */
  @ParameterizedTest
  @ValueSource(booleans = { true, false })
  void testBasic(boolean deleteCachedDatasetInfo) throws Throwable {
    // String2.log("\n****************** EDDTableFromAwsXmlFiles.test()
    // *****************\n");
    // testVerboseOn();
    int language = 0;
    String name, tName, results, tResults, expected, userDapQuery, tQuery;
    String error = "";
    EDV edv;
    String today = Calendar2.getCurrentISODateTimeStringZulu().substring(0, 10);

    String id = "testAwsXml";
    if (deleteCachedDatasetInfo)
      EDDTableFromAwsXmlFiles.deleteCachedDatasetInfo(id);
    EDDTable eddTable = (EDDTable) EDDTestDataset.gettestAwsXml();

    // *** test getting das for entire dataset
    String2.log("\n****************** EDDTableFromAwsXmlFiles test das and dds for entire dataset\n");
    tName = eddTable.makeNewFileForDapQuery(language, null, null, "", EDStatic.fullTestCacheDirectory,
        eddTable.className() + "_Entire", ".das");
    results = File2.directReadFrom88591File(EDStatic.fullTestCacheDirectory + tName);
    // String2.log(results);
    expected = "Attributes {\n" +
        " s {\n" +
        "  fileName {\n" +
        "    String ioos_category \"Identifier\";\n" +
        "    String long_name \"File Name\";\n" +
        "  }\n" +
        "  station_id {\n" +
        "    String ioos_category \"Unknown\";\n" +
        "    String long_name \"Station-id\";\n" +
        "  }\n" +
        "  station {\n" +
        "    String ioos_category \"Unknown\";\n" +
        "    String long_name \"Station\";\n" +
        "  }\n" +
        "  city_state_zip {\n" +
        "    String ioos_category \"Unknown\";\n" +
        "    String long_name \"City-state-zip\";\n" +
        "  }\n" +
        "  city_state {\n" +
        "    String ioos_category \"Unknown\";\n" +
        "    String long_name \"City-state\";\n" +
        "  }\n" +
        "  site_url {\n" +
        "    String ioos_category \"Unknown\";\n" +
        "    String long_name \"Site-url\";\n" +
        "  }\n" +
        "  altitude {\n" +
        "    String _CoordinateAxisType \"Height\";\n" +
        "    String _CoordinateZisPositive \"up\";\n" +
        "    Float32 actual_range 0.0, 0.0;\n" +
        "    String axis \"Z\";\n" +
        "    String ioos_category \"Location\";\n" +
        "    String long_name \"Altitude\";\n" +
        "    String positive \"up\";\n" +
        "    String standard_name \"altitude\";\n" +
        "    String units \"m\";\n" +
        "  }\n" +
        "  time {\n" +
        "    String _CoordinateAxisType \"Time\";\n" +
        "    Float64 actual_range 1.3519746e+9, 1.3519746e+9;\n" +
        "    String axis \"T\";\n" +
        "    String ioos_category \"Time\";\n" +
        "    String long_name \"Time\";\n" +
        "    String standard_name \"time\";\n" +
        "    String time_origin \"01-JAN-1970 00:00:00\";\n" +
        "    String units \"seconds since 1970-01-01T00:00:00Z\";\n" +
        "  }\n" +
        "  aux_temp {\n" +
        "    Float32 actual_range 32.0, 32.0;\n" +
        "    Float64 colorBarMaximum 104.0;\n" +
        "    Float64 colorBarMinimum 14.0;\n" +
        "    String ioos_category \"Temperature\";\n" +
        "    String long_name \"Aux-temp\";\n" +
        "    String units \"degree_F\";\n" +
        "  }\n" +
        "  aux_temp_rate {\n" +
        "    Float32 actual_range 0.0, 0.0;\n" +
        "    Float64 colorBarMaximum 10.0;\n" +
        "    Float64 colorBarMinimum -10.0;\n" +
        "    String ioos_category \"Temperature\";\n" +
        "    String long_name \"Aux-temp-rate\";\n" +
        "    String units \"degree_F\";\n" +
        "  }\n" +
        "  dew_point {\n" +
        "    Float32 actual_range 54.0, 54.0;\n" +
        "    Float64 colorBarMaximum 104.0;\n" +
        "    Float64 colorBarMinimum 14.0;\n" +
        "    String ioos_category \"Temperature\";\n" +
        "    String long_name \"Dew Point Temperature\";\n" +
        "    String standard_name \"dew_point_temperature\";\n" +
        "    String units \"degree_F\";\n" +
        "  }\n" +
        "  feels_like {\n" +
        "    Float32 actual_range 67.0, 67.0;\n" +
        "    Float64 colorBarMaximum 104.0;\n" +
        "    Float64 colorBarMinimum 14.0;\n" +
        "    String ioos_category \"Temperature\";\n" +
        "    String long_name \"Feels-like\";\n" +
        "    String units \"degree_F\";\n" +
        "  }\n" +
        "  gust_time {\n" +
        "    Float64 actual_range 1.3519746e+9, 1.3519746e+9;\n" +
        "    String ioos_category \"Time\";\n" +
        "    String long_name \"Gust-time\";\n" +
        "    String standard_name \"time\";\n" +
        "    String time_origin \"01-JAN-1970 00:00:00\";\n" +
        "    String units \"seconds since 1970-01-01T00:00:00Z\";\n" +
        "  }\n" +
        "  gust_direction {\n" +
        "    Float64 colorBarMaximum 360.0;\n" +
        "    Float64 colorBarMinimum 0.0;\n" +
        "    String ioos_category \"Wind\";\n" +
        "    String long_name \"Gust-direction\";\n" +
        "  }\n" +
        "  gust_speed {\n" +
        "    Float32 actual_range 8.0, 8.0;\n" +
        "    Float64 colorBarMaximum 30.0;\n" +
        "    Float64 colorBarMinimum 0.0;\n" +
        "    String ioos_category \"Wind\";\n" +
        "    String long_name \"Wind Speed Of Gust\";\n" +
        "    String standard_name \"wind_speed_of_gust\";\n" +
        "    String units \"mph\";\n" +
        "  }\n" +
        "  humidity {\n" +
        "    Float32 actual_range 63.0, 63.0;\n" +
        "    Float64 colorBarMaximum 100.0;\n" +
        "    Float64 colorBarMinimum 0.0;\n" +
        "    String ioos_category \"Meteorology\";\n" +
        "    String long_name \"Relative Humidity\";\n" +
        "    String standard_name \"relative_humidity\";\n" +
        "    String units \"percent\";\n" +
        "  }\n" +
        "  humidity_high {\n" +
        "    Float32 actual_range 100.0, 100.0;\n" +
        "    Float64 colorBarMaximum 100.0;\n" +
        "    Float64 colorBarMinimum 0.0;\n" +
        "    String ioos_category \"Meteorology\";\n" +
        "    String long_name \"Relative Humidity\";\n" +
        "    String standard_name \"relative_humidity\";\n" +
        "    String units \"percent\";\n" +
        "  }\n" +
        "  humidity_low {\n" +
        "    Float32 actual_range 63.0, 63.0;\n" +
        "    Float64 colorBarMaximum 100.0;\n" +
        "    Float64 colorBarMinimum 0.0;\n" +
        "    String ioos_category \"Meteorology\";\n" +
        "    String long_name \"Relative Humidity\";\n" +
        "    String standard_name \"relative_humidity\";\n" +
        "    String units \"percent\";\n" +
        "  }\n" +
        "  humidity_rate {\n" +
        "    Float32 actual_range -6.0, -6.0;\n" +
        "    String ioos_category \"Meteorology\";\n" +
        "    String long_name \"Humidity-rate\";\n" +
        "  }\n" +
        "  indoor_temp {\n" +
        "    Float32 actual_range 90.0, 90.0;\n" +
        "    Float64 colorBarMaximum 104.0;\n" +
        "    Float64 colorBarMinimum 14.0;\n" +
        "    String ioos_category \"Temperature\";\n" +
        "    String long_name \"Indoor-temp\";\n" +
        "    String units \"degree_F\";\n" +
        "  }\n" +
        "  indoor_temp_rate {\n" +
        "    Float32 actual_range 4.6, 4.6;\n" +
        "    Float64 colorBarMaximum 10.0;\n" +
        "    Float64 colorBarMinimum -10.0;\n" +
        "    String ioos_category \"Temperature\";\n" +
        "    String long_name \"Indoor-temp-rate\";\n" +
        "    String units \"degree_F\";\n" +
        "  }\n" +
        "  light {\n" +
        "    Float32 actual_range 67.9, 67.9;\n" +
        "    String ioos_category \"Unknown\";\n" +
        "    String long_name \"Light\";\n" +
        "  }\n" +
        "  light_rate {\n" +
        "    Float32 actual_range -0.3, -0.3;\n" +
        "    String ioos_category \"Unknown\";\n" +
        "    String long_name \"Light-rate\";\n" +
        "  }\n" +
        "  moon_phase_moon_phase_img {\n" +
        "    String ioos_category \"Unknown\";\n" +
        "    String long_name \"Moon-phase-moon-phase-img\";\n" +
        "  }\n" +
        "  moon_phase {\n" +
        "    Byte _FillValue 127;\n" +
        "    String _Unsigned \"false\";\n" + // ERDDAP adds
        "    Byte actual_range 82, 82;\n" +
        "    String ioos_category \"Unknown\";\n" +
        "    String long_name \"Moon-phase\";\n" +
        "  }\n" +
        "  pressure {\n" +
        "    Float32 actual_range 30.1, 30.1;\n" +
        "    String ioos_category \"Pressure\";\n" +
        "    String long_name \"Pressure\";\n" +
        "    String units \"inch_Hg\";\n" +
        "  }\n" +
        "  pressure_high {\n" +
        "    Float32 actual_range 30.14, 30.14;\n" +
        "    String ioos_category \"Pressure\";\n" +
        "    String long_name \"Pressure-high\";\n" +
        "    String units \"inch_Hg\";\n" +
        "  }\n" +
        "  pressure_low {\n" +
        "    Float32 actual_range 30.06, 30.06;\n" +
        "    String ioos_category \"Pressure\";\n" +
        "    String long_name \"Pressure-low\";\n" +
        "    String units \"inch_Hg\";\n" +
        "  }\n" +
        "  pressure_rate {\n" +
        "    Float32 actual_range -0.01, -0.01;\n" +
        "    String ioos_category \"Pressure\";\n" +
        "    String long_name \"Pressure-rate\";\n" +
        "    String units \"inch_Hg/h\";\n" +
        "  }\n" +
        "  rain_month {\n" +
        "    Float32 actual_range 0.21, 0.21;\n" +
        "    String ioos_category \"Meteorology\";\n" +
        "    String long_name \"Rain-month\";\n" +
        "    String units \"inches\";\n" +
        "  }\n" +
        "  rain_rate {\n" +
        "    Float32 actual_range 0.0, 0.0;\n" +
        "    String ioos_category \"Meteorology\";\n" +
        "    String long_name \"Rain-rate\";\n" +
        "    String units \"inches/h\";\n" +
        "  }\n" +
        "  rain_rate_max {\n" +
        "    Float32 actual_range 0.0, 0.0;\n" +
        "    String ioos_category \"Meteorology\";\n" +
        "    String long_name \"Rain-rate-max\";\n" +
        "    String units \"inches/h\";\n" +
        "  }\n" +
        "  rain_today {\n" +
        "    Float32 actual_range 0.0, 0.0;\n" +
        "    String ioos_category \"Meteorology\";\n" +
        "    String long_name \"Rain-today\";\n" +
        "    String units \"inches\";\n" +
        "  }\n" +
        "  rain_year {\n" +
        "    Float32 actual_range 1.76, 1.76;\n" +
        "    String ioos_category \"Meteorology\";\n" +
        "    String long_name \"Rain-year\";\n" +
        "    String units \"inches\";\n" +
        "  }\n" +
        "  temp {\n" +
        "    Float32 actual_range 66.9, 66.9;\n" +
        "    Float64 colorBarMaximum 104.0;\n" +
        "    Float64 colorBarMinimum 14.0;\n" +
        "    String ioos_category \"Temperature\";\n" +
        "    String long_name \"Temp\";\n" +
        "    String units \"degree_F\";\n" +
        "  }\n" +
        "  temp_high {\n" +
        "    Float32 actual_range 67.0, 67.0;\n" +
        "    Float64 colorBarMaximum 104.0;\n" +
        "    Float64 colorBarMinimum 14.0;\n" +
        "    String ioos_category \"Temperature\";\n" +
        "    String long_name \"Temp-high\";\n" +
        "    String units \"degree_F\";\n" +
        "  }\n" +
        "  temp_low {\n" +
        "    Float32 actual_range 52.0, 52.0;\n" +
        "    Float64 colorBarMaximum 104.0;\n" +
        "    Float64 colorBarMinimum 14.0;\n" +
        "    String ioos_category \"Temperature\";\n" +
        "    String long_name \"Temp-low\";\n" +
        "    String units \"degree_F\";\n" +
        "  }\n" +
        "  temp_rate {\n" +
        "    Float32 actual_range 3.8, 3.8;\n" +
        "    Float64 colorBarMaximum 10.0;\n" +
        "    Float64 colorBarMinimum -10.0;\n" +
        "    String ioos_category \"Temperature\";\n" +
        "    String long_name \"Temp-rate\";\n" +
        "    String units \"degree_F\";\n" +
        "  }\n" +
        "  sunrise {\n" +
        "    Float64 actual_range 1.351953497e+9, 1.351953497e+9;\n" +
        "    String ioos_category \"Time\";\n" +
        "    String long_name \"Sunrise\";\n" +
        "    String standard_name \"time\";\n" +
        "    String time_origin \"01-JAN-1970 00:00:00\";\n" +
        "    String units \"seconds since 1970-01-01T00:00:00Z\";\n" +
        "  }\n" +
        "  sunset {\n" +
        "    Float64 actual_range 1.351991286e+9, 1.351991286e+9;\n" +
        "    String ioos_category \"Time\";\n" +
        "    String long_name \"Sunset\";\n" +
        "    String standard_name \"time\";\n" +
        "    String time_origin \"01-JAN-1970 00:00:00\";\n" +
        "    String units \"seconds since 1970-01-01T00:00:00Z\";\n" +
        "  }\n" +
        "  wet_bulb {\n" +
        "    Float32 actual_range 59.162, 59.162;\n" +
        "    Float64 colorBarMaximum 104.0;\n" +
        "    Float64 colorBarMinimum 14.0;\n" +
        "    String ioos_category \"Temperature\";\n" +
        "    String long_name \"Wet Bulb Temperature\";\n" +
        "    String standard_name \"wet_bulb_temperature\";\n" +
        "    String units \"degree_F\";\n" +
        "  }\n" +
        "  wind_speed {\n" +
        "    Float32 actual_range 0.0, 0.0;\n" +
        "    Float64 colorBarMaximum 15.0;\n" +
        "    Float64 colorBarMinimum 0.0;\n" +
        "    String ioos_category \"Wind\";\n" +
        "    String long_name \"Wind Speed\";\n" +
        "    String standard_name \"wind_speed\";\n" +
        "    String units \"mph\";\n" +
        "  }\n" +
        "  wind_speed_avg {\n" +
        "    Float32 actual_range 2.0, 2.0;\n" +
        "    Float64 colorBarMaximum 15.0;\n" +
        "    Float64 colorBarMinimum 0.0;\n" +
        "    String ioos_category \"Wind\";\n" +
        "    String long_name \"Wind Speed\";\n" +
        "    String standard_name \"wind_speed\";\n" +
        "    String units \"mph\";\n" +
        "  }\n" +
        "  wind_direction {\n" +
        "    Float64 colorBarMaximum 360.0;\n" +
        "    Float64 colorBarMinimum 0.0;\n" +
        "    String ioos_category \"Wind\";\n" +
        "    String long_name \"Wind From Direction\";\n" +
        "    String standard_name \"wind_from_direction\";\n" +
        "  }\n" +
        "  wind_direction_avg {\n" +
        "    Float64 colorBarMaximum 360.0;\n" +
        "    Float64 colorBarMinimum 0.0;\n" +
        "    String ioos_category \"Wind\";\n" +
        "    String long_name \"Wind From Direction\";\n" +
        "    String standard_name \"wind_from_direction\";\n" +
        "  }\n" +
        " }\n" +
        "  NC_GLOBAL {\n" +
        "    String cdm_data_type \"Other\";\n" +
        "    String Conventions \"COARDS, CF-1.6, ACDD-1.3\";\n" +
        "    String creator_name \"exploratorium\";\n" +
        "    String creator_url \"http://www.exploratorium.edu\";\n" +
        "    Float64 geospatial_vertical_max 0.0;\n" +
        "    Float64 geospatial_vertical_min 0.0;\n" +
        "    String geospatial_vertical_positive \"up\";\n" +
        "    String geospatial_vertical_units \"m\";\n" +
        "    String history \"" + today; // T16:36:59Z (local files)\n" +
    // "2012-11-21T16:36:59Z
    // http://localhost:8080/cwexperimental/tabledap/testAwsXml.das\";\n" +
    String expected2 = "    String infoUrl \"http://www.exploratorium.edu\";\n" +
        "    String institution \"exploratorium\";\n" +
        "    String keywords \"atmosphere, atmospheric, aux, aux-temp, aux-temp-rate, bulb, city, city-state, city-state-zip, dew point, dew_point_temperature, direction, Earth Science > Atmosphere > Atmospheric Temperature > Dew Point Temperature, Earth Science > Atmosphere > Atmospheric Water Vapor > Dew Point Temperature, Earth Science > Atmosphere > Atmospheric Winds > Surface Winds, elevation, exploratorium, feels, feels-like, file, from, gust, high, humidity, humidity-high, humidity-low, humidity-rate, identifier, img, indoor, indoor-temp, indoor-temp-rate, light, light-rate, like, low, max, meteorology, month, moon, moon-phase, moon-phase-moon-phase-img, name, newer, phase, pressure, pressure-high, pressure-low, pressure-rate, rain, rain-month, rain-rate, rain-rate-max, rain-today, rain-year, rate, site, site-url, speed, state, station, station-id, surface, temp-high, temp-low, temp-rate, temperature, time, title, today, url, vapor, water, wet, wet-bulb, wind, wind_from_direction, wind_speed, wind_speed_of_gust, winds, year, zip\";\n"
        +
        "    String keywords_vocabulary \"GCMD Science Keywords\";\n" +
        "    String license \"The data may be used and redistributed for free but is not intended\n" +
        "for legal use, since it may contain inaccuracies. Neither the data\n" +
        "Contributor, ERD, NOAA, nor the United States Government, nor any\n" +
        "of their employees or contractors, makes any warranty, express or\n" +
        "implied, including warranties of merchantability and fitness for a\n" +
        "particular purpose, or assumes any legal liability for the accuracy,\n" +
        "completeness, or usefulness, of this information.\";\n" +
        "    String sourceUrl \"(local files)\";\n" +
        "    String standard_name_vocabulary \"CF Standard Name Table v70\";\n" +
        "    String subsetVariables \"fileName, station_id, station, city_state_zip, city_state, site_url, altitude\";\n"
        +
        "    String summary \"The new summary!\";\n" +
        "    String time_coverage_end \"2012-11-03T20:30:00Z\";\n" +
        "    String time_coverage_start \"2012-11-03T20:30:00Z\";\n" +
        "    String title \"The Newer Title!\";\n" +
        "  }\n";
    tResults = results.substring(0, Math.min(results.length(), expected.length()));
    Test.ensureEqual(tResults, expected, "\nresults=\n" + results);

    int tPo = results.indexOf(expected2.substring(0, 17));
    Test.ensureTrue(tPo >= 0, "tPo=-1 results=\n" + results);
    Test.ensureEqual(
        results.substring(tPo, Math.min(results.length(), tPo + expected2.length())),
        expected2, "results=\n" + results);

    // *** test getting dds for entire dataset
    tName = eddTable.makeNewFileForDapQuery(language, null, null, "", EDStatic.fullTestCacheDirectory,
        eddTable.className() + "_Entire", ".dds");
    results = File2.directReadFrom88591File(EDStatic.fullTestCacheDirectory + tName);
    // String2.log(results);
    expected = "Dataset {\n" +
        "  Sequence {\n" +
        "    String fileName;\n" +
        "    String station_id;\n" +
        "    String station;\n" +
        "    String city_state_zip;\n" +
        "    String city_state;\n" +
        "    String site_url;\n" +
        "    Float32 altitude;\n" +
        "    Float64 time;\n" +
        "    Float32 aux_temp;\n" +
        "    Float32 aux_temp_rate;\n" +
        "    Float32 dew_point;\n" +
        "    Float32 feels_like;\n" +
        "    Float64 gust_time;\n" +
        "    String gust_direction;\n" +
        "    Float32 gust_speed;\n" +
        "    Float32 humidity;\n" +
        "    Float32 humidity_high;\n" +
        "    Float32 humidity_low;\n" +
        "    Float32 humidity_rate;\n" +
        "    Float32 indoor_temp;\n" +
        "    Float32 indoor_temp_rate;\n" +
        "    Float32 light;\n" +
        "    Float32 light_rate;\n" +
        "    String moon_phase_moon_phase_img;\n" +
        "    Byte moon_phase;\n" +
        "    Float32 pressure;\n" +
        "    Float32 pressure_high;\n" +
        "    Float32 pressure_low;\n" +
        "    Float32 pressure_rate;\n" +
        "    Float32 rain_month;\n" +
        "    Float32 rain_rate;\n" +
        "    Float32 rain_rate_max;\n" +
        "    Float32 rain_today;\n" +
        "    Float32 rain_year;\n" +
        "    Float32 temp;\n" +
        "    Float32 temp_high;\n" +
        "    Float32 temp_low;\n" +
        "    Float32 temp_rate;\n" +
        "    Float64 sunrise;\n" +
        "    Float64 sunset;\n" +
        "    Float32 wet_bulb;\n" +
        "    Float32 wind_speed;\n" +
        "    Float32 wind_speed_avg;\n" +
        "    String wind_direction;\n" +
        "    String wind_direction_avg;\n" +
        "  } s;\n" +
        "} s;\n";
    Test.ensureEqual(results, expected, "\nresults=\n" + results);

    // *** test make data files
    String2.log("\n****************** EDDTableFromAwsXmlFiles.test make DATA FILES\n");

    // .csv for one lat,lon,time
    // 46012 -122.879997 37.360001
    userDapQuery = "&fileName=~\"SNFLS|zztop\"";
    tName = eddTable.makeNewFileForDapQuery(language, null, null, userDapQuery, EDStatic.fullTestCacheDirectory,
        eddTable.className() + "_1", ".csv");
    results = File2.directReadFrom88591File(EDStatic.fullTestCacheDirectory + tName);
    // String2.log(results);
    expected = "fileName,station_id,station,city_state_zip,city_state,site_url,altitude,time,aux_temp,aux_temp_rate,dew_point,feels_like,gust_time,gust_direction,gust_speed,humidity,humidity_high,humidity_low,humidity_rate,indoor_temp,indoor_temp_rate,light,light_rate,moon_phase_moon_phase_img,moon_phase,pressure,pressure_high,pressure_low,pressure_rate,rain_month,rain_rate,rain_rate_max,rain_today,rain_year,temp,temp_high,temp_low,temp_rate,sunrise,sunset,wet_bulb,wind_speed,wind_speed_avg,wind_direction,wind_direction_avg\n"
        +
        ",,,,,,m,UTC,degree_F,degree_F,degree_F,degree_F,UTC,,mph,percent,percent,percent,,degree_F,degree_F,,,,,inch_Hg,inch_Hg,inch_Hg,inch_Hg/h,inches,inches/h,inches/h,inches,inches,degree_F,degree_F,degree_F,degree_F,UTC,UTC,degree_F,mph,mph,,\n"
        +
        "SNFLS,SNFLS,Exploratorium,94123,\"San Francisco, CA\",,0.0,2012-11-03T20:30:00Z,32.0,0.0,54.0,67.0,2012-11-03T20:30:00Z,E,8.0,63.0,100.0,63.0,-6.0,90.0,4.6,67.9,-0.3,mphase16.gif,82,30.1,30.14,30.06,-0.01,0.21,0.0,0.0,0.0,1.76,66.9,67.0,52.0,3.8,2012-11-03T14:38:17Z,2012-11-04T01:08:06Z,59.162,0.0,2.0,ENE,E\n";
    Test.ensureEqual(results, expected, "\nresults=\n" + results);
  }
}
