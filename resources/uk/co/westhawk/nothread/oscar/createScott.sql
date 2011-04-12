rem
rem NAME
rem      $RCSfile: createScott.sql,v $
rem DESCRIPTION
rem      [given below]
rem DELTA
rem      $Revision: 1.1 $
rem      $Author: birgit $
rem CREATED
rem      $Date: 2006/02/06 12:51:12 $
rem COPYRIGHT
rem      Westhawk Ltd
rem TO DO
rem

declare
  done number;
begin
  done:=0;
  select count(username)
    into done
    from all_users
   where username = 'SCOTT';

  if (done=0) then
      execute immediate
        'CREATE USER scott
            IDENTIFIED BY tiger';
  end if;
end;
/

GRANT connect, resource, unlimited tablespace to scott
/

