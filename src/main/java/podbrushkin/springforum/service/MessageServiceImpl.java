package podbrushkin.springforum.service;

import podbrushkin.springforum.model.*;
import podbrushkin.springforum.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Optional;
import java.util.List;

@Service
@Transactional(readOnly=true)
@lombok.extern.slf4j.Slf4j
public class MessageServiceImpl implements MessageService {
	
	@Autowired
	MessageRepository messageRepository;
	@Autowired
	UserRepository userRepository;
	
	@PersistenceContext
	EntityManager em;
	
	public List<Message> getAll() {
		return messageRepository.findAll();
	}
	
	public List<Message> findByUserId(long userId) {
		var user = userRepository.getReferenceById(userId);
		return messageRepository.findByAuthor(user);
	}
	
	public Optional<Message> getById(long messageId) {
		return Optional.ofNullable(messageRepository.getReferenceById(messageId));
	}
	
	@Transactional
	public Message createMessage(Object principal, Message message) {
		if (message.getText().isBlank()) return null;
		message.setId(null);
		var userDetails = (podbrushkin.springforum.security.MyUserDetails) principal;
		var author = userRepository.findByUsername(userDetails.getUsername());
		message.setAuthor(author);
		message.setDate(java.time.LocalDateTime.now());
		var m = messageRepository.save(message);
		log.info("Persisted new Message to database: " + m);
		return m;
	}
}