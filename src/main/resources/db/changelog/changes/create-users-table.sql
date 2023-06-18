--liquibase formatted sql
--changeset <nikitos>:<create-users-table>
    CREATE TABLE IF NOT EXISTS users (
        id bigint auto_increment primary key not null,
        email varchar(255) not null unique,
        first_name varchar(255) not null,
        last_name varchar(255) not null,
        password varchar(255) not null,
        chat_id bigint unique
    );
--DROP TABLE users;
