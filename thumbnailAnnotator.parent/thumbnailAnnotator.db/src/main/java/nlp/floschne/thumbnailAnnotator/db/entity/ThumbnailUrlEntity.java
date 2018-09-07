package nlp.floschne.thumbnailAnnotator.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@RedisHash("thumbnail_url_entity")
public class ThumbnailUrlEntity extends Entity {
    @Indexed
    private String url;

    private int priority;
}