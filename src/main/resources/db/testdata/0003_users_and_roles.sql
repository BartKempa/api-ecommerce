insert into
    users(email, password, first_name, last_name,  phone_number, creation_date)
values
        ('admin@mail.com', '{noop}adminpass', 'Bartek', 'Kowalski', '506123456', '2024-02-14T11:33:54.478613800'),
    ('user@mail.com', '{noop}Userpass123!@#', 'Janek', 'Janecki', '506111222', '2024-02-14T11:33:54.478613800'),
    ('secondUser@mail.com', '{noop}userpass', 'Daga', 'Szczepankowa', '506112233', '2024-02-14T11:33:54.478613800');

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
    (3, 2);