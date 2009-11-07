#!/usr/bin/env jython

'''
Created on Oct 31, 2009

@author: klulrich
'''
from com.danet.ulrich.pictures import MainFrame, Element
from java.io import File
import sys

for i in Element.readElements(File("C:\\pictures")):
    print i

m = MainFrame(sys.argv[1])
