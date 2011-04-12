rem
rem NAME
rem      $RCSfile: createStoredProc.sql,v $
rem DESCRIPTION
rem      [given below]
rem DELTA
rem      $Revision: 1.2 $
rem      $Author: birgit $
rem CREATED
rem      $Date: 2005/02/22 10:32:45 $
rem COPYRIGHT
rem      Westhawk Ltd
rem TO DO
rem

CREATE OR REPLACE PACKAGE scott.OscarTest IS
    FUNCTION oscar_quote
    RETURN VARCHAR2;
END;
/

CREATE OR REPLACE PACKAGE BODY scott.OscarTest IS
    FUNCTION oscar_quote
    RETURN VARCHAR2 AS
    LANGUAGE JAVA 
    NAME 'uk.co.westhawk.nothread.oscar.Oscar.quote() return java.lang.String';
END;
/


