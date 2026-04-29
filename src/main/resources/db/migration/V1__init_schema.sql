CREATE TABLE bank_stock (
    name VARCHAR(255) PRIMARY KEY,
    quantity INT NOT NULL DEFAULT 0
);

CREATE TABLE wallet_stock (
    wallet_id VARCHAR(255) NOT NULL,
    stock_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    PRIMARY KEY (wallet_id, stock_name)
);

CREATE TABLE audit_log_entry (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(10) NOT NULL,
    wallet_id VARCHAR(255) NOT NULL,
    stock_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_wallet_stock_wallet_id ON wallet_stock(wallet_id);
CREATE INDEX idx_audit_log_created_at ON audit_log_entry(id);
