#!/bin/sh
#
# NAME
#      $RCSfile: run_all_applications.sh,v $
# DESCRIPTION
#      [given below]
# DELTA
#      $Revision: 1.2 $
#      $Author: birgit $
# CREATED
#      $Date: 2005/02/22 10:30:02 $
# COPYRIGHT
#      Westhawk Ltd
# TO DO
#


JAVA_HOME=/opt/jdk1.4.1

ROOT=/project/snmp/working/birgit
CLASSES=${ROOT}/classes
TABLE=${ROOT}/lib/tablelayout.jar
SNMPJAR=${ROOT}/lib/snmp4_14.jar

CLASSPATH=${TABLE}:${SNMPJAR}:${CLASSES}:.
echo CLASSPATH ${CLASSPATH}

OPTIONS=
OPTIONS=-Djava.compiler=NONE

for i in `grep -lw main *.java` ; do
  class1=`echo $i | sed "s/.java//"`
  class2=uk.co.westhawk.examplev1.$class1
  echo $class2
  ${JAVA_HOME}/bin/java ${OPTIONS} -classpath ${CLASSPATH} ${class2} ${class1}.properties
  read a
done

