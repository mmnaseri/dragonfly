DROP PROCEDURE `test`.`refresh`;
CREATE DEFINER=`dragonfly`@`localhost` PROCEDURE `test`.`refresh`()
BEGIN
	DELETE FROM `test`.`authors`;
	DELETE FROM `test`.`books`;
	DELETE FROM `test`.`dragonfly_sequences`;
	DELETE FROM `test`.`groups`;
	DELETE FROM `test`.`library_card`;
	DELETE FROM `test`.`people`;
	DELETE FROM `test`.`stations`;
	DELETE FROM `test`.`stuff`;
	DELETE FROM `test`.`thors_books_authors_books`;
	DELETE FROM `test`.`thrs_bks_dtdbooks_editors`;
	DELETE FROM `test`.`switch`;
END;