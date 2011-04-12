rem
rem NAME
rem      $RCSfile: checkStoredFunc.sql,v $
rem DESCRIPTION
rem      [given below]
rem DELTA
rem      $Revision: 1.2 $
rem      $Author: birgit $
rem CREATED
rem      $Date: 2005/02/22 10:32:47 $
rem COPYRIGHT
rem      Westhawk Ltd
rem TO DO
rem

rem describe all_source;

SELECT LINE, TEXT
  FROM all_source
 WHERE OWNER = 'SCOTT'
   AND TYPE = 'PACKAGE BODY'
/


