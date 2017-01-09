UPDATE wahwahplatform.ad_units n1, wahwahplatform.ad_units n2
SET n1.external_id2 = n2.external_id2
WHERE n1.external_id = n2.external_id AND n1.external_id2 IS NULL AND n2.external_id2 IS NOT NULL;

