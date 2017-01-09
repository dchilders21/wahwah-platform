ALTER TABLE demand_sources ADD COLUMN summary_impressions BIGINT NOT NULL DEFAULT 0;
ALTER TABLE demand_sources ADD COLUMN summary_fillrate INT NOT NULL DEFAULT 0;
ALTER TABLE demand_sources ADD COLUMN summary_ecpm INT NOT NULL DEFAULT 0;
ALTER TABLE demand_sources ADD COLUMN summary_rcpm INT NOT NULL DEFAULT 0;