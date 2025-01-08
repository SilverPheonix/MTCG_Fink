-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       coins INT DEFAULT 20,
                       elo NUMERIC DEFAULT 100,
                       games_played INT DEFAULT 0,
                       wins INT DEFAULT 0,
                       losses INT DEFAULT 0,
                       name VARCHAR(100),
                       bio TEXT,
                       image VARCHAR(255);
);
CREATE TABLE tokens (
                        token VARCHAR(255) PRIMARY KEY,
                        username VARCHAR(255) NOT NULL,
                        FOREIGN KEY (username) REFERENCES users(username)
);


CREATE TABLE cards (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       name VARCHAR(100) NOT NULL,
                       damage NUMERIC(10, 2) NOT NULL,
                       element_type VARCHAR(20) NOT NULL,
                       card_type VARCHAR(20) NOT NULL CHECK (card_type IN ('monster', 'spell')),
                       owner_id VARCHAR(100) REFERENCES users(username) ON DELETE SET NULL
);

CREATE TABLE packages (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          is_purchased BOOLEAN DEFAULT FALSE
);

CREATE TABLE package_cards (
                               package_id UUID REFERENCES packages(id) ON DELETE CASCADE,
                               card_id UUID REFERENCES cards(id) ON DELETE CASCADE,
                               PRIMARY KEY (package_id, card_id)
);

CREATE TABLE trades (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        offered_card_id UUID REFERENCES cards(id) ON DELETE CASCADE,
                        requirement_type VARCHAR(20) NOT NULL CHECK (requirement_type IN ('monster', 'spell')),
                        min_damage NUMERIC(10, 2) DEFAULT 0,
                        is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE battles (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         player_one_id UUID REFERENCES users(id),
                         player_two_id UUID REFERENCES users(id),
                         winner_id UUID REFERENCES users(id),
                         log TEXT,
                         start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE decks (
                       username VARCHAR(255) NOT NULL,  -- Store the username instead of user_id
                       card_id UUID REFERENCES cards(id) ON DELETE CASCADE,
                       PRIMARY KEY (username, card_id)  -- Make username and card_id the composite key
);


-- Indexes for better performance
CREATE INDEX idx_cards_owner_id ON cards(owner_id);
CREATE INDEX idx_battles_player_one_id ON battles(player_one_id);
CREATE INDEX idx_battles_player_two_id ON battles(player_two_id);