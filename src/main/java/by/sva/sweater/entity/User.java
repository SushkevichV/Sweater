package by.sva.sweater.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "usr") //Postgresql сам не создает таблицу с именем user, но работает с ней, если она уже есть
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotBlank(message = "Поле не должно быть пустым")
	private String username;
	@NotBlank(message = "Поле не должно быть пустым")
	private String password;
	private boolean active;
	@NotBlank(message = "Поле не должно быть пустым")
	@Email(message = "Неверный email")
	private String email;
	private String activationCode;
	
	// добавляет таблицу со значениями из класса Role, использует жадную загрузку при обращении к таблице
	@ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
	// данное поле будет храниться в отдельной таблице. Она будет связана с текущей таблицей через внешний ключ "user_id"
	@CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
	// хранить ENUM в виде строки (а не int)
	@Enumerated(EnumType.STRING)
	private Set<Role> roles;
	
	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	private Set<Message> messages;
	
	@ManyToMany
	@JoinTable(
			name = "user_subscriptions",
			joinColumns = {@JoinColumn(name = "channel_id")},
			inverseJoinColumns = {@JoinColumn(name = "subscriber_id")}
			)
	private Set<User> subscribers = new HashSet<>();

	@ManyToMany
	@JoinTable(
			name = "user_subscriptions",
			joinColumns = {@JoinColumn(name = "subscriber_id")},
			inverseJoinColumns = {@JoinColumn(name = "channel_id")}
			)
	private Set<User> subscriptions = new HashSet<>();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String userName) {
		this.username = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public boolean isAdmin() {
		return roles.contains(Role.ADMIN);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return getRoles();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return isActive();
	}

	public Set<Message> getMessages() {
		return messages;
	}

	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}

	public Set<User> getSubscribers() {
		return subscribers;
	}

	public void setSubscribers(Set<User> subscribers) {
		this.subscribers = subscribers;
	}

	public Set<User> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(Set<User> subscriptions) {
		this.subscriptions = subscriptions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
