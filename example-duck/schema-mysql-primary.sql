CREATE DATABASE `duck-primary` DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_bin;

CREATE TABLE `student` (
    `id` int NOT NULL AUTO_INCREMENT,
    `name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
    `age` int DEFAULT NULL,
    `gender` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
    `birthday` datetime DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
