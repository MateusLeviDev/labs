package demo.com.service;

import demo.com.domain.Anime;
import demo.com.mapper.AnimeMapper;
import demo.com.repository.AnimeRepository;
import demo.com.requests.AnimePostRequestBody;
import demo.com.requests.AnimePutRequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimeService {
    private final AnimeRepository repository;

    public List<Anime> listAll() {
        return repository.findAll();
    }

    public Anime findByIdOrThrowBadRequestException(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anime not found."));
    }

    public Anime save(AnimePostRequestBody AnimePostRequestBody) {
        return repository.save(
                AnimeMapper.INSTANCE.toMapper(AnimePostRequestBody));
    }

    public void delete(long id) {
        repository.delete(findByIdOrThrowBadRequestException(id));
    }

    public void replace(AnimePutRequestBody animePutRequestBody) {
        Anime savedAnime = findByIdOrThrowBadRequestException(animePutRequestBody.getId());
        Anime anime = AnimeMapper.INSTANCE.toMapper(animePutRequestBody);
        anime.setId(savedAnime.getId());
        repository.save(anime);
    }
}
