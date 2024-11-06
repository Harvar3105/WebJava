INSERT INTO users (userName, password, enabled, firstName) VALUES
    ('user', '$2a$10$C9v79cpZKXoFcULjH3OtGepMWKpLYi/eScnaNzkoSjnHROGLpzjS6', true, 'User First Name'),
    ('admin', '$2a$10$JBmLzHsD1c8XdFF322vWlu14qTWTqFwNEflSPAbYpJxhb081iozQm', true, 'Admin First Name');

INSERT INTO authorities (user_id, authorityName) VALUES
    ((SELECT id FROM USERS WHERE userName = 'user'), 'ROLE_USER'),
    ((SELECT id FROM USERS WHERE userName = 'admin'), 'ROLE_USER'),
    ((SELECT id FROM USERS WHERE userName = 'admin'), 'ROLE_ADMIN');
