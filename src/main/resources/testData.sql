-- Вставка учебного заведения
INSERT INTO educational_institution (id, name, domen_name, created_at)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'Test University', 'test.edu', NOW())
ON CONFLICT (id) DO NOTHING;

-- Вставка обычного пользователя
INSERT INTO account (
    id, educational_institution_id, nickname, first_name, last_name, email,
    is_professor, is_admin, is_visible, is_consulting, rating, course_number, ip, created_at,
    is_accepted_privacy_policy, is_accepted_personal_data_processing
) VALUES (
             '00000000-0000-0000-0000-000000000010',
             '00000000-0000-0000-0000-000000000001',
             'test_user', 'Ivan', 'Ivanov', 'user@test.edu',
             false, false, true, false, 0.0, 0, '1.0', NOW(), true, true
         )
ON CONFLICT (id) DO NOTHING;

-- Вставка администратора
INSERT INTO account (
    id, educational_institution_id, nickname, first_name, last_name, email,
    is_professor, is_admin, is_visible, is_consulting, rating, course_number, ip, created_at,
    is_accepted_privacy_policy, is_accepted_personal_data_processing
) VALUES (
             '00000000-0000-0000-0000-000000000011',
             '00000000-0000-0000-0000-000000000001',
             'admin_user', 'Petr', 'Petrov', 'admin@test.edu',
             false, true, true, false, 0.0, 0,'1.1', NOW(), true, true
         )
ON CONFLICT (id) DO NOTHING;
