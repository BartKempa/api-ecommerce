insert into
    users(email, password, first_name, last_name,  phone_number, creation_date, cart_id)
values
    ('admin@mail.com', '{noop}adminpass', 'Bartek', 'Kowalski', '506123456', '2024-02-14T11:33:54.478613800', null),
    ('user@mail.com', '{noop}Userpass123!@#', 'Janek', 'Janecki', '506111222', '2024-02-15T11:33:54.478613800', '2'),
    ('secondUser@mail.com', '{noop}userpass2', 'Daga', 'Szczepankowa', '503123132', '2024-02-16T11:33:54.478613800', '3'),
    ('thirdUser@mail.com', '{noop}userpass3', 'Anna', 'Kowalska', '504121212', '2024-02-17T11:33:54.478613800', '4'),
    ('fourthUser@mail.com', '{noop}userpass4', 'Jola', 'Fifi', '502112233', '2024-02-18T11:33:54.478613800', '5'),
    ('fifthUser@mail.com', '{noop}userpass5', 'Olo', 'Serek', '501501501', '2024-02-19T11:33:54.478613800', '6'),
    ('sixthUser@mail.com', '{noop}userpass6', 'Adam', 'Adamski', '508508508', '2024-02-20T11:33:54.478613800', '7'),
    ('seventhUser@mail.com', '{noop}userpass7', 'Darek', 'Darecki', '606606606', '2024-02-21T11:33:54.478613800', '1'),
    ('eighthUser@mail.com', '{noop}userpass8', 'Piotr', 'Piotrula', '666666666', '2024-02-22T11:33:54.478613800', null);


insert into
    user_role(name, description)
values
    ('ADMIN', 'pełne uprawnienia'),
    ('USER', 'podstawowe uprawnienia, możliwość przeglądania i kupowania produktów');

insert into
    user_roles(user_id, role_id)
values
    (1, 1),
    (2, 2),
    (3, 2),
    (4, 2),
    (5, 2),
    (6, 2),
    (7, 2),
    (8, 2);
