CREATE TABLE IF NOT EXISTS crypto_data_table(
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(255) NOT NULL,
    private_key VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL
);