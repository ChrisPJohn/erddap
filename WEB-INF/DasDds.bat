rem This is the Windows batch file to run DasDds.
rem See http://coastwatch.pfeg.noaa.gov/erddap/download/setupDatasetsXml.html#Tools

# You'll need to change java's path to make this work:
"C:\Program Files (x86)\Java\jre1.8.0_321\bin\java.exe" -cp classes;../../../lib/servlet-api.jar;lib/*  -Xms1000M -Xmx1000M  gov.noaa.pfel.erddap.DasDds %*
