
--
-- Structure for table vault_application
--

DROP TABLE IF EXISTS vault_application;
CREATE TABLE `vault_application` (
 `id_application` int NOT NULL AUTO_INCREMENT,
 `name` varchar(50) COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '',
 `code` varchar(50) COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '',
 PRIMARY KEY (`id_application`)
);

--
-- Structure for table vault_environnement
--

DROP TABLE IF EXISTS vault_environnement;
CREATE TABLE `vault_environnement` (
`id_environnement` int NOT NULL AUTO_INCREMENT,
`code` varchar(50) COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '',
`idapplication` int NOT NULL DEFAULT '0',
`type` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
PRIMARY KEY (`id_environnement`)
);

--
-- Structure for table vault_properties
--

DROP TABLE IF EXISTS vault_properties;
CREATE TABLE `vault_properties` (
`id_properties` int NOT NULL AUTO_INCREMENT,
`labelkey` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '',
`value` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '',
`idenvironnement` int NOT NULL DEFAULT '0',
PRIMARY KEY (`id_properties`)
);
