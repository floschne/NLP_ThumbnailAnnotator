package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.core.domain.UDependency;
import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.CrawlerResultEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertSame;
import static junit.framework.TestCase.assertTrue;

public class CrawlerResultEntityRepositoryTest extends RepositoryTestBase<CrawlerResultEntity> {

    public CrawlerResultEntityRepositoryTest() {
        super(RepoType.CRAWLER_RESULT);
    }

    @NotNull
    @Override
    protected CrawlerResultEntity createDummyEntity() {

        List<UDependency> udContext = new ArrayList<>();
        udContext.add(new UDependency("amod", "big", "ship"));
        CaptionTokenEntity captionTokenEntity = new CaptionTokenEntity(
                "big ship",
                "COMPOUND",
                Arrays.asList("JJ", "NN"),
                Arrays.asList("big", "ship"),
                udContext,
                Collections.singletonList("ship"));

        List<ThumbnailEntity> thumbnails = new ArrayList<>();

        ThumbnailEntity entity = new ThumbnailEntity(
                "https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg",
                1,
                "desc1",
                13337L,
                Arrays.asList(new Thumbnail.Category(1, "a"), new Thumbnail.Category(2, "b")),
                Arrays.asList("k1", "k2"));
        entity.setId("https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg");
        thumbnails.add(entity);

        entity = new ThumbnailEntity(
                "https://image.shutterstock.com/image-vector/lupe-magnifying-glass-barcode-serial-260nw-476181607.jpg",
                2,
                "desc2",
                133437L,
                Arrays.asList(new Thumbnail.Category(3, "c"), new Thumbnail.Category(4, "d")),
                Arrays.asList("k3", "k4"));
        entity.setId("https://image.shutterstock.com/image-vector/lupe-magnifying-glass-barcode-serial-260nw-476181607.jpg");

        thumbnails.add(entity);

        return new CrawlerResultEntity(captionTokenEntity.getValue(), captionTokenEntity, thumbnails);
    }

    @Override
    protected void assertEqual(CrawlerResultEntity a, CrawlerResultEntity b) {
        assertEquals(a.getCaptionTokenValue(), b.getCaptionTokenValue());
        assertEquals(a.getCaptionToken(), b.getCaptionToken());
        assertTrue(a.getThumbnails().containsAll(b.getThumbnails()));
        assertTrue(b.getThumbnails().containsAll(a.getThumbnails()));
    }

    @Override
    protected void saveEntity(CrawlerResultEntity entity) {
        this.thumbnailEntityRepository.saveAll(entity.getThumbnails());
        this.captionTokenEntityRepository.save(entity.getCaptionToken());
        this.crawlerResultEntityRepository.save(entity);

    }
}