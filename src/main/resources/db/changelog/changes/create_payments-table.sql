--liquibase formatted sql
--changeset <nikitos>:<create-payments-table>

CREATE TABLE IF NOT EXISTS payments (
    id bigint AUTO_INCREMENT primary key not null,
    url varchar(500),
    session_id varchar(255),
    payment_amount decimal(10, 2) not null,
    rental_id bigint not null,
    status varchar(255) not null,
    type varchar(255) not null,
    FOREIGN KEY (rental_id) references rentals(id)
);

--DROP TABLE payments;
