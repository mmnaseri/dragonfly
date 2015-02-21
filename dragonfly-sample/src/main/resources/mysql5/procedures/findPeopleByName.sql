DROP PROCEDURE `test`.`findPeopleByName`;
CREATE DEFINER=`dragonfly`@`localhost` PROCEDURE `test`.`findPeopleByName`(IN `name` VARCHAR(255), OUT `cnt` BIGINT)
BEGIN
	SELECT COUNT(*) INTO `cnt` FROM `test`.`people` WHERE `name` LIKE `name`;
	SELECT * FROM `test`.`people`;
END;