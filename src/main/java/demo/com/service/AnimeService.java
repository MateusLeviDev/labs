package demo.com.service;

import demo.com.domain.Anime;
import demo.com.exception.BadRequestException;
import demo.com.mapper.AnimeMapper;
import demo.com.repository.AnimeRepository;
import demo.com.requests.AnimePostRequestBody;
import demo.com.requests.AnimePutRequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimeService {
    private final AnimeRepository repository;

    public Page<Anime> listAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<Anime> listAllNonPageable() {
        return repository.findAll();
    }

    public List<Anime> findByName(String name) {
        return repository.findByName(name);
    }

    public Anime findByIdOrThrowBadRequestException(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BadRequestException("Anime not found."));
    }

    @Transactional
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
