--liquibase formatted sql
--changeset <nikitos>:<create-rentals-table>
CREATE TABLE IF NOT EXISTS rentals
(
    id          bigint auto_increment primary key not null,
    return_date datetime not null,
    rental_date datetime not null,
    actual_date datetime,
    car_id bigint NOT NULL,
    user_id bigint NOT NULL,
    FOREIGN KEY (car_id) references cars (id),
    FOREIGN KEY (user_id) references users (id)
);

--DROP TABLE rentals;
