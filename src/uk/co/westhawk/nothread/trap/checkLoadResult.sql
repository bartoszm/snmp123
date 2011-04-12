rem
rem NAME
rem      $RCSfile: checkLoadResult.sql,v $
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

rem set hea off;
rem describe user_objects;

select OBJECT_NAME, 
       OBJECT_TYPE, 
       STATUS, 
       TIMESTAMP
  from user_objects
 where OBJECT_TYPE LIKE 'JAVA %'
   and object_name NOT LIKE 'SYS_%'
   and object_name NOT LIKE 'CREATE$%'
   and object_name NOT LIKE 'JAVA$%'
   and object_name NOT LIKE 'LOADLOB%'
   and status = 'INVALID'
/


