UPDATE sites SET traffic_estimate = '0_TO_1MM' WHERE traffic_estimate <> '_1M_PLUS';
UPDATE sites SET traffic_estimate = '_1MM_TO_5MM' WHERE traffic_estimate = '_1M_PLUS';