UPDATE sites AS s INNER JOIN accounts AS a ON s.account_id = a.id SET  s.contact_email=a.contact_email,s.contact_name=a.contact_name,s.site_country=a.country,s.site_language=a.language WHERE s.inherit_pub_details = TRUE