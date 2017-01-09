
ALTER TABLE audit_log CHANGE COLUMN description description varchar (1024);
ALTER TABLE audit_log CHANGE COLUMN comment_msg comment_msg varchar (1024);