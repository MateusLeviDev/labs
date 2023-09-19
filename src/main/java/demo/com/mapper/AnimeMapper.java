package demo.com.mapper;

import demo.com.domain.Anime;
import demo.com.requests.AnimePostRequestBody;
import demo.com.requests.AnimePutRequestBody;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public abstract class AnimeMapper {
    public static final AnimeMapper INSTANCE = Mappers.getMapper(AnimeMapper.class);

    public abstract Anime toMapper(AnimePostRequestBody animePostRequestBody);
    public abstract Anime toMapper(AnimePutRequestBody animePutRequestBody);
}
