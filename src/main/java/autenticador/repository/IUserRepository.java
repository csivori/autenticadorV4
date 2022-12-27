package autenticador.repository;

import autenticador.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {
    List<User> findAllByName(String name);
    List<User> findAllByNameLike(String name);
    List<User> findAllByCreatedDateBetween(LocalDateTime desde, LocalDateTime hasta);
    @Query(value = "SELECT u.* FROM `users` u WHERE DATEDIFF(CURRENT_DATE, u.created_date) > :dias and DATEDIFF(CURRENT_DATE, IFNULL(u.last_logged, u.created_date)) > :dias", nativeQuery = true)
    List<User> findAllInactiveUsers(@Param("dias") Integer dias);


}
