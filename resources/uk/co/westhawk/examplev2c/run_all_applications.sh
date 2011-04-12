#!/bin/sh
#
# NAME
#      $RCSfile: run_all_applications.sh,v $
# DESCRIPTION
#      [given below]
# DELTA
#      $Revision: 1.3 $
#      $Author: birgit $
# CREATED
#      $Date: 2005/02/22 10:25:03 $
# COPYRIGHT
#      Westhawk Ltd
# TO DO
#

JAVA_HOME=/opt/jdk1.4.1

ROOT=/project/snmp/working/birgit
SOURCE=${ROOT}/classes
TABLE=${ROOT}/lib/tablelayout.jar
SNMPJAR=${ROOT}/lib/snmp4_14.jar

CLASSPATH=${TABLE}:${SNMPJAR}:${SOURCE}:.
echo CLASSPATH ${CLASSPATH}

OPTIONS=
OPTIONS=-Djava.compiler=NONE

files=`grep -lw main *.java`

for i in ${files} ; do
  class1=`echo $i | sed "s/.java//"`
  class2=uk.co.westhawk.examplev2c.$class1
  echo $class2
  ${JAVA_HOME}/bin/java ${OPTIONS} -classpath ${CLASSPATH} ${class2}
  read a
done

