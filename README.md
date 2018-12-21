# Student Access Utility

Web-automated grade/semester calculator written for Windows 7+ and OSX. Requires connection to CISD Student Access Center to create a recorded profile; guest profile does not require connection or setup (functions as simple calculator). Grade viewing must be enabled, so profile recording will not work while grades are locked during "entry and verification" period.

Source files are in '/src' packages.\
Setup and refresh script 'setup.py'\
'schooldata.dat' is used for a browser interaction\
All other files and directories are for project support.

SAU is written in Java 8 and automation is run through Selenium 3.6.0 with ChromeDriver 2.45.\
The small setup script is written in Python.

## Dependencies

Button (https://github.com/michael-c-123/Button)  
Selenium API (https://www.seleniumhq.org/)  
ChromeDriver (http://chromedriver.chromium.org/), runs Google Chrome
