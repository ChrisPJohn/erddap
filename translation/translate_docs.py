import os
from pathlib import Path
import re
import argostranslate.package
import argostranslate.translate
# This assumes you are running it from the project base directory not the translation directory.

# NOTES These are machine translations, and so are inherently imperfect. The use of ERDDAP and
# domain-related jargon makes it even harder. Most of the translated text hasn't even be
# read/proofed by a human. This doesn't attempt to translate messages from lower level code,
# e.g., PrimitiveArray, String2, Math2, HtmlWidgets, MustBe. You would have to add a complex system
# of setting arrays for each stock error message and then passing languageCode into to each method
# so if an error occurred, the translated message would be generated. But even that is trouble if a
# mid-level procedure looks for a specific static error message. Even some ERDDAP-related things like
# accessibleVia... are just English. Most error messages are just English, although the start of the
# message, e.g., "Query Error" may appear translated and in English. Several tags used on the 3rd(?)
# page of the Data Provider Form (e.g., dpt_standardName) maybe shouldn't be translated since they
# refer to a specific CF or ACDD attribute (and the explanation is translated). Several tags can be
# specified in datasets.xml (e.g., &lt;startHeadHtml5&gt;, &lt;standardLicense&lt;). The definitions
# there only affect the English language version. This is not ideal. Many tags get translated by this
# system but only the English version is used, e.g., advl_datasetID. See "NOT TRANSLATED" in
# EDStatic.java for most of these. Language=0 must be English ("en"). Many places in the code
# specify EDStatic....Ar[0] so that only the English version of the tag is used.
# 
# FUTURE? Things to think about. Make it so that, after running Translate, all changes for each
# language are captured separately, so an editor for that language can review them. Disable/change
# the system for removing space after odd '"' and before even '"'? Or just fix problems in tags
# where it has problems (usually some root cause). Need to note max line length in non-html tags?
# Then apply to translated text? There is no dontTranslate for non-html. Is it needed? How do it?
# Do more testing of non-html, e.g., {0}, line breaks, etc. Is there a way to force <submit> to be
# the computer-button meaning? E.g., I think German should be Senden (not "einreichen"). Should the
# language list be the translated language names? Translate EDStatic.errorFromDataSource and use it
# with bilingual()?

from_code = "en"
translate_count = 0
translate_limit = -1 # set this to 0/-1 to translate all

# Download and install Argos Translate package
argostranslate.package.update_package_index()
available_packages = argostranslate.package.get_available_packages()

markdown_formatting_line_start = [
    "# ",
    "## ",
    "### ",
    "#### ",
    "##### ",
    "###### ",
]

markdown_formatting_preserve_preceding_whitespace = [
    "* ",
    "- ",
    # also number.
]

language_code_list  = [
    # "en", # Don't do en->en translation
    "bn",
    "zh", # was "zh-CN"
    "zt", # was "zh-TW"
    "cs",
    "da",
    "nl",
    "fi",
    "fr",
    "de",
    "el",
    #"gu", # not supported in argos
    "hi",
    "hu",
    "id",
    "ga",
    "it",
    "ja",
    "ko",
    #"mr", # not supported in argos
    "nb", # was "no"
    "pl",
    "pt",
    #"pa", # not supported in argos
    "ro",
    "ru",
    "es",
    #"sw", # not supported in argos
    "sv",
    "tl",
    "th",
    "tr",
    "uk",
    "ur",
    #"vi" # not supported in argos
]

dont_translate_strings = [
    # !!!ESSENTIAL: if a short phrase (DAP) is in a long phrase (ERDDAP), the long phrase must come
    # first.
    # main() below has a test for this.
    # phrases in quotes
    "\" since \"",
    "\"{0}\"",
    "\"{1}\"",
    "\"{count}\"",
    "\"\*\*\"",
    "\"[in_i]\"",
    "\"[todd'U]\"",
    "\"%{vol}\"",
    "\"&amp;units=...\"",
    "\"&C;\"",
    "\"&micro;\"", # otherwise it is often dropped from the translation.   Only used in one place
    # in messages.xml.
    "\"BLANK\"",
    "\"c/s\"",
    "\"CA, Monterey\"",
    "\"Cel\"",
    "\"Coastlines\"",
    "\"comment\"",
    "\"content-encoding\"",
    "\"count\"",
    "\"days since Jan 1, 1900\"",
    "\"deg\"",
    "\"deg{north}\"",
    "\"degree\"",
    "\"degree_north\"",
    "\"extended\"",
    "\"farad\"",
    "\"files\"",
    "\"gram\"",
    "\"hours since 0001-01-01\"",
    "\"import\"",
    "\"institution\"",
    "\"J\"",
    "\"joule\"",
    "\"joules\"",
    "\"kg.m2.s-2\"",
    "\"kilo\"",
    "\"LakesAndRivers\"",
    "\"Land\"",
    "\"last\"",
    "\"Linear\"",
    "\"Log\"",
    "\"log\"",
    "\"long_name\"",
    "\"m s-1\"",
    "\"m.s^-1\"",
    "\"meter per second\"",
    "\"meters/second\"",
    "\"mo_g\"",
    "\"months since 1970-01-01\"",
    "\"months since\"",
    "\"Nations\"",
    "\"per\"",
    "\"PER\"",
    "\"Range\"",
    "\"s{since 1970-01-01T00:00:00Z}\"",
    "\"Sea Surface Temperature\"",
    "\"searchFor=wind%20speed\"",
    "\"seconds since\"",
    "\"seconds since 1970-01-01\"",
    "\"seconds since 1970-01-01T00:00:00Z\"",
    "\"since\"",
    "\"SOS\"",
    "\"sos\"",
    "\"sst\"",
    "\"States\"",
    "\"stationID,time/1day,10\"",
    "\"time\"",
    "\"times\"",
    "\"times 1000\"",
    "\"title\"",
    "\"years since\"",
    "'u'",
    "'/'",
    "'*'",
    "'^'",
    "'='",

    # lots of things that shouldn't be translated are within <kbd> and <strong>
    "<kbd>\"long_name=Sea Surface Temperature\"</kbd>",
    "<kbd>{0} : {1}</kbd>",
    "<kbd>{0}</kbd>",
    "<kbd>{1} : {2}</kbd>",
    "<kbd>{41008,41009,41010}</kbd>",
    "<kbd>#1</kbd>",
    # there are some <kbd>&pseudoEntity;</kbd>  The translation system REQUIRES that "pseudoEntity"
    # only use [a-zA-Z0-9].
    "<kbd>&adminEmail;</kbd>",
    "<kbd>&amp;addVariablesWhere(\"<i>attName</i>\",\"<i>attValue</i>\")</kbd>",
    "<kbd>&amp;</kbd>",
    "<kbd>&amp;stationID%3E=%2241004%22</kbd>",
    "<kbd>&amp;stationID&gt;=\"41004\"</kbd>",
    "<kbd>&amp;time&gt;now-7days</kbd>",
    "<kbd>&amp;units(\"UDUNITS\")</kbd>",
    "<kbd>&amp;units(\"UCUM\")</kbd>",
    "<kbd>&category;</kbd>",
    "<kbd>&lt;att name=\"units\"&gt;days since -4712-01-01T00:00:00Z&lt;/att&gt;</kbd>",
    "<kbd>&lt;units_standard&gt;</kbd>",
    "<kbd>&quot;wind speed&quot;</kbd>",
    "<kbd>&quot;datasetID=<i>erd</i>&quot;</kbd>",
    "<kbd>&safeEmail;</kbd>",
    "<kbd>&searchButton;</kbd>",
    "<kbd>(last)</kbd>",
    "<kbd>(unknown)</kbd>",
    "<kbd>--compressed</kbd>",
    "<kbd>-999</kbd>",
    "<kbd>-g</kbd>",
    "<kbd>-o <i>fileDir/fileName.ext</i></kbd>",
    "<kbd>-<i>excludedWord</i></kbd>",
    "<kbd>-&quot;<i>excluded phrase</i>&quot;</kbd>",
    "<kbd>01</kbd>",
    "<kbd>2014</kbd>",
    "<kbd>2020-06-12T06:17:00Z</kbd>",
    "<kbd><i>attName</i>=<i>attValue</i></kbd>",
    "<kbd><i>attName=attValue</i></kbd>",
    "<kbd><i>erddapUrl</i></kbd>",
    "<kbd><i>units</i> since <i>basetime</i></kbd>",
    "<kbd>air_pressure</kbd>",
    "<kbd>algorithm</kbd>",
    "<kbd>altitude</kbd>",
    "<kbd>attribute=value</kbd>",
    "<kbd>AND</kbd>",
    "<kbd>Back</kbd>",
    "<kbd>Bilinear</kbd>",
    "<kbd>Bilinear/4</kbd>",
    "<kbd>bob dot simons at noaa dot gov</kbd>",
    "<kbd>boolean</kbd>",
    "<kbd>Bypass this form</kbd>",
    "<kbd>byte</kbd>",
    "<kbd>Cel</kbd>",
    "<kbd>CMC0.2deg-CMC-L4-GLOB-v2.0</kbd>",
    "<kbd>cmd</kbd>",
    "<kbd>count</kbd>",
    "<kbd>curl --compressed \"<i>erddapUrl</i>\" -o <i>fileDir/fileName#1.ext</i></kbd>",
    "<kbd>curl --compressed -g \"<i>erddapUrl</i>\" -o <i>fileDir/fileName.ext</i></kbd>",
    "<kbd>datasetID</kbd>",
    "<kbd>datasetID/variable/algorithm/nearby</kbd>",
    "<kbd>days since 2010-01-01</kbd>",
    "<kbd>deflate</kbd>",
    "<kbd>degC</kbd>",
    "<kbd>degF</kbd>",
    "<kbd>degK</kbd>",
    "<kbd>degree_C</kbd>",
    "<kbd>degree_F</kbd>",
    "<kbd>degrees_east</kbd>",
    "<kbd>degrees_north</kbd>",
    "<kbd>depth</kbd>",
    "<kbd>double</kbd>",
    "<kbd>File : Open</kbd>",
    "<kbd>File : Save As</kbd>",
    "<kbd>File Type</kbd>",
    "<kbd>File type</kbd>",
    "<kbd>float</kbd>",
    "<kbd>fullName=National Oceanic and Atmospheric Administration</kbd>",
    "<kbd>fullName=National%20Oceanic%20and%20Atmospheric%20Administration</kbd>",
    "<kbd>graph</kbd>",
    "<kbd>Graph Type</kbd>",
    "<kbd>Grid</kbd>",
    "<kbd>HTTP 404 Not-Found</kbd>",
    "<kbd>https://spray.ucsd.edu</kbd>",
    "<kbd>https://www.yourWebSite.com?department=R%26D&amp;action=rerunTheModel</kbd>",
    "<kbd>Identifier</kbd>",
    "<kbd>In 8x</kbd>",
    "<kbd>InverseDistance2</kbd>",
    "<kbd>InverseDistance4</kbd>",
    "<kbd>InverseDistance6</kbd>",
    "<kbd>InverseDistance</kbd>",
    "<kbd>int</kbd>",
    "<kbd>John Smith</kbd>",
    "<kbd>jplMURSST41/analysed_sst/Bilinear/4</kbd>",
    "<kbd>jplMURSST41_analysed_sst_Bilinear_4</kbd>",
    "<kbd>Just generate the URL</kbd>",
    "<kbd>keywords</kbd>",
    "<kbd>last</kbd>",
    # "<kbd>(last)</kbd>" is above
    "<kbd>latitude</kbd>",
    "<kbd>Location</kbd>",
    "<kbd>long</kbd>",
    "<kbd>longitude</kbd>",
    "<kbd>maximum=37.0</kbd>",
    "<kbd>mean</kbd>",
    "<kbd>Mean</kbd>",
    "<kbd>Median</kbd>",
    "<kbd>minimum=32.0</kbd>",
    "<kbd>NaN</kbd>",
    "<kbd>nearby</kbd>",
    "<kbd>Nearest</kbd>",
    "<kbd>No animals were harmed during the collection of this data.</kbd>",
    "<kbd>NOAA NMFS SWFSC</kbd>",
    "<kbd>now-7days</kbd>",
    "<kbd>Ocean Color</kbd>",
    "<kbd>org.ghrsst</kbd>",
    "<kbd>Other</kbd>",
    "<kbd>Point</kbd>",
    "<kbd>Profile</kbd>",
    "<kbd>protocol=griddap</kbd>",
    "<kbd>protocol=tabledap</kbd>",
    "<kbd>Redraw the Graph</kbd>",
    "<kbd>Refine ...</kbd>",
    "<kbd>Scaled</kbd>",
    "<kbd>SD</kbd>",
    "<kbd>sea_water_temperature</kbd>",
    "<kbd>short</kbd>",
    "<kbd>Simons, R.A. 2022. ERDDAP. https://coastwatch.pfeg.noaa.gov/erddap . Monterey, CA: NOAA/NMFS/SWFSC/ERD.</kbd>",
    "<kbd>spee</kbd>",
    "<kbd>speed</kbd>",
    "<kbd>Spray Gliders, Scripps Institution of Oceanography</kbd>",
    "<kbd>[standard]</kbd>",
    "<kbd>STANDARDIZE_UDUNITS=<i>udunitsString</i></kbd>",
    "<kbd>Start:Stop</kbd>",
    "<kbd>Start:Stride:Stop</kbd>",
    "<kbd>Start</kbd>",
    "<kbd>Stop</kbd>",
    "<kbd>Stride</kbd>",
    "<kbd>String</kbd>",
    "<kbd>Submit</kbd>",
    "<kbd>Subset</kbd>",
    "<kbd>Taxonomy</kbd>",
    "<kbd>testOutOfDate</kbd>",
    "<kbd>text=<i>some%20percent-encoded%20text</i></kbd>",
    "<kbd>Time</kbd>",
    "<kbd>time</kbd>",
    "<kbd>time&gt;now-2days</kbd>",
    "<kbd>time&gt;max(time)-2days</kbd>",
    "<kbd>timestamp</kbd>",
    "<kbd>TimeSeries</kbd>",
    "<kbd>TimeSeriesProfile</kbd>",
    "<kbd>title=Spray Gliders, Scripps Institution of Oceanography</kbd>",
    "<kbd>Trajectory</kbd>",
    "<kbd>TrajectoryProfile</kbd>",
    "<kbd>true</kbd>",
    "<kbd>UCUM=<i>ucumString</i></kbd>",
    "<kbd>units=degree_C</kbd>",
    # "<kbd><i>units</i> since <i>basetime</i></kbd>" is above
    "<kbd>Unknown</kbd>",
    "<kbd>URL/action</kbd>",
    "<kbd>variable</kbd>",
    "<kbd>view the URL</kbd>",
    "<kbd>Water Temperature</kbd>",
    "<kbd>waterTemp</kbd>",
    "<kbd>WindSpeed</kbd>",
    "<kbd>wt</kbd>",
    "<kbd>your.name@yourOrganization.org</kbd>",
    "<kbd>yyyy-MM-ddTHH:mm:ssZ</kbd>",
    "<pre>curl --compressed -g \"https://coastwatch.pfeg.noaa.gov/erddap/files/cwwcNDBCMet/nrt/NDBC_41008_met.nc\" -o ndbc/41008.nc</pre>",
    "<pre>curl --compressed \"https://coastwatch.pfeg.noaa.gov/erddap/files/cwwcNDBCMet/nrt/NDBC_{41008,41009,41010}_met.nc\" -o ndbc/#1.nc</pre>",

    # All psuedo entities (used for param names, proper nouns, substitutions)
    #  MUST be here by themselves
    #  OR in <kbd>&pseudoEntity;</kbd> above
    #  so code in postProcessHtml works correctly.
    # postProcessHtml() REQUIRES that "pseudoEntity" only use [a-zA-Z0-9].
    "&acceptEncodingHtml;",
    "&acceptEncodingHtmlh3tErddapUrl;",
    "&adminContact;",
    "&advancedSearch;",
    "&algorithm;",
    "&bgcolor;",
    "&BroughtToYouBy;",
    "&C;",
    # above is <kbd>&category;</kbd>
    "&convertTimeReference;",
    "&cookiesHelp;",
    "&dataFileTypeInfo1;",
    "&dataFileTypeInfo2;",
    "&descriptionUrl;",
    "&datasetListRef;",
    "&e0;",
    "&EasierAccessToScientificData;",
    "&elevation;",
    "&encodedDefaultPIppQuery;",
    "&erddapIs;",
    "&erddapUrl;",
    "&erddapVersion;",
    "&exceptions;",
    "&externalLinkHtml;",
    "&F;",
    "&FALSE;",
    "&format;",
    "&fromInfo;",
    "&g;",
    "&griddapExample;",
    "&headingType;",
    "&height;",
    "&htmlQueryUrl;",
    "&htmlQueryUrlWithSpaces;",
    "&htmlTooltipImage;",
    "&info;",
    "&initialHelp;",
    "&jsonQueryUrl;",
    "&langCode;",
    "&language;",
    "&layers;",
    "&license;",
    "&likeThis;",
    "&loginInfo;",

    # these <tag>s were gathered by code in main that matches a regex in messages.xml
    "&lt;/att&gt;",
    "&lt;addAttributes&gt;",
    "&lt;subsetVariables&gt;",
    "&lt;time_precision&gt;",
    "&lt;units_standard&gt;",
    "&lt;updateUrls&gt;",
    "&lt;",
    "&makeAGraphListRef;",
    "&makeAGraphRef;",
    "&nbsp;",
    "&niceProtocol;",
    "&NTU;",
    "&offerValidMinutes;",
    "&partNumberA;",
    "&partNumberB;",
    "&plainLinkExamples1;",
    "&plainLinkExamples2;",
    "&plainLinkExamples3;",
    "&plainLinkExamples4;",
    "&plainLinkExamples5;",
    "&plainLinkExamples6;",
    "&plainLinkExamples7;",
    "&plainLinkExamples8;",
    "&protocolName;",
    "&PSU;",
    "&requestFormatExamplesHtml;",
    "&requestGetCapabilities;",
    "&requestGetMap;",
    "&resultsFormatExamplesHtml;",
    # above is <kbd>&safeEmail;</kbd>
    "&sampleUrl;",
    "&secondPart;",
    # above is <kbd>&searchButton;</kbd>
    "&serviceWMS;",
    "&sheadingType;",
    "&ssUse;",
    "&standardLicense;",
    "&styles;",
    "&subListUrl;",
    "&tabledapExample;",
    "&tagline;",
    "&tEmailAddress;",
    "&tErddapUrl;",
    "&time;",
    "&transparentTRUEFALSE;",
    "&TRUE;",
    "&tTimestamp;",
    "&tWmsGetCapabilities130;",
    "&tWmsOpaqueExample130Replaced;",
    "&tWmsOpaqueExample130;",
    "&tWmsTransparentExample130Replaced;",
    "&tWmsTransparentExample130;",
    "&tYourName;",
    "&unitsStandard;",
    "&variable;",
    "&version;",
    "&versionLink;",
    "&versionResponse;",
    "&versionStringLink;",
    "&versionStringResponse;",
    "&widgetEmailAddress;",
    "&widgetFrequencyOptions;",
    "&widgetGriddedOptions;",
    "&widgetSelectGroup;",
    "&widgetSubmitButton;",
    "&widgetTabularOptions;",
    "&widgetYourName;",
    "&width;",
    "&wmsVersion;",
    "&wmsManyDatasets;",
    "&WMSSEPARATOR;",
    "&WMSSERVER;",

    # things that are never translated
    "{ }",
    "{east}",
    "{north}",
    "{NTU}",
    "{PSU}",
    "{true}",
    "{west}",
    "( )",
    "(Davis, 1986, eq 5.67, page 367)",
    "(Nephelometric Turbidity Unit)",
    "(OPeN)DAP",
    "(Practical Salinity Units)",
    "[ ]",
    "[standardContact]",
    "[standardDataLicenses]",
    "[standardDisclaimerOfEndorsement]",
    "[standardDisclaimerOfExternalLinks]",
    "[standardPrivacyPolicy]",
    "[standardShortDescriptionHtml]",
    "@noaa.gov",
    ".bz2",
    ".fileType",
    ".gzip",
    ".gz",
    ".hdf",
    ".htmlTable",
    ".itx",
    ".jsonlCSV1",
    ".jsonlCSV", # must be after the .jsonlCSV1
    ".jsonlKVP",
    ".json", # must be after the longer versions
    ".kml",
    ".mat",
    ".nccsv",
    ".nc", # must be after .nccsv
    ".tar",
    ".tgz",
    ".tsv",
    ".xhtml",
    ".zip",
    ".z",

    # text (proper nouns, parameter names, phrases, etc) that shouldn't be translated
    "1230768000 seconds since 1970-01-01T00:00:00Z",
    "2452952 \"days since -4712-01-01\"",
    "2009-01-21T23:00:00Z",
    "60000=AS=AMERICA SAMOA",
    "64000=FM=FEDERATED STATES OF MICRONESIA",
    "66000=GU=GUAM",
    "68000=MH=MARSHALL ISLANDS",
    "69000=MP=NORTHERN MARIANA ISLANDS",
    "70000=PW=PALAU",
    "72000=PR=PUERTO RICO",
    "74000=UM=U.S. MINOR OUTLYING ISLANDS",
    "78000=VI=VIRGIN ISLANDS OF THE UNITED STATES",
    "AJAX",
    "algorithm=Nearest",
    "algorithms for oligotrophic oceans: A novel approach",
    "allDatasets",
    "ArcGIS for Server",
    "ArcGIS",
    "Ardour",
    "Audacity",
    "Awesome ERDDAP",
    "based on three-band reflectance difference, J. Geophys.",
    "beginTime",
    "bob dot simons at noaa dot gov",
    "bob.simons at noaa.gov",
    "C., Lee Z., and Franz, B.A. (2012). Chlorophyll-a",
    "categoryAttributes",
    "centeredTime",
    "Chronological Julian Dates (CJD)",
    "COARDS",
    "colorBarMaximum",
    "colorBarMinimum",
    "Conda",
    "content-encoding",
    "curl",
    # "DAP",  is below, after OPeNDAP
    "d, day, days,",
    "datasetID/variable/algorithm/nearby", # before datasetID
    "datasetID",
    "datasets.xml",
    "Davis, J.C. 1986. Statistics and Data Analysis in Geology, 2nd Ed. John Wiley and Sons. New York, New York.",
    "days since 2010-01-01",
    "deflate",
    "degree_C",
    "degree_F",
    "degrees_east",
    "degrees_north",
    "DODS",
    "DOI",
    "E = &sum;(w Y)/&sum;(w)",
    "Earth Science &amp; Atmosphere &amp; Atmospheric Pressure &amp; Atmospheric Pressure Measurements",
    "Earth Science &amp; Atmosphere &amp; Atmospheric Pressure &amp; Sea Level Pressure",
    "Earth Science &amp; Atmosphere &amp; Atmospheric Pressure &amp; Static Pressure",
    "EDDGrid",
    "encodeURIComponent()",
    "endTime",
    "ERDDAP™",
    "erddapContentDirectory",
    "=~tomcat/content/erddap",
    "_tomcat_/content/erddap",
    "_tomcat_\\bin\\startup.bat",
    "_tomcat_\\bin\\setenv.bat",
    "ERDDAP", # before ERD and DAP
    "erd dot data at noaa dot gov",
    "erd.data at noaa.gov",
    "ERD",
    "ESPRESSO",
    "ESPreSSO",
    "ESRI .asc",
    "ESRI GeoServices REST",
    "excludedWord",
    "Ferret",
    "FileInfo.com",
    "fileType={0}",
    "FIPS",
    "GetCapabilities",
    "GetMap",
    "Gimp",
    "GNOME",
    "Google Charts",
    "Google Earth",
    "Google Visualization",
    # "gzip", #is below after x-gzip
    "h, hr, hrs, hour, hours,",
    "HDF",
    "http<strong>s</strong>",
    "https://coastwatch.pfeg.noaa.gov/erddap/files/jplMURSST41/.csv",
    "https://coastwatch.pfeg.noaa.gov/erddap/files/jplMURSST41/",
    "HTTP GET",
    "Hyrax",
    "InverseDistance",
    "IOOS DIF SOS",
    "IOOS Animal Telemetry Network",
    "IrfanView",
    "Java",
    "java.net.URLEncoder",
    "Leaflet",
    "long_name",
    "m, min, mins, minute, minutes,",
    "mashups",
    "Matlab",
    "maximum=37.0",
    "minimum=32.0",
    "mon, mons, month, months,",
    "ms, msec, msecs, millis, millisecond, milliseconds,",
    "NASA's Panoply",
    "National Oceanic and Atmospheric Administration",
    "NCO",
    "Ncview",
    "Nearest, Bilinear, Scaled",
    "NetCDF",
    "NMFS",
    "NOAA",
    "now-7days", # before now-
    "now-",
    "ODV .txt",
    "OGC",
    "OOSTethys",
    "OPeNDAP",
    "DAP", # out of place, so that it is after ERDDAP and OPeNDAP
    "OpenID",
    "OpenLayers",
    "OpenSearch",
    "Oracle",
    "orderBy(\"stationID, time\")", # before orderBy
    "orderByClosest(\"stationID, time/2hours\")",
    "orderByCount(\"stationID, time/1day\")",
    "orderByMax(\"stationID, time/1day\")",
    "orderByMax(\"stationID, time/1day, 10\")",
    "orderByMax(\"stationID, time/1day, temperature\")",
    "orderByMinMax(\"stationID, time/1day, temperature\")",
    "orderByClosest",
    "orderByCount",
    "orderByLimit",
    "orderByMax",
    "orderByMean",
    "orderByMinMax",
    "orderBy", # must be after the longer versions
    "Panoply",
    "Photoshop",
    "position={1}",
    "Practical Salinity Units",
    "protocol=griddap",
    "protocol=tabledap",
    "PSU",
    "Pull",
    "Push",
    "Python",
    "Res., 117, C01011, doi:10.1029/2011JC007395.",
    "RESTful", # before REST
    "REST",
    "ROA",
    "RSS",
    "s, sec, secs, second, seconds,",
    "Satellite Application Facility",
    "Sea Surface Temperature",
    "searchEngine=lucene",
    "shutdown.bat",
    "SOAP+XML",
    "SOS",
    "sst",
    "stationID",
    "StickX",
    "StickY",
    "<strong>lines</strong>",
    "<strong>linesAndMarkers</strong>",
    "<strong>markers</strong>",
    "<strong>sticks</strong>",
    "<strong>surface</strong>",
    "<strong>vectors</strong>",
    "subsetVariables",
    "Surface Skin Temperature",
    "SWFSC", # before WFS
    "Synthetic Aperture Focusing",
    "tabledap",
    "Todd",
    "uDig",
    "UDUNITS",
    "Unidata",
    "URN",
    "WCS",
    "week, weeks,",
    "WFS",
    "wget",
    "Wikipedia",
    "WMS",
    "x-gzip",
    "gzip", # must be after x-gzip
    "yr, yrs, year, or years",
    "yyyy-MM-ddTHH:mm:ssZ", # before yyyy-MM-dd
    "yyyy-MM-dd",
    "Zulu",
    "\*.sh",
]

# For testing
# language_code_list  = [
#    "fr",
# ]

def get_file_name(file_path):
    file_path_components = file_path.split('/')
    return file_path_components[-1]

def get_docs_file_path(file_path):
    path = Path(file_path)
    if path.parent.name == "docs":
        return path.name
    else:
        return os.path.join(path.parent.name, path.name)
    
def get_json_path(file_path):
    path = Path(file_path)
    if path.parent.name == "en":
        return path.name
    else:
        return os.path.join(path.parent.name, path.name)

def find_files(src_filepath, extension):
    filepath_list = []
    #This for loop uses the os.walk() function to walk through the files and directories
    #and records the filepaths of the files to a list
    for root, dirs, files in os.walk(src_filepath):
        #iterate through the files currently obtained by os.walk() and
        #create the filepath string for that file and add it to the filepath_list list
        for file in files:
            #Checks to see if the root is '.' and changes it to the correct current
            #working directory by calling os.getcwd(). Otherwise root_path will just be the root variable value.
            if root == '.':
                root_path = os.getcwd() + "/"
            else:
                root_path = root
            filepath = root_path + "/" + file
            #Appends filepath to filepath_list if filepath does not currently exist in filepath_list
            # Also don't include auto generated documentation (dokka)
            if filepath not in filepath_list and filepath.endswith(extension) and not "dokka" in filepath:
                filepath_list.append(filepath)
    #Return filepath_list        
    return filepath_list

def find_and_install_langauge_package(target):
    package_to_install = next(
        filter(
            lambda x: x.from_code == from_code and x.to_code == target, available_packages
        )
    )
    argostranslate.package.install_from_path(package_to_install.download())

def escape_text(text):
    text = text.replace("&", "&amp;")
    text = text.replace("<", "&lt;")
    text = text.replace(">", "&gt;")
    text = text.replace('"', "&quot;")
    text = text.replace("'", "&#39;")
    return text

def is_index_in_tag(string, index, opening_tag, closing_tag):
    """
    Checks if a given index in a string is within a specific tag.

    Args:
        string: The string to search.
        index: The index to check.
        opening_tag: The opening tag (e.g., "<div>").
        closing_tag: The closing tag (e.g., "</div>").

    Returns:
        bool: True if the index is within a tag, False otherwise.
    """

    tag_stack = []
    for i, char in enumerate(string):
        if i == index:
            return len(tag_stack) != 0
        if char == opening_tag[0]:
            if string[i:i+len(opening_tag)] == opening_tag:
                tag_stack.append(i)
        if char == closing_tag[0]:
            if len(tag_stack) > 0 and string[i:i+len(closing_tag)] == closing_tag:
                tag_stack.pop()

    return False

class LinkMatcher:
  def getMatch(self, chunk):
    self.match = re.search(r"\[(.*?)\]\((.*?)\)", chunk)

  def getStart(self):
    if self.match:
        return self.match.start()
    else:
        return -1
    
  def getEnd(self):
    if self.match:
        return self.match.end()
    else:
        return -1

  def processMatch(self, processed_line, idx, chunk):
    # before link text
    processed_line["translate_text"][idx] = chunk[:self.match.start()]
    # link text
    processed_line["translate_text"].append(self.match.group(1))
    # text after the link
    processed_line["translate_text"].append(chunk[self.match.end():])
    # update format {idx} -> {idx} + "[" +"{legnth-2}" +"]" + "(" + match.group(2) + ")" + "{length-1}"
    placeholder = "{"+ str(idx) +"}"
    processed_line["format"] = processed_line["format"].replace(placeholder, placeholder + "[{" + str(len(processed_line["translate_text"]) -2) + "}](" + self.match.group(2) + "){" + str(len(processed_line["translate_text"]) -1) + "}")
    return processed_line
  
class TagMatcher:
  def getMatch(self, chunk):
    self.match = re.search(r"<(.*?)>", chunk)

  def getStart(self):
    if self.match:
        return self.match.start()
    else:
        return -1
  def getEnd(self):
    if self.match:
        return self.match.end()
    else:
        return -1
  def processMatch(self, processed_line, idx, chunk):
    # before tag text
    processed_line["translate_text"][idx] = chunk[:self.match.start()]
    # text after the tag
    processed_line["translate_text"].append(chunk[self.match.end():])
    # update format {idx} -> {idx} + "<" + match.group(0) + ">" + "{length-1}"
    placeholder = "{"+ str(idx) +"}"
    processed_line["format"] = processed_line["format"].replace(placeholder, placeholder + self.match.group(0) + "{" + str(len(processed_line["translate_text"]) -1) + "}")
    return processed_line

class EscapedTagMatcher:
  def getMatch(self, chunk):
    self.match = re.search(r"\&lt\;(.*?)\&gt\;", chunk)

  def getStart(self):
    if self.match:
        return self.match.start()
    else:
        return -1
  def getEnd(self):
    if self.match:
        return self.match.end()
    else:
        return -1
  def processMatch(self, processed_line, idx, chunk):
    # before tag text
    processed_line["translate_text"][idx] = chunk[:self.match.start()]
    # text after the tag
    processed_line["translate_text"].append(chunk[self.match.end():])
    # update format {idx} -> {idx} + "<" + match.group(0) + ">" + "{length-1}"
    placeholder = "{"+ str(idx) +"}"
    processed_line["format"] = processed_line["format"].replace(placeholder, placeholder + self.match.group(0) + "{" + str(len(processed_line["translate_text"]) -1) + "}")
    return processed_line
  
class BoldMatcher:
  def getMatch(self, chunk):
    self.match = re.search(r"\*\*(.*?)\*\*", chunk)

  def getStart(self):
    if self.match:
        return self.match.start()
    else:
        return -1
  def getEnd(self):
    if self.match:
        return self.match.end()
    else:
        return -1
  def processMatch(self, processed_line, idx, chunk):
    # before tag text
    processed_line["translate_text"][idx] = chunk[:self.match.start()]
    # The match text
    processed_line["translate_text"].append(self.match.group(1))
    # text after the tag
    processed_line["translate_text"].append(chunk[self.match.end():])
    # update format {idx} -> {idx} + "**" + {length-1} + "**" + "{length-2}"
    placeholder = "{"+ str(idx) +"}"
    processed_line["format"] = processed_line["format"].replace(placeholder, placeholder + " **" + "{" + str(len(processed_line["translate_text"]) -2) + "}** {"  + str(len(processed_line["translate_text"]) -1) + "}")
    return processed_line

class ItalicMatcher:
  def getMatch(self, chunk):
    self.match = re.search(r"\*(.*?)\*", chunk)
  def getStart(self):
    if self.match:
        return self.match.start()
    else:
        return -1
  def getEnd(self):
    if self.match:
        return self.match.end()
    else:
        return -1
  def processMatch(self, processed_line, idx, chunk):
    # before tag text
    processed_line["translate_text"][idx] = chunk[:self.match.start()]
    # The match text
    processed_line["translate_text"].append(self.match.group(1))
    # text after the tag
    processed_line["translate_text"].append(chunk[self.match.end():])
    # update format {idx} -> {idx} + "*" + {length-1} + "*" + "{length-2}"
    placeholder = "{"+ str(idx) +"}"
    processed_line["format"] = processed_line["format"].replace(placeholder, placeholder + " *" + "{" + str(len(processed_line["translate_text"]) -2) + "}* {"  + str(len(processed_line["translate_text"]) -1) + "}")
    return processed_line

class ParenthesisMatcher:
  def getMatch(self, chunk):
    self.match = re.search(r"\((.*?)\)", chunk)
  def getStart(self):
    if self.match:
        return self.match.start()
    else:
        return -1
  def getEnd(self):
    if self.match:
        return self.match.end()
    else:
        return -1
  def processMatch(self, processed_line, idx, chunk):
    # before tag text
    processed_line["translate_text"][idx] = chunk[:self.match.start()]
    # The match text
    processed_line["translate_text"].append(self.match.group(1))
    # text after the tag
    processed_line["translate_text"].append(chunk[self.match.end():])
    # update format {idx} -> {idx} + "*" + {length-1} + "*" + "{length-2}"
    placeholder = "{"+ str(idx) +"}"
    processed_line["format"] = processed_line["format"].replace(placeholder, placeholder + " (" + "{" + str(len(processed_line["translate_text"]) -2) + "}) {"  + str(len(processed_line["translate_text"]) -1) + "}")
    return processed_line
  
class BracesMatcher:
  def getMatch(self, chunk):
    self.match = re.search(r"\{(.*?)\}", chunk)
  def getStart(self):
    if self.match:
        return self.match.start()
    else:
        return -1
  def getEnd(self):
    if self.match:
        return self.match.end()
    else:
        return -1
  def processMatch(self, processed_line, idx, chunk):
    # before tag text
    processed_line["translate_text"][idx] = chunk[:self.match.start()]
    # text after the tag
    processed_line["translate_text"].append(chunk[self.match.end():])
    # update format {idx} -> {idx} + "<" + match.group(0) + ">" + "{length-1}"
    placeholder = "{"+ str(idx) +"}"
    processed_line["format"] = processed_line["format"].replace(placeholder, placeholder + self.match.group(0) + "{" + str(len(processed_line["translate_text"]) -1) + "}")
    return processed_line

markdown_matchers = [
    LinkMatcher(),
    TagMatcher(),
    EscapedTagMatcher(),
    BoldMatcher(),
    ItalicMatcher(),
    ParenthesisMatcher(),
]
def preprocess_file(text):
    line_info = []
    # {
    #   format: "1. **{0}** *{1}*"
    #   0: "text to be translated"
    #   1: "more text to be translated"
    # }

    # check order:
    # markdown_formatting_line_start
    # markdown_formatting_preserve_preceding_whitespace (also check whitespace + num + '.')
    # markdown links, <a> links and other text within <>
    # dont_translate_strings
    # markdown_text_styling (check for pre and post) (is this one needed?)
    is_code_block = False
    for line in text:
        processed_line = {
            "format": "{0}",
            "translate_text": [line],
        }

        # "---", # frontmatter boundaries
        if processed_line["translate_text"][0].strip() == "---":
            processed_line["format"] = processed_line["translate_text"][0]
            processed_line["translate_text"].clear()
            line_info.append(processed_line)
            continue

        if processed_line["translate_text"][0].strip().startswith("sidebar_position:"):
            processed_line["format"] = processed_line["translate_text"][0]
            processed_line["translate_text"].clear()
            line_info.append(processed_line)
            continue

        if processed_line["translate_text"][0].strip().startswith("title:"):
            processed_line["format"] = processed_line["translate_text"][0]
            processed_line["translate_text"].clear()
            line_info.append(processed_line)
            continue

        # Detect code blocks (start or stop)
        if processed_line["translate_text"][0].strip().startswith("```"):
            processed_line["format"] = processed_line["translate_text"][0]
            processed_line["translate_text"].clear()
            line_info.append(processed_line)
            is_code_block = not is_code_block
            continue

        # Don't translate block quote lines
        if processed_line["translate_text"][0].strip().startswith(">"):
            processed_line["format"] = processed_line["translate_text"][0]
            processed_line["translate_text"].clear()
            line_info.append(processed_line)
            continue

        # if in a code block, don't translate
        if is_code_block:
            processed_line["format"] = processed_line["translate_text"][0]
            processed_line["translate_text"].clear()
            line_info.append(processed_line)
            continue
        

        for formatting in markdown_formatting_line_start:
            if processed_line["translate_text"][0].strip().startswith(formatting):
                index = processed_line["translate_text"][0].index(formatting)
                processed_line["format"] = processed_line["translate_text"][0][:(index+len(formatting))] + processed_line["format"]
                processed_line["translate_text"][0] = processed_line["translate_text"][0][(index+len(formatting)):]
                # Handle explicit tags in headers
                matcher = BracesMatcher()
                chunk = processed_line["translate_text"][0]
                best_match = None
                matcher.getMatch(chunk)
                if matcher.getStart() > -1:
                    processed_line = matcher.processMatch(processed_line, 0, chunk)

        if processed_line["translate_text"][0].startswith("title: \""):
            processed_line["format"] = "title: \"{0}\"\n"
            processed_line["translate_text"][0] = processed_line["translate_text"][0][8:-2]

        # only do these checks if we didn't already have formatting on this line.
        if len(processed_line["format"]) == 3:
            for formatting in markdown_formatting_preserve_preceding_whitespace:
                if processed_line["translate_text"][0].strip().startswith(formatting):
                    index = processed_line["translate_text"][0].index(formatting)
                    processed_line["format"] = processed_line["translate_text"][0][:(index+len(formatting))] + processed_line["format"]
                    processed_line["translate_text"][0] = processed_line["translate_text"][0][(index+len(formatting)):]

        # Numbered lists
        if (len(processed_line["format"]) == 3):
            pattern = r"^(\s*\d+\. )"
            match = re.match(pattern, processed_line["translate_text"][0])
            if match:
                formatting = match.group(0)
                processed_line["format"] = formatting + processed_line["format"]
                processed_line["translate_text"][0] = processed_line["translate_text"][0][len(formatting):]
        
        idx = 0
        while idx < len(processed_line["translate_text"]):
            chunk = processed_line["translate_text"][idx]
            best_match = None
            for matcher in markdown_matchers:
                matcher.getMatch(chunk)
                if not best_match:
                    if matcher.getStart() > -1:
                        best_match = matcher
                elif matcher.getEnd() > -1 and matcher.getEnd() > best_match.getEnd():
                    best_match = matcher
            if best_match:
                processed_line = best_match.processMatch(processed_line, idx, chunk)
                # reprocess this chunk, don't increment the index
            else:
                idx = idx + 1

        for no_translate in dont_translate_strings:
            for idx, chunk in enumerate(processed_line["translate_text"]):
                index = chunk.find(no_translate)
                if index > -1:
                    # If there's more than one, we will find it later in the for loop,
                    # this iterates over chunks that are appended during the iteration
                    processed_line["translate_text"][idx] = chunk[:index]
                    processed_line["translate_text"].append(chunk[index+len(no_translate):])
                    placeholder = "{"+ str(idx) +"}"
                    processed_line["format"] = processed_line["format"].replace(placeholder, placeholder + no_translate + "{" + str(len(processed_line["translate_text"]) -1) + "}")

        line_info.append(processed_line)

    return line_info

def postprocess_line(format, chunks):
    # text = text.replace(start_no_translate + "&amp;", start_no_translate + "&")
    # text = text.replace(start_no_translate + "<kbd>&amp;", start_no_translate + "<kbd>&")
    index = 0
    end_span = 0
    count = 0
    while index > -1 and end_span > -1:
        index = format.find("{", index)
        end_span = format.find("}", index)
        if index > -1 and end_span > -1:
            chunk_id = format[index+1:end_span]
            if index + 5 > end_span and chunk_id.isdigit() and int(chunk_id) < len(chunks):
                format = format[0:index] + chunks[int(chunk_id)] + format[end_span+1:]
                count = count + 1
            else:
                index = index + 1
    format = fix_invalid_escapes(format)
    if not format.endswith('\n'):
        format = format + "\n"
    return format


def translate_markdown(file_contents, output_file, to_code):
    translated_file = []
    for line in file_contents:
        translated_text = []
        for chunk in line["translate_text"]:
            translated_text.append(argostranslate.translate.translate(chunk, from_code=from_code, to_code=to_code))

        translated_file.append(postprocess_line(line["format"], translated_text))
    
    Path(os.path.dirname(output_file)).mkdir(parents=True, exist_ok=True)
    with open(output_file, "w", encoding="utf-8") as out_file:
        out_file.writelines(translated_file)

def translate_markdown_all(input_file, i18n_path):
    with open(input_file, 'r', encoding="utf-8") as to_translate:
        file_contents = to_translate.readlines()

        file_contents = preprocess_file(file_contents)
        for lang_code in language_code_list:
            print("translating to language: " + lang_code)
            translate_markdown(file_contents, "documentation/i18n/" + lang_code + i18n_path, lang_code)

def preprocess_json_file(lines):
    line_info = []
    json_matches = [
        "{",
        "},",
        "}",
    ]
    for line in lines:
        processed_line = {
            "format": "{0}",
            "translate_text": [line],
        }

        for pattern in json_matches:
            if processed_line["translate_text"][0].strip() == pattern:
                processed_line["format"] = processed_line["translate_text"][0]
                processed_line["translate_text"].clear()
                break
        if not processed_line["translate_text"]:
            line_info.append(processed_line)
            continue

        # if line is "{text}": {
        # don't translate it, treat it as formatting
        match = re.search(r'\".*?\": {', processed_line["translate_text"][0])
        if match:
            processed_line["format"] = processed_line["translate_text"][0]
            processed_line["translate_text"].clear()
            line_info.append(processed_line)
            continue

        # if line is "description": "{text}"
        # don't translate it, treat it as formatting
        match = re.search(r'\"description\": \".*?\"', processed_line["translate_text"][0])
        if match:
            processed_line["format"] = processed_line["translate_text"][0]
            processed_line["translate_text"].clear()
            line_info.append(processed_line)
            continue

        match = re.search(r'\"message\": \"(.*?)\"', processed_line["translate_text"][0])
        if match:
            processed_line["format"] = processed_line["translate_text"][0][:match.start()] + "\"message\": \"{0}\""
            if processed_line["translate_text"][0].endswith(',\n'):
                processed_line["format"] = processed_line["format"] + ',\n'
                processed_line["translate_text"][0] = processed_line["translate_text"][0][match.start()+12:-3]
            else:
                processed_line["format"] = processed_line["format"] + '\n'
                processed_line["translate_text"][0] = processed_line["translate_text"][0][match.start()+12:-2]

        # Handle translation placeholders
        for idx, chunk in enumerate(processed_line["translate_text"]):
            match = re.search(r"{(.*?)}", chunk)
            if match:
                # before tag text
                processed_line["translate_text"][idx] = chunk[:match.start()]
                # text after the tag
                processed_line["translate_text"].append(chunk[match.end():])
                # update format {idx} -> {idx} + "<" + match.group(0) + ">" + "{length-1}"
                placeholder = "{"+ str(idx) +"}"
                processed_line["format"] = processed_line["format"].replace(placeholder, placeholder + match.group(0) + "{" + str(len(processed_line["translate_text"]) -1) + "}")

        # handle html tags
        for idx, chunk in enumerate(processed_line["translate_text"]):
            match = re.search(r"<(.*?)>", chunk)
            if match:
                # before tag text
                processed_line["translate_text"][idx] = chunk[:match.start()]
                # text after the tag
                processed_line["translate_text"].append(chunk[match.end():])
                # update format {idx} -> {idx} + "<" + match.group(0) + ">" + "{length-1}"
                placeholder = "{"+ str(idx) +"}"
                processed_line["format"] = processed_line["format"].replace(placeholder, placeholder + match.group(0) + "{" + str(len(processed_line["translate_text"]) -1) + "}")

        for no_translate in dont_translate_strings:
            for idx, chunk in enumerate(processed_line["translate_text"]):
                index = chunk.find(no_translate)
                if index > -1:
                    # If there's more than one, we will find it later in the for loop,
                    # this iterates over chunks that are appended during the iteration
                    processed_line["translate_text"][idx] = chunk[:index]
                    processed_line["translate_text"].append(chunk[index+len(no_translate):])
                    placeholder = "{"+ str(idx) +"}"
                    processed_line["format"] = processed_line["format"].replace(placeholder, placeholder + no_translate + "{" + str(len(processed_line["translate_text"]) -1) + "}")

        line_info.append(processed_line)
    return line_info

def fix_invalid_escapes(text):
  """Detects and fixes invalid escape sequences by adding an extra backslash.

  Args:
    text: The input string.

  Returns:
    The string with invalid escape sequences fixed.
  """
  pattern = r"(?<!\\)\\([^ntbrf\"'\\\0])"
  while True:
    match = re.search(pattern, text)
    if match:
        text = text[:match.start()] + "\\" + text[match.start():]
    else:
        break
  
  return text

def translate_json(file_contents, output_file, to_code):
    translated_file = []
    for line in file_contents:
        translated_text = []
        for chunk in line["translate_text"]:
            translated_chunk = argostranslate.translate.translate(chunk, from_code=from_code, to_code=to_code)
            # make sure quotes are escapted
            translated_chunk = re.sub(r'(?<!\\)"', r'\\"', translated_chunk)
            translated_text.append(translated_chunk)

        translated_file.append(postprocess_line(line["format"], translated_text))
    
    Path(os.path.dirname(output_file)).mkdir(parents=True, exist_ok=True)
    with open(output_file, "w", encoding="utf-8") as out_file:
        out_file.writelines(translated_file)

def translate_json_all(input_file, i18n_path):
    print (input_file)
    with open(input_file, 'r', encoding="utf-8") as to_translate:
        file_contents = to_translate.readlines()

        file_contents = preprocess_json_file(file_contents)
        for lang_code in language_code_list:
            print("translating to language: " + lang_code)
            translate_json(file_contents, "documentation/i18n/" + lang_code + "/" + i18n_path, lang_code)

for lang_code in language_code_list:
    print("Installing language: " + lang_code)
    find_and_install_langauge_package(lang_code)

# translate markdown pages
pages_dir = "documentation/src/pages"
pages_list = find_files(pages_dir, ".md")
for page in pages_list:
    # page output: i18n/{lang_code}/docusaurus-plugin-content-pages/{pageName}.md
    print(get_file_name(page))
    translate_markdown_all(page, "/docusaurus-plugin-content-pages/" + get_file_name(page))

# translate markdown docs
docs_dir = "documentation/docs"
docs_list = find_files(docs_dir, ".md")
for doc in docs_list:
    # page output: i18n/{lang_code}/docusaurus-plugin-content-docs/current/{docName}.md
    print(get_file_name(doc))
    translate_markdown_all(doc, "/docusaurus-plugin-content-docs/current/" + get_docs_file_path(doc))

# translate json text
json_dir = "documentation\i18n\en"
json_list = find_files(json_dir, ".json")
for file in json_list:
    # page output: i18n/{lang_code}/docusaurus-plugin-content-pages/{pageName}.md
    print(get_file_name(file))
    translate_json_all(file, get_json_path(file))

