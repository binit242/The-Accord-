CREATE DATABASE IF NOT EXISTS scm20
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE scm20;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS social_link;
DROP TABLE IF EXISTS user_role_list;
DROP TABLE IF EXISTS contact;
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
  user_id VARCHAR(255) NOT NULL,
  user_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255),
  profile_pic VARCHAR(1000),
  phone_number VARCHAR(255),
  gender VARCHAR(255),
  enabled BIT(1) NOT NULL DEFAULT b'0',
  email_verified BIT(1) NOT NULL DEFAULT b'0',
  phone_verified BIT(1) NOT NULL DEFAULT b'0',
  cloudinary_image_public_id VARCHAR(255),
  provider ENUM('SELF', 'GOOGLE', 'GITHUB') DEFAULT 'SELF',
  provider_user_id VARCHAR(255),
  email_token VARCHAR(255),
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE contact (
  id VARCHAR(255) NOT NULL,
  name VARCHAR(255),
  email VARCHAR(255),
  phone_number VARCHAR(255),
  address VARCHAR(255),
  picture VARCHAR(255),
  description VARCHAR(1000),
  favorite BIT(1) NOT NULL DEFAULT b'0',
  website_link VARCHAR(255),
  linked_in_link VARCHAR(255),
  facebook_link VARCHAR(255),
  insta_link VARCHAR(255),
  cloudinary_image_public_id VARCHAR(255),
  user_user_id VARCHAR(255),
  PRIMARY KEY (id),
  KEY idx_contact_user (user_user_id),
  CONSTRAINT fk_contact_user FOREIGN KEY (user_user_id) REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE social_link (
  id BIGINT NOT NULL AUTO_INCREMENT,
  link VARCHAR(255),
  title VARCHAR(255),
  contact_id VARCHAR(255),
  PRIMARY KEY (id),
  KEY idx_social_link_contact (contact_id),
  CONSTRAINT fk_social_link_contact FOREIGN KEY (contact_id) REFERENCES contact (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_role_list (
  user_user_id VARCHAR(255) NOT NULL,
  role_list VARCHAR(255),
  KEY idx_user_role_user (user_user_id),
  CONSTRAINT fk_user_role_user FOREIGN KEY (user_user_id) REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE feedback (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255),
  email VARCHAR(255),
  review VARCHAR(1000),
  rating INT NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO users (
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
) VALUES (
  'local-demo-user',
  'Local Demo User',
  'demo@scm.local',
  '$2a$10$QxZZxB3XWUI3TDIXLXeUMurdM72vryi2PKi2UkwET.NsATiYKB2Dq',
  '/images/accord.png',
  '9999999999',
  'Other',
  b'1',
  b'1',
  b'0',
  'SELF'
);

INSERT INTO user_role_list (user_user_id, role_list)
VALUES ('local-demo-user', 'ROLE_USER');

INSERT INTO contact (
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
) VALUES (
  'local-demo-contact',
  'The Accord Support',
  'support@theaccord.local',
  '8888888888',
  'Kolkata, India',
  '/images/telephone.png',
  'Sample contact included so the dashboard is not empty after import.',
  b'1',
  'https://example.com',
  'https://linkedin.com',
  'https://facebook.com',
  'https://instagram.com',
  'local-demo-user'
);

INSERT INTO feedback (name, email, review, rating)
VALUES ('Local Demo User', 'demo@scm.local', 'The local database is ready.', 5);
