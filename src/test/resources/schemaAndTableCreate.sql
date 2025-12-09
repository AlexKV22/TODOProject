CREATE SCHEMA if not exists todo_project;

CREATE TABLE IF NOT EXISTS todo_project.tasks (
                                     id BIGSERIAL PRIMARY KEY,
                                     title VARCHAR(200) NOT NULL,
                                     created_at TIMESTAMP NOT NULL,
                                     expire_at DATE,
                                     status VARCHAR(255) NOT NULL
);