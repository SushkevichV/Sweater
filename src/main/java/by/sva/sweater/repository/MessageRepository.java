package by.sva.sweater.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import by.sva.sweater.entity.Message;
import by.sva.sweater.entity.User;

public interface MessageRepository extends JpaRepository<Message, Long> {
	
	// постраничное получение данных
	// переопределить стандартный метод findAll - изменить тип возвращаемого значения на Page
	Page<Message> findAll(Pageable pageable);
	//List<Message> findAll();
	
	// List<Message> findByTag(String tag);
	Page<Message> findByTag(String tag, Pageable pageable);
	//List<Message> findByTag(String tag);

	/* HQL-запрос 
	 *  из таблицы, соответствующей сущности Message (далее - m) найти поля, 
	 *  где значение поля m.author равно значению переменной author
	 *  Этот формат запроса удобен для CRUD-репозитория
	 *  В JPA-репозитории Sptring генерирует запрос исходя из имени метода,
	 *  т.е. искомое поле должно быть в имени метода
	 */
	//@Query("from Message m where m.author = :author")
	//Page<Message> findByUser(Pageable pageable, @Param("author") User author);
	Page<Message> findByAuthor(Pageable pageable, User author);
	//List<Message> findByAuthor(User author);

}
