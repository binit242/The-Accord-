MERGE INTO users (
  user_id,
  user_name,
  email,
  password,
  profile_pic,
  phone_number,
  gender,
  enabled,
  email_verified,
  phone_verified,
  provider
) KEY (user_id) VALUES (
  'local-demo-user',
  'Local Demo User',
  'demo@scm.local',
  '$2a$10$QxZZxB3XWUI3TDIXLXeUMurdM72vryi2PKi2UkwET.NsATiYKB2Dq',
  '/images/accord.png',
  '9999999999',
  'Other',
  TRUE,
  TRUE,
  FALSE,
  'SELF'
);

MERGE INTO user_role_list (user_user_id, role_list) KEY (user_user_id, role_list)
VALUES ('local-demo-user', 'ROLE_USER');

MERGE INTO contact (
  id,
  name,
  email,
  phone_number,
  address,
  picture,
  description,
  favorite,
  website_link,
  linked_in_link,
  facebook_link,
  insta_link,
  user_user_id
) KEY (id) VALUES (
  'local-demo-contact',
  'The Accord Support',
  'support@theaccord.local',
  '8888888888',
  'Kolkata, India',
  '/images/telephone.png',
  'Sample contact included so the dashboard is not empty after import.',
  TRUE,
  'https://example.com',
  'https://linkedin.com',
  'https://facebook.com',
  'https://instagram.com',
  'local-demo-user'
);

MERGE INTO feedback (id, name, email, review, rating) KEY (id)
VALUES (1, 'Local Demo User', 'demo@scm.local', 'The local database is ready.', 5);
