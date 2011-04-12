#!/bin/sh
#
# NAME
#      $RCSfile: run_all_getbulk.sh,v $
# DESCRIPTION
#      [given below]
# DELTA
#      $Revision: 1.2 $
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

files=get_bulk*.properties

for i in ${files} ; do
  echo $i
  class=uk.co.westhawk.examplev2c.get_bulk
  ${JAVA_HOME}/bin/java ${OPTIONS} -classpath ${CLASSPATH} ${class} $i
  read a
done

