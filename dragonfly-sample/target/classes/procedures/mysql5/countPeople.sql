DROP PROCEDURE test.countPeople;
CREATE DEFINER=`root`@`localhost` PROCEDURE `countPeople`(OUT cnt BIGINT)
BEGIN
	SELECT COUNT(*) INTO cnt FROM `test`.`people`;
END;