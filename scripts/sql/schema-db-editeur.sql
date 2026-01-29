-- Table jeu
CREATE TABLE jeu (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    datepublication DATE,
    genre VARCHAR[] NOT NULL,
    supports VARCHAR[] NOT NULL,
    versionpubliee VARCHAR(100) NOT NULL,
    versioncourante VARCHAR(100) NOT NULL,
    isdlc BOOLEAN,
    prix INT NOT NULL,
    enpublication BOOLEAN,
    idediteur INT NOT NULL,
    idparent INT
);

-- Table evaluation
CREATE TABLE evaluation (
    id SERIAL PRIMARY KEY,
    idjeu INT REFERENCES jeu(id) ON DELETE SET NULL,
    versionjeuevaluee VARCHAR(100) NOT NULL,
    estdlc BOOLEAN,
    note INT,
    commentaire VARCHAR(100),
    dateeval BIGINT
);

-- Table crash
CREATE TABLE crash (
    id SERIAL PRIMARY KEY,
    idjeu INT REFERENCES jeu(id) ON DELETE SET NULL,
    idsession INT NOT NULL,
    versionjeuconcernee VARCHAR(100) NOT NULL,
    codeerreur INT,
    datecrash BIGINT
);

-- Table patch
CREATE TABLE patch (
    id SERIAL PRIMARY KEY,
    idjeu INT REFERENCES jeu(id) ON DELETE SET NULL,
    idcrash INT REFERENCES crash(id) ON DELETE SET NULL,
    versionproblematique VARCHAR(120),
    versioncible VARCHAR(120) NOT NULL,
    datepublication BIGINT,
    causepatch VARCHAR(120)
);

-- Inserts dans Jeu
INSERT INTO Jeu (nom,  datePublication, genre,supports, versionPubliee, versionCourante, isDLC, prix, enPublication, idEditeur, idParent)
VALUES
('Elden Ring',  '2022-02-25', ARRAY['RPG','Action'],ARRAY['PC','SWITCH'], '1.0', '1.10', false, 60, true, 0, NULL),
('The Witcher 3', '2015-05-19', ARRAY['RPG','Open World'], ARRAY['PC','SWITCH'],'1.0', '4.04', false, 40, false, 0, NULL),
('Cyberpunk 2077 - Phantom Liberty', '2023-09-26', ARRAY['RPG'],ARRAY['PC','SWITCH'], '2.0', '2.1', true, 30, false, 0, 2);

-- Jeu pour l'éditeur 1000 (ShadowDev)
INSERT INTO Jeu (nom,  datePublication, genre,supports, versionPubliee, versionCourante, isDLC, prix, enPublication, idEditeur, idParent)
VALUES ('Cyber Quest',  '2026-01-10', ARRAY['RPG', 'Cyberpunk'],ARRAY['PC','SWITCH'], '1.0', '1.0.1', false, 45, true, 1000, NULL);

-- Jeu pour l'éditeur 1001 (PixelMaster)
INSERT INTO Jeu (nom,  datePublication, genre,supports, versionPubliee, versionCourante, isDLC, prix, enPublication, idEditeur, idParent)
VALUES ('Pixel Odyssey', '2025-11-20', ARRAY['Platformer', 'Adventure'],ARRAY['PC','SWITCH'], '1.0', '1.2', false, 20, true, 1001, NULL);

-- Jeu pour l'éditeur 1002 (GameMaker99)
INSERT INTO Jeu (nom,  datePublication, genre,supports, versionPubliee, versionCourante, isDLC, prix, enPublication, idEditeur, idParent)
VALUES ('Space Survival', '2026-01-15', ARRAY['Survival', 'Sci-Fi'],ARRAY['PC','SWITCH'], '1.0', '1.0', false, 35, true, 1002, NULL);

-- Jeu pour l'éditeur 1003 (IndieHero)
INSERT INTO Jeu (nom,  datePublication, genre,supports, versionPubliee, versionCourante, isDLC, prix, enPublication, idEditeur, idParent)
VALUES ('Forest Spirit',  '2024-05-12', ARRAY['Indie', 'Relaxing'],ARRAY['PC','SWITCH'], '1.0', '2.0', false, 15, true, 1003, NULL);

-- Jeu pour l'éditeur 1004 (BugHunter)
INSERT INTO Jeu (nom,  datePublication, genre,supports, versionPubliee, versionCourante, isDLC, prix, enPublication, idEditeur, idParent)
VALUES ('Medieval Siege',  '2026-01-01', ARRAY['Strategy', 'History'],ARRAY['PC','SWITCH'], '1.0', '1.1', false, 55, true, 1004, NULL);


INSERT INTO Jeu (nom, datePublication, genre,supports, versionPubliee, versionCourante, isDLC, prix, enPublication, idEditeur, idParent)
VALUES ('Pixel Odyssey: Lost Levels',  '2026-02-01', ARRAY['Platformer'],ARRAY['PC','SWITCH'], '1.0', '1.0', true, 10, true, 1001, 2);

-- Inserts dans Evaluation
INSERT INTO Evaluation (idJeu, versionJeuEvaluee, estDLC, note, commentaire, dateEval)
VALUES
(1, '1.09', false, 5, 'Incroyable expérience', 1700000000000),
(1, '1.10', false, 4, 'Très bon mais difficile', 1700500000000),
(2, '4.03', false, 5, 'Chef-d’œuvre intemporel', 1701000000000),
(2, '4.04', false, 5, 'Toujours aussi bon', 1701500000000),
(3, '2.0', true, 4, 'Excellent DLC', 1702000000000),
(3, '2.1', true, 5, 'Très grosse amélioration', 1702500000000),
(1, '1.0', false, 5, 'Une surprise totale, j adore', 1706000000000),
(1, '1.0', false, 5, 'Graphismes époustouflants', 1706500000000),
(3, '1.1', false, 4, 'Prometteur pour la suite', 1707000000000),
(2, '1.0', false, 5, 'Une pépite indépendante !', 1708000000000);



