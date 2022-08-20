package by.sva.sweater.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import by.sva.sweater.entity.Message;
import by.sva.sweater.entity.User;
import by.sva.sweater.entity.dto.MessageDto;
import by.sva.sweater.repository.MessageRepository;

@Service
public class MessageService {
	
	@Autowired
	private MessageRepository messageRepository;
	
	public Page<MessageDto> getMessageList(Pageable pageable, String filter, User user) {
		Page<Message> messages;
		//можно получить объект Page сразу из репозитория, но в данном случае элементы списка нужно конвертировать для представления
		if(filter != null && !filter.isEmpty()) {
			//return messageRepository.findByTag(filter, pageable);
			messages = messageRepository.findByTag(filter, pageable);
		} else {
			//return messageRepository.findAll(pageable);
			messages = messageRepository.findAll(pageable);
		}
	
		return new PageImpl<MessageDto>(convertToDto(messages, user), pageable, messages.getTotalElements());
	}

	public Page<MessageDto> getUsersMessages(Pageable pageable, User currentUser, User author) {
		//return messageRepository.findByAuthor(pageable, author);
		Page<Message> messages = messageRepository.findByAuthor(pageable, author);
		
		//return new PageImpl<>(convertToDto(messages, currentUser), pageable, messages.size());
		return new PageImpl<MessageDto>(convertToDto(messages, currentUser), pageable, messages.getTotalElements());
	}
	
	public List<MessageDto> convertToDto(Page<Message> page, User user){
		
		//List<Message> messages = page.getContent();
		List<MessageDto> messagesDto = new ArrayList<>();
		
		for(Message message : page) {
			boolean meLiked = message.getLikes().contains(user)? true : false;
			messagesDto.add(new MessageDto(message, meLiked));
		}
		
		return messagesDto;
	}

}
