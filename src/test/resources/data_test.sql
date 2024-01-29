delete from requests cascade;
delete from bookings cascade;
delete from items cascade;
delete from users cascade;

INSERT INTO users (name, email)
VALUES ('user1', 'mail@ya.ru'),
       ('user2', 'ya@mail.ru');

INSERT INTO requests (description, requestor_id, created_date)
VALUES ('request_body', 2, '2023-10-10 00:00:00'),
       ('request_des', 1, '2023-11-20 00:00:00'),
       ('description', 2, '2023-12-30 00:00:00');

INSERT INTO items (name, description, is_available, owner_id, request_id)
VALUES ('title', 'description', true, 1, 1),
       ('tit', 'specification', true, 2, 2),
       ('item_name', 'script', true, 1, 2);

INSERT INTO bookings (start_date, end_date, item_id, booker_id, status)
VALUES ('2023-10-10 00:00:00', '2023-10-30 00:00:00', 1, 2, 'WAITING'),
       ('2024-01-10 00:00:00', '2024-10-10 00:00:00', 2, 1, 'WAITING'),
       ('2024-10-10 00:00:00', '2024-12-20 00:00:00', 3, 2, 'WAITING');

INSERT INTO comments (text, item_id, author, created)
VALUES ('positive_comment', 1, 2, '2024-10-10 00:00:00');