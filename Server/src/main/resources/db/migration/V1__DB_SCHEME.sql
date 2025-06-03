
CREATE TABLE `user` (
    `id` int NOT NULL AUTO_INCREMENT,
    `balance` double DEFAULT NULL,
    `display_name` varchar(255) DEFAULT NULL,
    `experience` int DEFAULT NULL,
    `email` varchar(255) DEFAULT NULL,
    `password` varchar(255) DEFAULT NULL,
    `username` varchar(255) DEFAULT NULL,
    `profile_picture` int DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK3kldg2stvujr4dqxewoxa9x4i` (`profile_picture`),
    CONSTRAINT `FK3kldg2stvujr4dqxewoxa9x4i` FOREIGN KEY (`profile_picture`) REFERENCES `image` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




CREATE TABLE `image` (
  `id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




CREATE TABLE `saved_images` (
    `id` int NOT NULL AUTO_INCREMENT,
    `image_id` int DEFAULT NULL,
    `user_id` int DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK6i2b3ipcwp9qxoeypt0qnbyl1` (`image_id`),
    KEY `FKe08rexaui23xipwksf91rfh6l` (`user_id`),
    CONSTRAINT `FK6i2b3ipcwp9qxoeypt0qnbyl1` FOREIGN KEY (`image_id`) REFERENCES `image` (`id`),
    CONSTRAINT `FKe08rexaui23xipwksf91rfh6l` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;