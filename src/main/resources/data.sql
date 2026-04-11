INSERT INTO privileges (id, privilege) VALUES
                                      (1, 'WRITE_EVENT'),
                                      (2, 'READ_EVENT'),
                                      (3, 'DELETE_EVENT'),
                                      (4, 'UPDATE_EVENT');

INSERT INTO roles (id, role) VALUES
                                 (1, 'ADMIN'),
                                 (2, 'ORGANIZER'),
                                 (3, 'USER');


-- ADMIN (Privileges 1, 2, 3, 4)
INSERT INTO role_privilege (role_id, privilege_id) VALUES
                                                         (1, 1), (1, 2), (1, 3), (1, 4);

-- ORGANIZER (Privileges 1, 2, 4)
INSERT INTO role_privilege (role_id, privilege_id) VALUES
                                                         (2, 1), (2, 2), (2, 4);

-- USER (Privilege 2)
INSERT INTO role_privilege (role_id, privilege_id) VALUES
    (3, 2);


INSERT INTO users (id, email, first_name, last_name, phone_number, password, username)
VALUES ('b0292a37-46af-4f6f-b1a9-82c236233181', 'admin@event.com', 'Liban', 'Abdullahi', '+358469098471', '$2a$12$B0pF3MbSOThpNf7vlraCRe6Im.j8MhGyj9qDfRAIgirGMnT2pOjx.', 'liban');

INSERT INTO user_role (user_id, role_id) VALUES ('b0292a37-46af-4f6f-b1a9-82c236233181', 1);