CREATE TABLE IF NOT EXISTS users (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(100) NOT NULL,
  email         VARCHAR(120) NOT NULL UNIQUE,
  password_hash VARCHAR(200) NOT NULL,
  role          VARCHAR(10)  NOT NULL
);

CREATE TABLE IF NOT EXISTS projects (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  owner_id    INT NOT NULL,
  group_name  VARCHAR(20),
  title       VARCHAR(200)  NOT NULL,
  description VARCHAR(2000) NOT NULL,
  tech        VARCHAR(200),
  team        VARCHAR(300),
  status      VARCHAR(20)   NOT NULL,
  submitted   DATE          NOT NULL,
  FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS milestones (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  project_id INT NOT NULL,
  title      VARCHAR(200) NOT NULL,
  due_date   DATE NULL,
  done       BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS updates (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  project_id INT NOT NULL,
  created    DATE NOT NULL,
  body       VARCHAR(1000) NOT NULL,
  FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS feedback (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  project_id INT NOT NULL,
  created    DATE NOT NULL,
  by_name    VARCHAR(100) NOT NULL,
  body       VARCHAR(1000) NOT NULL,
  FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);
