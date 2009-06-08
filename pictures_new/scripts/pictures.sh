#!/bin/sh
#
# $Id: pictures.sh,v 1.4 2005/05/16 18:57:16 klaus Exp $

# Default path for picture directory
PICS=$HOME/pictures

if [ "$1" != "" ]
then
	PICS=$1
fi

DELIMITER=":"
FORMS=$HOME/lib/forms_rt.jar
PICTURES=$HOME/lib/pictures.jar

if [ `uname` = "CYGWIN_NT-5.1" ]
then
	DELIMITER=";"
	PICS=`cygpath -w $PICS`
	FORMS=`cygpath -w $FORMS`
	PICTURES=`cygpath -w $PICTURES`
fi

echo "java -classpath \"${FORMS}${DELIMITER}${PICTURES}\" com.danet.ulrich.pictures.MainFrame $PICS"
java -classpath "${FORMS}${DELIMITER}${PICTURES}" com.danet.ulrich.pictures.MainFrame $PICS
