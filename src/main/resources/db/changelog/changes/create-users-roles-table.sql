--liquibase formatted sql
--changeset <nikitos>:<create-users_roles-table>

    CREATE TABLE IF NOT EXISTS users_roles (
        user_id bigint,
        role_id bigint,
        CONSTRAINT fk_users_roles_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
        CONSTRAINT fk_users_roles_role_id FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
    );
--DROP TABLE users_roles;
