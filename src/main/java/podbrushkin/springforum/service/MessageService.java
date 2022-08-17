package podbrushkin.springforum.service;

import podbrushkin.springforum.model.*;
import java.util.Optional;
import java.util.List;

public interface MessageService {
	
	List<Message> getAll();
	
	List<Message> findByUserId(long userId);
	Optional<Message> getById(long messageId);
	Message createMessage(Object principal, Message message);
}