DELIMITER $$
CREATE DEFINER=`lab`@`%` PROCEDURE `calculate_get_salary` ()  BEGIN

SET @command = (
    SELECT CONCAT('
        INSERT INTO salary(person_id, salary)
        SELECT person_id, (', GROUP_CONCAT(
        	CONCAT('calculate_get_spd(', COLUMN_NAME, ')') 
        	SEPARATOR ' + '
    	),') FROM person_schedule_info')
	FROM INFORMATION_SCHEMA.COLUMNS
	WHERE TABLE_NAME = 'person_schedule_info'
	AND COLUMN_NAME RLIKE '^day'
);

PREPARE stmt FROM @command;
EXECUTE stmt;

END$$

CREATE DEFINER=`lab`@`%` PROCEDURE `calculate_get_sold_amount` (IN `depname` VARCHAR(255), OUT `o_department` VARCHAR(255) CHARSET utf8, OUT `o_total_amount` BIGINT(20), OUT `o_date` DATETIME)  SELECT person_schedule_info.department, SUM(amount) as total_amount, NOW() as date INTO o_department, o_total_amount, o_date FROM products
JOIN person_schedule_info USING(person_id)
WHERE person_schedule_info.department = depname
GROUP BY person_schedule_info.department$$

CREATE DEFINER=`lab`@`%` PROCEDURE `calculate_store_sold_amount` ()  MODIFIES SQL DATA
BEGIN
DECLARE done INT DEFAULT 0;
DECLARE dep_name varchar(255);
DECLARE cur CURSOR FOR SELECT DISTINCT department FROM person_schedule_info;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

OPEN cur;
REPEAT
    FETCH cur INTO dep_name;
    IF NOT done THEN
      CALL calculate_get_sold_amount(dep_name, @dep, @total, @date);
      INSERT INTO department_score (SELECT @dep, @total, @date);
    END IF;
UNTIL done END REPEAT;
CLOSE cur;
END$$

CREATE DEFINER=`lab`@`%` FUNCTION `calculate_get_spd` (`state` TINYINT(4)) RETURNS INT(11) BEGIN

IF state < 4 THEN 
RETURN 1000;
ELSE 
RETURN 0;
END IF;

END$$

DELIMITER ;

CREATE TABLE `department_score` (
  `department` varchar(255) NOT NULL,
  `amount` bigint(20) NOT NULL,
  `date` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `people` (
  `person_id` bigint(20) UNSIGNED NOT NULL,
  `last_name` text,
  `first_name` text,
  `second_name` text,
  `date_of_birth` date DEFAULT NULL,
  `sex` char(1) DEFAULT NULL,
  `place_of_birth` text,
  `address` text,
  `phone` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `person_schedule_info` (
  `sched_id` bigint(20) UNSIGNED NOT NULL,
  `person_id` bigint(20) UNSIGNED NOT NULL,
  `department` varchar(255) NOT NULL,
  `day1` tinyint(4) NOT NULL,
  `day2` tinyint(4) NOT NULL,
  `day3` tinyint(4) NOT NULL,
  `day4` tinyint(4) NOT NULL,
  `day5` tinyint(4) NOT NULL,
  `day6` tinyint(4) NOT NULL,
  `day7` tinyint(4) NOT NULL,
  `day8` tinyint(4) NOT NULL,
  `day9` tinyint(4) NOT NULL,
  `day10` tinyint(4) NOT NULL,
  `day11` tinyint(4) NOT NULL,
  `day12` tinyint(4) NOT NULL,
  `day13` tinyint(4) NOT NULL,
  `day14` tinyint(4) NOT NULL,
  `day15` tinyint(4) NOT NULL,
  `day16` tinyint(4) NOT NULL,
  `day17` tinyint(4) NOT NULL,
  `day18` tinyint(4) NOT NULL,
  `day19` tinyint(4) NOT NULL,
  `day20` tinyint(4) NOT NULL,
  `day21` tinyint(4) NOT NULL,
  `day22` tinyint(4) NOT NULL,
  `day23` tinyint(4) NOT NULL,
  `day24` tinyint(4) NOT NULL,
  `day25` tinyint(4) NOT NULL,
  `day26` tinyint(4) NOT NULL,
  `day27` tinyint(4) NOT NULL,
  `day28` tinyint(4) NOT NULL,
  `day29` tinyint(4) NOT NULL,
  `day30` tinyint(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `products` (
  `product_id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `amount` bigint(20) NOT NULL,
  `person_id` bigint(20) UNSIGNED NOT NULL,
  `sell_date` date NOT NULL,
  `price` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `salary` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `person_id` bigint(20) UNSIGNED NOT NULL,
  `salary` int(11) NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE `people`
  ADD PRIMARY KEY (`person_id`);

ALTER TABLE `person_schedule_info`
  ADD PRIMARY KEY (`sched_id`),
  ADD UNIQUE KEY `person_department_data` (`department`,`person_id`),
  ADD KEY `person_id` (`person_id`),
  ADD KEY `department` (`department`) USING BTREE;

ALTER TABLE `products`
  ADD PRIMARY KEY (`product_id`),
  ADD UNIQUE KEY `sell_entry` (`product_id`,`sell_date`,`person_id`) USING BTREE,
  ADD KEY `person_id` (`person_id`);

ALTER TABLE `salary`
  ADD PRIMARY KEY (`id`);


ALTER TABLE `people`
  MODIFY `person_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1001;
ALTER TABLE `person_schedule_info`
  MODIFY `sched_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1001;
ALTER TABLE `products`
  MODIFY `product_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1001;
ALTER TABLE `salary`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1024;

ALTER TABLE `person_schedule_info`
  ADD CONSTRAINT `person_schedule_info_ibfk_1` FOREIGN KEY (`person_id`) REFERENCES `people` (`person_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `products`
  ADD CONSTRAINT `products_ibfk_1` FOREIGN KEY (`person_id`) REFERENCES `people` (`person_id`) ON DELETE CASCADE ON UPDATE CASCADE;

DELIMITER $$
CREATE DEFINER=`lab`@`%` EVENT `calculate_month_report` ON SCHEDULE EVERY 1 MONTH STARTS '2016-10-17 22:06:35' ON COMPLETION NOT PRESERVE ENABLE DO BEGIN
CALL calculate_get_salary();
CALL calculate_store_sold_amount();
END$$

DELIMITER ;
