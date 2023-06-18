--liquibase formatted sql
--changeset <nikitos>:<create-roles-table>

    CREATE TABLE IF NOT EXISTS roles (
        id bigint auto_increment primary key not null ,
        role_name varchar(255) not null
    );
--DROP TABLE roles;
