#!/bin/sh
# Wrapper script to call MainFrame.py
# Call it like this:
# ./MainFrame.sh C:\\pictures

export CLASSPATH="../bin;../lib/forms_rt.jar"
./MainFrame.py $@
