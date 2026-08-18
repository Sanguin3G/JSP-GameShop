-- GameShop local database
--
-- This file is deliberately small and matches the tables used by the
-- legacy DAO. DBContext runs it automatically the first time the app starts.

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS category (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    description TEXT NOT NULL DEFAULT '',
    display_order INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS account (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE COLLATE NOCASE,
    pass TEXT NOT NULL,
    role_id INTEGER NOT NULL DEFAULT 2 CHECK (role_id IN (1, 2))
);

CREATE TABLE IF NOT EXISTS product (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    image_url TEXT NOT NULL,
    price REAL NOT NULL DEFAULT 0 CHECK (price >= 0),
    description TEXT NOT NULL DEFAULT '',
    category_id INTEGER NOT NULL,
    release_date TEXT NOT NULL,
    rating REAL NOT NULL DEFAULT 0 CHECK (rating >= 0 AND rating <= 5),
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS orders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id INTEGER NOT NULL,
    total_amount REAL NOT NULL CHECK (total_amount >= 0),
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS order_item (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    price REAL NOT NULL CHECK (price >= 0),
    subtotal REAL NOT NULL CHECK (subtotal >= 0),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_product_category ON product(category_id);
CREATE INDEX IF NOT EXISTS idx_product_name ON product(name);
CREATE INDEX IF NOT EXISTS idx_order_account ON orders(account_id);

-- Demo accounts use the plain-text password convention of the original
-- project so the existing legacy login form remains compatible.
INSERT OR IGNORE INTO account (id, username, pass, role_id) VALUES
    (1, 'admin@gamestore.com', 'admin123', 1),
    (2, 'maya.chen', 'gamer123', 2),
    (3, 'alex.rivera', 'gamer123', 2),
    (4, 'noah.wilson', 'gamer123', 2);

INSERT OR IGNORE INTO category (id, name, description, display_order) VALUES
    (1, 'Action', 'Fast combat, dramatic set pieces, and plenty of momentum.', 1),
    (2, 'Role-playing', 'Character-driven adventures with choices, builds, and progression.', 2),
    (3, 'Adventure', 'Exploration-first games built around discovery and story.', 3),
    (4, 'Strategy', 'Careful planning, clever systems, and long-term decisions.', 4),
    (5, 'Horror', 'Unsettling worlds, survival pressure, and stories that bite back.', 5),
    (6, 'Racing', 'Competitive driving from realistic circuits to impossible roads.', 6),
    (7, 'Simulation', 'Relaxing or demanding sandboxes that model a world of their own.', 7),
    (8, 'Indie', 'Distinctive smaller-scale games with strong creative identities.', 8);

INSERT OR IGNORE INTO product
    (id, name, image_url, price, description, category_id, release_date, rating) VALUES
    (1, 'The Witcher 3: Wild Hunt', 'https://cdn.cloudflare.steamstatic.com/steam/apps/292030/header.jpg', 39.99,
        'Geralt of Rivia searches a war-torn continent for Ciri in a huge fantasy RPG filled with memorable contracts, difficult choices, and stories worth getting lost in.',
        2, '2015-05-18', 4.9),
    (2, 'Cyberpunk 2077', 'https://cdn.cloudflare.steamstatic.com/steam/apps/1091500/header.jpg', 49.99,
        'Build V into a mercenary with a personal code, then take on Night City jobs where every shortcut has a cost and every district has its own pulse.',
        1, '2020-12-10', 4.4),
    (3, 'Red Dead Redemption 2', 'https://cdn.cloudflare.steamstatic.com/steam/apps/1174180/header.jpg', 59.99,
        'Ride with the Van der Linde gang through a beautifully detailed 1899 America as loyalty, survival, and the end of the outlaw era collide.',
        3, '2019-11-05', 4.8),
    (4, 'Elden Ring', 'https://cdn.cloudflare.steamstatic.com/steam/apps/1245620/header.jpg', 59.99,
        'Explore the Lands Between at your own pace, combine strange weapons and spells, and face bosses that make “one more attempt” a dangerous promise.',
        2, '2022-02-25', 4.9),
    (5, 'Baldur''s Gate 3', 'https://cdn.cloudflare.steamstatic.com/steam/apps/1086940/header.jpg', 59.99,
        'Gather an unpredictable party, shape a story through genuinely consequential choices, and survive a cinematic fantasy adventure with friends or alone.',
        2, '2023-08-03', 4.9),
    (6, 'Hades', 'https://cdn.cloudflare.steamstatic.com/steam/apps/1145360/header.jpg', 24.99,
        'Battle out of the Underworld in quick, satisfying runs where new powers, sharp dialogue, and a surprisingly tender family drama keep pulling you back.',
        8, '2020-09-17', 4.8),
    (7, 'Hollow Knight', 'https://cdn.cloudflare.steamstatic.com/steam/apps/367520/header.jpg', 14.99,
        'Descend into the ruined kingdom of Hallownest for precise 2D combat, melancholy secrets, and an insect-sized world with enormous atmosphere.',
        8, '2017-02-24', 4.8),
    (8, 'Resident Evil Village', 'https://cdn.cloudflare.steamstatic.com/steam/apps/1196590/header.jpg', 39.99,
        'Ethan Winters enters a remote European village where every route forward reveals a new kind of nightmare and ammunition is never quite enough.',
        5, '2021-05-07', 4.6),
    (9, 'DOOM Eternal', 'https://cdn.cloudflare.steamstatic.com/steam/apps/782330/header.jpg', 39.99,
        'Rip and tear through Hell with aggressive movement, heavy weapons, and a combat loop that rewards keeping the pressure on.',
        1, '2020-03-20', 4.7),
    (10, 'Portal 2', 'https://cdn.cloudflare.steamstatic.com/steam/apps/620/header.jpg', 9.99,
        'Solve increasingly devious physics puzzles with a portal gun, a dryly funny cast, and one of the most dependable co-op campaigns around.',
        3, '2011-04-18', 4.9),
    (11, 'Sid Meier''s Civilization VI', 'https://cdn.cloudflare.steamstatic.com/steam/apps/289070/header.jpg', 29.99,
        'Lead a civilization from its first settlement to the space age, balancing diplomacy, science, culture, and the occasional extremely avoidable war.',
        4, '2016-10-20', 4.5),
    (12, 'Forza Horizon 5', 'https://cdn.cloudflare.steamstatic.com/steam/apps/1551360/header.jpg', 59.99,
        'Race across a vibrant Mexican landscape in a huge open festival packed with road cars, dirt trails, stunt events, and an alarming number of barn finds.',
        6, '2021-11-09', 4.6),
    (13, 'Microsoft Flight Simulator', 'https://cdn.cloudflare.steamstatic.com/steam/apps/1250410/header.jpg', 69.99,
        'Take to the skies in aircraft ranging from light trainers to airliners and explore a richly rendered planet from the cockpit.',
        7, '2020-08-18', 4.3),
    (14, 'Stardew Valley', 'https://cdn.cloudflare.steamstatic.com/steam/apps/413150/header.jpg', 14.99,
        'Restore an inherited farm, meet the locals, and decide whether your ideal evening involves fishing, mining, or aggressively reorganizing a chest.',
        7, '2016-02-26', 4.9),
    (15, 'Disco Elysium - The Final Cut', 'https://cdn.cloudflare.steamstatic.com/steam/apps/632470/header.jpg', 29.99,
        'Play a detective with a broken past and an unreliable inner committee in a richly written investigation where conversation is the main battlefield.',
        2, '2019-10-15', 4.8),
    (16, 'It Takes Two', 'https://cdn.cloudflare.steamstatic.com/steam/apps/1426210/header.jpg', 39.99,
        'Two players guide a pair of reluctant tiny heroes through a constantly changing co-op adventure built around teamwork and inventive surprises.',
        3, '2021-03-26', 4.7),
    (17, 'Terraria', 'https://cdn.cloudflare.steamstatic.com/steam/apps/105600/header.jpg', 9.99,
        'Dig, build, craft, and fight across a colorful 2D sandbox where a quiet cabin and a boss arena are equally reasonable construction projects.',
        8, '2011-05-16', 4.8),
    (18, 'Sekiro: Shadows Die Twice', 'https://cdn.cloudflare.steamstatic.com/steam/apps/814380/header.jpg', 59.99,
        'Master sharp parries and vertical movement as a shinobi protects his young lord in a focused action adventure with no interest in your excuses.',
        1, '2019-03-22', 4.7);
