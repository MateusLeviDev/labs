package demo.com.controller;

import demo.com.domain.Anime;
import demo.com.requests.AnimePostRequestBody;
import demo.com.requests.AnimePutRequestBody;
import demo.com.service.AnimeService;
import demo.com.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping()
@Log4j2
@RequiredArgsConstructor
public class AnimeController {

    private final DateUtil dateUtil;
    private final AnimeService animeService;

    @GetMapping("/logs")
    public String homePage() {
        LocalDateTime localDateTime = LocalDateTime.now();
        log.info("This logs page" + localDateTime);
        return "Welcome SpringBoot";
    }

    @GetMapping
    public ResponseEntity<Page<Anime>> list(Pageable pageable) {
        return ResponseEntity.ok(animeService.listAll(pageable));
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<Anime>> listAll() {
        log.info("List all animes" + dateUtil.formatLocalDateTimeToDataBaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.listAllNonPageable());
    }

    @GetMapping("/ip")
    public ResponseEntity<?> getIP(HttpServletRequest request) throws IOException {
        return ResponseEntity.ok().body(request.getRemoteAddr());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Anime> findById(@PathVariable Long id) {
        return ResponseEntity.ok(animeService.findByIdOrThrowBadRequestException(id));
    }

    @PostMapping
    public ResponseEntity<Anime> save(@RequestBody @Valid AnimePostRequestBody animePostRequestBody) {
        return new ResponseEntity<>(animeService.save(animePostRequestBody), HttpStatus.CREATED);
    }

    @GetMapping(path = "/find")
    public ResponseEntity<List<Anime>> findByName(@RequestParam String name) {
        return ResponseEntity.ok(animeService.findByName(name));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        animeService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<Void> replace(@RequestBody AnimePutRequestBody animePutRequestBody) {
        animeService.replace(animePutRequestBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
