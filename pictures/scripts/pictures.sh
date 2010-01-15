#!/bin/sh
#
# Usage: pictures.sh

# Default path for picture directory
#PICS=/local/digicam/pictures
PICS=/c/pictures

if [ "$1" != "" ]
then
	PICS=$1
fi

DELIMITER=":"
FORMS=../target/forms_rt-7.0.3.jar
PICTURES=../target/pictures-1.0-SNAPSHOT.jar
# set output directory for marked pictures
OUTDIR="/tmp/pics_out"
#DEBUG="true"

if [ `uname` = "CYGWIN_NT-5.1" ]
then
	DELIMITER=";"
	PICS=`cygpath -w $PICS`
	FORMS=`cygpath -w $FORMS`
	PICTURES=`cygpath -w $PICTURES`
fi

cmd="java -DDEBUG=${DEBUG} -DOUTDIR=${OUTDIR} -classpath ${FORMS}${DELIMITER}${PICTURES} com.ulrich.MainFrame $PICS"
echo $cmd
$cmd
