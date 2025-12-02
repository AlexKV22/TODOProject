CREATE TABLE IF NOT EXISTS tasks (
                      id BIGSERIAL PRIMARY KEY,
                      title VARCHAR(200) NOT NULL,
                      created_at TIMESTAMP NOT NULL,
                      expire_at DATE,
                      status VARCHAR(255) NOT NULL
);