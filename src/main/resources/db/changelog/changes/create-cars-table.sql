--liquibase formatted sql
--changeset <nikitos>:<create-cars-table>

CREATE TABLE IF NOT EXISTS cars
(
    id        bigint auto_increment primary key not null,
    model     varchar(255) not null,
    brand     varchar(255) not null,
    inventory int not null,
    daily_fee decimal(10, 2) not null,
    car_type   varchar(255) not null,
    is_deleted tinyint(1) not null default 0
);
--DROP TABLE cars;
