DROP PROCEDURE `test`.`countPeople`;
CREATE DEFINER=`dragonfly`@`localhost` PROCEDURE `test`.`countPeople`(OUT `cnt` BIGINT)
BEGIN
	SELECT COUNT(*) INTO `cnt` FROM `test`.`people`;
END;