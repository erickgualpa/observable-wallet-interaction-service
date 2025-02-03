CREATE TABLE wallet (
    id INT AUTO_INCREMENT PRIMARY KEY,
    entity_id CHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    UNIQUE KEY uk_wallet_entity_id(entity_id)
);