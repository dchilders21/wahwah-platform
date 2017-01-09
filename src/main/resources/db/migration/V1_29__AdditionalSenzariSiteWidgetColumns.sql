-- Senzari - monthly?
ALTER TABLE sites ADD
(
    traffic_estimate VARCHAR(255),
    site_country ENUM('UNITED_STATES', 'SPAIN', 'BRAZIL') NOT NULL DEFAULT 'UNITED_STATES'
);