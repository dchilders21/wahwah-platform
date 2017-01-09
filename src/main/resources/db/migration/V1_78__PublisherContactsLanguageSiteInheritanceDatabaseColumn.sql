ALTER TABLE sites ADD inherit_pub_details BIT(1) DEFAULT TRUE;

UPDATE sites AS s INNER JOIN accounts AS a ON s.account_id = a.id SET s.inherit_pub_details = ( a.contact_email<=>s.contact_email AND a.contact_name<=>s.contact_name AND a.country<=>s.site_country AND a.language<=>s.site_language );