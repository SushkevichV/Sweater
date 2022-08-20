package by.sva.sweater.entity.dto;

import by.sva.sweater.entity.Message;
import by.sva.sweater.entity.User;
import by.sva.sweater.entity.util.MessageHelper;

public class MessageDto {
	private Long id;
	private String text;
	private String tag;
	private User author;
	private String filename;
	private int likes;
	private Boolean meLiked;

	public MessageDto(Message message, Boolean meLiked) {
		this.id = message.getId();
		this.text = message.getText();
		this.tag = message.getTag();
		this.author = message.getAuthor();
		this.filename = message.getFilename();
		this.likes = message.getLikes().size();
		this.meLiked = meLiked;
	}

	public Long getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getTag() {
		return tag;
	}

	public User getAuthor() {
		return author;
	}

	public String getFilename() {
		return filename;
	}

	public int getLikes() {
		return likes;
	}

	public Boolean getMeLiked() {
		return meLiked;
	}
	
	public String getAuthorName() {
		return MessageHelper.getAuthorName(author);
	}

	@Override
	public String toString() {
		return "MessageDto [id=" + id + ", author=" + author + ", likes=" + likes + ", meLiked=" + meLiked + "]";
	}

}
