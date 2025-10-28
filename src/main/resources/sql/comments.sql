CREATE TABLE comments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    issue_id INT NOT NULL,
    author_id INT NOT NULL,
    message TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_internal BOOLEAN DEFAULT FALSE, -- true = agent-only/internal note
    FOREIGN KEY (issue_id) REFERENCES issues(id),
    FOREIGN KEY (author_id) REFERENCES users(id)
);