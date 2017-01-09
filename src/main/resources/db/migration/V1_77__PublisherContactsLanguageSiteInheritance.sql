ALTER TABLE accounts
  ADD contact_name VARCHAR(255),
  ADD contact_email VARCHAR(255),
  ADD language ENUM('en','es','pt') DEFAULT 'en',
  ADD country ENUM('UNITED_STATES', 'SPAIN', 'BRAZIL') DEFAULT 'UNITED_STATES';

UPDATE accounts AS a INNER JOIN sites AS s ON s.account_id = a.id SET a.contact_email=s.contact_email,a.contact_name=s.contact_name,a.country=s.site_country,a.language=s.site_language;