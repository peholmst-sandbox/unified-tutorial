INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (1, 1, 'Alpha Tech', 'https://www.alphatech.com', 'US', '1990-01-01');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (2, 1, 'Beta Solutions', 'https://www.betasolutions.com', 'GB', '1991-02-15');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (3, 1, 'Gamma Industries', 'https://www.gammaindustries.com', 'CA', '1992-03-10');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (4, 1, 'Delta Corp', 'https://www.deltacorp.com', 'AU', '1993-04-20');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (5, 1, 'Epsilon Enterprises', 'https://www.epsilonenterprises.com', 'NZ', '1994-05-25');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (6, 1, 'Zeta Global', 'https://www.zetaglobal.com', 'IN', '1995-06-30');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (7, 1, 'Eta Systems', 'https://www.etasystems.com', 'ZA', '1996-07-15');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (8, 1, 'Theta Tech', 'https://www.thetatech.com', 'JP', '1997-08-05');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (9, 1, 'Iota Consulting', 'https://www.iotaconsulting.com', 'FR', '1998-09-10');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (10, 1, 'Kappa Designs', 'https://www.kappadesigns.com', 'DE', '1999-10-20');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (11, 1, 'Lambda Logistics', 'https://www.lambdalogistics.com', 'ES', '2000-11-30');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (12, 1, 'Mu Manufacturing', 'https://www.mumanufacturing.com', 'BR', '2001-12-15');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (13, 1, 'Nu Networks', 'https://www.nunetworks.com', 'CN', '2002-01-20');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (14, 1, 'Xi Exports', 'https://www.xiexports.com', 'RU', '2003-02-25');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (15, 1, 'Omicron Outfits', 'https://www.omicronoutfits.com', 'NL', '2004-03-30');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (16, 1, 'Pi Productions', 'https://www.piproductions.com', 'KE', '2005-04-15');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (17, 1, 'Rho Retail', 'https://www.rhoretail.com', 'SE', '2006-05-05');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (18, 1, 'Sigma Services', 'https://www.sigmaservices.com', 'NO', '2007-06-10');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (19, 1, 'Tau Technologies', 'https://www.tautecnhologies.com', 'MX', '2008-07-20');
INSERT INTO customer (id, version, name, website, country, first_contact)
VALUES (20, 1, 'Upsilon Utilities', 'https://www.upsilonutilities.com', 'AE', '2009-08-25');

INSERT INTO industry (id, version, name)
VALUES (101, 1, 'Technology and Software');
INSERT INTO industry (id, version, name)
VALUES (102, 1, 'Healthcare and Pharmaceuticals');
INSERT INTO industry (id, version, name)
VALUES (103, 1, 'Automotive Manufacturing');
INSERT INTO industry (id, version, name)
VALUES (104, 1, 'Retail and Consumer Goods');
INSERT INTO industry (id, version, name)
VALUES (105, 1, 'Financial Services and Banking');
INSERT INTO industry (id, version, name)
VALUES (106, 1, 'Energy and Utilities');
INSERT INTO industry (id, version, name)
VALUES (107, 1, 'Telecommunications');
INSERT INTO industry (id, version, name)
VALUES (108, 1, 'Aerospace and Defense');
INSERT INTO industry (id, version, name)
VALUES (109, 1, 'Agriculture and Food Production');
INSERT INTO industry (id, version, name)
VALUES (110, 1, 'Construction and Real Estate');
INSERT INTO industry (id, version, name)
VALUES (111, 1, 'Education and Training');
INSERT INTO industry (id, version, name)
VALUES (112, 1, 'Hospitality and Tourism');
INSERT INTO industry (id, version, name)
VALUES (113, 1, 'Media and Entertainment');
INSERT INTO industry (id, version, name)
VALUES (114, 1, 'Professional Services');
INSERT INTO industry (id, version, name)
VALUES (115, 1, 'Transportation and Logistics');
INSERT INTO industry (id, version, name)
VALUES (116, 1, 'Chemicals and Materials');
INSERT INTO industry (id, version, name)
VALUES (117, 1, 'Textiles and Apparel');
INSERT INTO industry (id, version, name)
VALUES (118, 1, 'Environmental Services and Sustainability');
INSERT INTO industry (id, version, name)
VALUES (119, 1, 'Mining and Metals');
INSERT INTO industry (id, version, name)
VALUES (120, 1, 'Biotechnology and Medical Research');

INSERT INTO customer_industries (customer_id, industries_id)
VALUES (1, 101);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (2, 102);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (3, 103);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (4, 104);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (5, 105);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (6, 106);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (7, 107);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (8, 108);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (9, 109);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (10, 110);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (11, 111);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (12, 112);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (13, 113);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (14, 114);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (15, 115);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (16, 116);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (17, 117);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (18, 118);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (19, 119);
INSERT INTO customer_industries (customer_id, industries_id)
VALUES (20, 120);
