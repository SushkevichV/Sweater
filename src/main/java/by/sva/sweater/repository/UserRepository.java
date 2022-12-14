package by.sva.sweater.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import by.sva.sweater.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);

	User findByActivationCode(String code);

}
