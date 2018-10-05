package nlp.floschne.thumbnailAnnotator.api.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.sf.extjwnl.JWNLException;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.CaptionTokenExtractor;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult;
import nlp.floschne.thumbnailAnnotator.core.domain.ExtractorResult;
import nlp.floschne.thumbnailAnnotator.core.domain.UserInput;
import nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.ThumbnailCrawler;
import nlp.floschne.thumbnailAnnotator.db.entity.CrawlerResultEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;
import nlp.floschne.thumbnailAnnotator.db.service.DBService;
import org.apache.uima.resource.ResourceInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin
@RequestMapping(value = "/")
@Api(tags = "Thumbnail Annotator API", description = "REST API to access the functionality of the Thumbnail Annotator Service!")
@Slf4j
public class ApiController {


    private final DBService dbService;

    @Autowired
    public ApiController(DBService dbService) {
        this.dbService = dbService;
        log.info("API Controller ready!");
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    void home(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }

    @RequestMapping(value = "/crawlThumbnails", method = RequestMethod.POST)
    public List<CrawlerResultEntity> crawlThumbnails(@RequestBody UserInput input) throws ExecutionException, InterruptedException, IOException, ResourceInitializationException, JWNLException {
        if (input.getValue().isEmpty())
            throw new InputMismatchException("Must input at least a Token!");

        // extract the CaptionTokens from UserInput
        Future<ExtractorResult> extractionResultFuture = CaptionTokenExtractor.getInstance().startExtractionOfCaptionTokens(input);
        List<CaptionToken> captionTokens = extractionResultFuture.get().getCaptionTokens();

        // list of futures of CrawlerResults
        List<Future<CrawlerResult>> crawlingResultFutures = new ArrayList<>();

        // list of CrawlerResultEntities that are already cached or get cached and will be returned
        List<CrawlerResultEntity> crawlerResults = new ArrayList<>();

        // for every extracted CaptionToken
        for (CaptionToken captionToken : captionTokens) {
            // check if CrawlerResultEntity that matches the captionToken is cached in repo and skip new crawling if so
            CrawlerResultEntity result = this.dbService.findCrawlerResultByCaptionToken(captionToken);
            if (result != null) {
                log.info("Using cached results for '" + captionToken + "'");
                if (!crawlerResults.contains(result))
                    crawlerResults.add(result);
                continue;
            }

            // no cached CrawlerResultEntity for the CaptionToken -> start crawling
            try {
                crawlingResultFutures.add(ThumbnailCrawler.getInstance().startCrawlingThumbnails(captionToken));
            } catch (Exception e) {
                throw e;
            }
        }


        // get the CrawlerResults from the Futures, cache them and add them to the final results
        for (Future<CrawlerResult> crawlerResultFuture : crawlingResultFutures) {

            try {
                // wait no longer than 5 second
                // TODO ConfigVariable
                CrawlerResult crawlerResult = crawlerResultFuture.get(10, TimeUnit.SECONDS);

                // save the results in repo
                log.info("Caching results for '" + crawlerResult.getCaptionToken() + "'");
                CrawlerResultEntity result = this.dbService.saveCrawlerResult(crawlerResult);

                if (!crawlerResults.contains(result))
                    crawlerResults.add(result);
            } catch (Exception e) {
                throw new ConnectException("It too long time (10s) to finish crawling of Thumbnails!");
            }

        }

        return crawlerResults;
    }

    @RequestMapping(value = "/getCrawlerResult/{id}", method = RequestMethod.GET)
    public CrawlerResultEntity getCrawlerResult(@PathVariable String id) {
        return this.dbService.findCrawlerResultById(id);
    }

    @RequestMapping(value = "/incrementThumbnailPriority/{id}", method = RequestMethod.PUT)
    public ThumbnailEntity incrementThumbnailPriority(@PathVariable String id) {
        return this.dbService.incrementThumbnailPriorityById(id);
    }

    @RequestMapping(value = "/decrementThumbnailPriority/{id}", method = RequestMethod.PUT)
    public ThumbnailEntity decrementThumbnailPriority(@PathVariable String id) {
        return this.dbService.decrementThumbnailPriorityById(id);
    }

    @RequestMapping(value = "/getCachedCrawlerResults", method = RequestMethod.GET)
    public List<CrawlerResultEntity> getCachedCrawlerResults() {
        return new ArrayList<>(this.dbService.findAllCrawlerResult());
    }

    @RequestMapping(value = "/flushCache", method = RequestMethod.DELETE)
    public void flushCache() {
        log.warn("Flushed Cache!");
        this.dbService.deleteAllCrawlerResultEntities();
    }
}
