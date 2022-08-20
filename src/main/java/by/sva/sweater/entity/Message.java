package by.sva.sweater.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import by.sva.sweater.entity.util.MessageHelper;

@Entity
public class Message {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank(message = "Поле не должно быть пустым")
	@Length(max = 2048, message = "Слишком длинное сообщение")
	private String text;
	@NotBlank(message = "Поле не должно быть пустым")
	@Length(max = 255, message = "Слишком длинное сообщение")
	private String tag;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User author;
	
	private String filename;
	
	@ManyToMany
	@JoinTable(
			name = "message_likes",
			joinColumns = {@JoinColumn(name = "message_id")},
			inverseJoinColumns = {@JoinColumn(name = "user_id")})
	private Set<User> likes = new HashSet<>();
	
	public Message() {}
	
	public Message(String text, String tag, User user) {
		this.text = text;
		this.tag = tag;
		this.author = user;
	}
	
	public String getAuthorName() {
		return MessageHelper.getAuthorName(author);
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Set<User> getLikes() {
		return likes;
	}

	public void setLikes(Set<User> likes) {
		this.likes = likes;
	}

}
