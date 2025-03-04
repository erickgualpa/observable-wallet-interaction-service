CREATE TABLE deposit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    entity_id CHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    account INT NOT NULL,
    account_entity_id CHAR(36) NOT NULL,
    amount DOUBLE NOT NULL,
    UNIQUE KEY uk_deposit_entity_id(entity_id)
);