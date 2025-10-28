

CREATE TABLE issues (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority ENUM('Low', 'Medium', 'High', 'Critical') DEFAULT 'Medium',
    type ENUM('Bug', 'Feature', 'Task', 'Incident') DEFAULT 'Task',
    status ENUM('Open', 'In Progress', 'Resolved', 'Closed', 'On Hold') DEFAULT 'Open',
    resolution VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    due_date DATE,
    sla_breach BOOLEAN DEFAULT FALSE,
    reporter_id INT,
    assignee_id INT,
    team_id INT,
    tags TEXT, -- Store comma-separated tags (e.g., "frontend,urgent")
    attachments TEXT, -- Store comma-separated URLs or file paths
    comments_count INT DEFAULT 0,
    watchers TEXT, -- Store comma-separated user IDs
    custom_fields JSON, -- Requires MySQL 5.7+ or MariaDB 10.2+
    linked_issues TEXT, -- Store comma-separated issue IDs
    time_spent TIME,
    estimated_time TIME,
    change_history JSON -- Optional: audit trail
);