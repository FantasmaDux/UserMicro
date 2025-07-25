-- Создание схем (в случае запуска с пустой БД)
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS networking;

-- Выбор схемы для всех дальнейших операций
SET search_path TO networking;

-- Вставка учебного заведения
INSERT INTO educational_institution (id, name, domen_name, created_at)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'Test University', 'test.edu', NOW());

-- Вставка обычного пользователя
INSERT INTO account (
    id, educational_institution_id, nickname, first_name, last_name, email,
    is_professor, is_admin, is_visible, is_consulting, rating, course_number, created_at
) VALUES (
             '00000000-0000-0000-0000-000000000010',
             '00000000-0000-0000-0000-000000000001',
             'test_user', 'Ivan', 'Ivanov', 'user@test.edu',
             false, false, true, false, 0.0, 0, NOW()
         );

-- Вставка администратора
INSERT INTO account (
    id, educational_institution_id, nickname, first_name, last_name, email,
    is_professor, is_admin, is_visible, is_consulting, rating, course_number, created_at
) VALUES (
             '00000000-0000-0000-0000-000000000011',
             '00000000-0000-0000-0000-000000000001',
             'admin_user', 'Petr', 'Petrov', 'admin@test.edu',
             false, true, true, false, 0.0, 0,NOW()
         );
