DELIMITER //
CREATE PROCEDURE sp_DeleteUserCompletelyById(IN inputId bigint) 
BEGIN
	DELETE FROM userid_username WHERE userid=inputId;
	DELETE FROM userid_role WHERE userid=inputId;
	DELETE FROM user_openid WHERE userid=inputId;
	DELETE FROM message WHERE author_id=inputId;
	DELETE FROM users WHERE id=inputId;
END //
DELIMITER ;