CREATE TABLE app_devices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(255) NOT NULL UNIQUE,
    version_code INT NOT NULL,
    version_name VARCHAR(50) NOT NULL,
    last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE system_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    log_level VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE app_releases (
    id INT AUTO_INCREMENT PRIMARY KEY,
    latest_version_code INT NOT NULL,
    latest_version_name VARCHAR(50) NOT NULL,
    apk_url TEXT NOT NULL,
    release_notes TEXT,
    is_forced BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO app_releases (
    latest_version_code, latest_version_name, apk_url, release_notes, is_forced
) VALUES (
    4,
    '1.3.0',
    'https://indrive-bot.onrender.com/updates/app-debug.apk',
    'Actualizacion visual King System con soporte modular.',
    FALSE
);
