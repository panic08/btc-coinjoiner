CREATE TABLE IF NOT EXISTS users_table(
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    registered_at VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_linked_socials_table(
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(255) NOT NULL,
    identifier VARCHAR(255) NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users_table(id)
);


