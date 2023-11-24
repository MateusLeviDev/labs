package demo.com.repository;

import demo.com.domain.Anime;
import demo.com.domain.AnimeApiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimeApiUserRepository extends JpaRepository<AnimeApiUser, Long> {
    AnimeApiUser findByUsername(String username);
}
