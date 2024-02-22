package demo.com.repository;


import demo.com.domain.AnimeApiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimeApiUserRepository extends JpaRepository<AnimeApiUser, Long> {
    AnimeApiUser findByUsername(String username);
}
