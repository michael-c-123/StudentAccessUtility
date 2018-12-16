
import sys
from os import scandir
from os import path
from os import system
from distutils.dir_util import copy_tree
from distutils.errors import DistutilsFileError

home = path.expanduser("~")

windows = path.normpath(str(home)+"/AppData/Local/Google/Chrome/User Data");
osx = path.normpath(str(home)+"/Library/Application Support/Google/Chrome");
src = None

if path.isdir(windows):
    src = windows
elif path.isdir(osx):
    src = osx
else:
    print("No profile directory found in default location.")
    while (src == None):
        customPathString = input("Enter the path: ")
        custom = path.normpath(customPathString)
        if path.isdir(custom):
            src = custom
        else:
            print("Not a valid path.")

print("COPYING FROM:")
print(str(src))

print("TO DESTINATION:")
if getattr(sys, 'frozen', False): #frozen when running as exe
    dst = path.dirname(sys.executable)
else:
    dst = path.dirname(path.abspath(__file__))
dst = path.normpath(str(dst)+"/Automation")

print(str(dst))

def folder_size(path='.'):
    total = 0
    for entry in scandir(path):
        if entry.is_file():
            total += entry.stat().st_size
        elif entry.is_dir():
            total += folder_size(entry.path)
    return total

size = folder_size(src)
size = round(size / 2**20, 0)
print("\nAll Google Chrome processes will be ended completely before starting.")
print("Do not reopen Chrome until the process is complete.")
print("This takes about 5-10 minutes, depending on the size of the directory.")
print("Required disk space for copy: "+str(size)+" MB")
yes = input("Are you sure you want to continue? Type \'yes\' and press ENTER: ")
if yes.lower() != "yes":
    input("Process aborted. Press ENTER to finish")
    sys.exit();
    
system("taskkill /F /IM chrome.exe")
print("\nStarting copy process...")
print("Do not open Google Chrome.")
print("Close to abort.")
print("Running...")


result = copy_tree(str(src), str(dst))
print(result)

print("Successfully copied profile directory.\n")
input("Press ENTER to finish")
