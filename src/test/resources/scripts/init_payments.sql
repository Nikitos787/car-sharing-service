INSERT INTO cars (id, model, brand, inventory, daily_fee, car_type, is_deleted) VALUES
(101, 'X6', 'BMW', 10, 100, 'SUV', false);

INSERT INTO users (id, email, first_name, last_name, password, chat_id) VALUES
    (101, 'user@i.ua', 'User', 'Userenko', '11111111', 122111);

INSERT INTO rentals (id, return_date, rental_date, actual_date, car_id, user_id)  VALUES
(101, '2023-01-02 00:30:31', '2023-01-01 00:30:31', '2023-01-02 00:20:31', 101, 101);

INSERT INTO payments (id, url, session_id, payment_amount, rental_id, status, type) VALUES
(101, 'google.com', 'sesionId12121', 1000, 101, 'PENDING', 'PAYMENT');
