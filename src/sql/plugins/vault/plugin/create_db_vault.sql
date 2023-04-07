
--
-- Structure for table vault_application
--

DROP TABLE IF EXISTS vault_application;
CREATE TABLE vault_application (
id_application int AUTO_INCREMENT,
name varchar(50) default '' NOT NULL,
code varchar(50) default '' NOT NULL,
PRIMARY KEY (id_application)
);

--
-- Structure for table vault_environnement
--

DROP TABLE IF EXISTS vault_environnement;
CREATE TABLE vault_environnement (
id_environnement int AUTO_INCREMENT,
code varchar(50) default '' NOT NULL,
token varchar(50) default '' NOT NULL,
idapplication int default '0' NOT NULL,
PRIMARY KEY (id_environnement)
);

--
-- Structure for table vault_properties
--

DROP TABLE IF EXISTS vault_properties;
CREATE TABLE vault_properties (
id_properties int AUTO_INCREMENT,
key varchar(50) default '' NOT NULL,
value varchar(50) default '' NOT NULL,
idenvironnement int default '0' NOT NULL,
PRIMARY KEY (id_properties)
);
