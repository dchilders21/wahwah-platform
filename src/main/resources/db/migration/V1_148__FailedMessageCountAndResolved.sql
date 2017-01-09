
ALTER TABLE message_failure_log ADD COLUMN failure_count INT NOT NULL;
ALTER TABLE message_failure_log ADD COLUMN resolved BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE message_failure_log ADD COLUMN entry_time_last DATETIME NOT NULL;
ALTER TABLE message_failure_log ADD COLUMN entry_time_resolved DATETIME NULL;
