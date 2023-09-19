package demo.com.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AnimePostRequestBody {
    @NotNull(message = "Anime cannot be null")
    private String name;
}
