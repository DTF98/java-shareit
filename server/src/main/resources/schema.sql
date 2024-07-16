DROP TABLE IF EXISTS comments, bookings, items, users, requests;
CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name  VARCHAR(255)                        NOT NULL,
    email VARCHAR(512)                        NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name        VARCHAR(255)                        NOT NULL,
    description VARCHAR(512)                        NOT NULL,
    available   BOOLEAN                             NOT NULL,
    owner_id    BIGINT                              NOT NULL,
    request_id  BIGINT,

    PRIMARY KEY (id),
    FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    item_id    BIGINT                              NOT NULL,
    status     VARCHAR(255)                        NOT NULL,
    booker_id  BIGINT                              NOT NULL,
    date_start TIMESTAMP WITHOUT TIME ZONE         NOT NULL,
    date_end   TIMESTAMP WITHOUT TIME ZONE         NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    FOREIGN KEY (booker_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    text      VARCHAR(255)                        NOT NULL,
    item_id   BIGINT                              NOT NULL,
    author_id BIGINT                              NOT NULL,
    created   TIMESTAMP WITHOUT TIME ZONE         NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    description VARCHAR(255)                        NOT NULL,
    user_id     BIGINT                              NOT NULL,
    created     TIMESTAMP WITHOUT TIME ZONE         NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);