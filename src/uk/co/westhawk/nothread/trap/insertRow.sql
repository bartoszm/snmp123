rem
rem NAME
rem      $RCSfile: insertRow.sql,v $
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
rem USAGE
rem

rem
rem If you are running XSQL, you can try xsql/insert.html as well.
rem Much more fun. It uses the 'demo' connection.
rem

rem VARIABLE theResult VARCHAR2(500);
rem CALL scott.TrapTest.send('localhost', 162, 'public', 100, '1.3.6.1.6.3.1.1.5.1')
rem INTO :theResult;
rem PRINT theResult;     


INSERT INTO scott.Trap 
    (hostname, port, community, sysUpTime, snmpTrapOID)
VALUES
    ('localhost', 162, 'public', 100, '1.3.6.1.6.3.1.1.5.1')
/


select * 
  from SCOTT.trap 
 order by insertdate
/

