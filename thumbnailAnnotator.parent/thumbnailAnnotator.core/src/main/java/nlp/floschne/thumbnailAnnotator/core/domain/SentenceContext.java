package nlp.floschne.thumbnailAnnotator.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class SentenceContext {

    private List<String> sTokens;
    private List<String> sLemmata;
    private List<String> sPosTags;

    // TODO methods to get the n left and/or right words! (context window)

    public static SentenceContext createDummyDomainTestingSentenceContext() {
        String s = "I like my smaller car a lot .";
        List<String> tokens = Arrays.asList(s.split(" "));
        List<String> lemmata = Arrays.asList("I", "like", "my", "small", "car", "a", "lot", ".");
        List<String> pos = Arrays.asList("PRP", "VB", "PRP", "JJR", "NN", "DET", "NN", ".");
        return new SentenceContext(tokens, lemmata, pos);
    }

    public static SentenceContext createDummyEntityTestingSentenceContext() {
        String s = "I like my bigger ship a lot .";
        List<String> tokens = Arrays.asList(s.split(" "));
        List<String> lemmata = Arrays.asList("I", "like", "my", "big", "ship", "a", "lot", ".");
        List<String> pos = Arrays.asList("PRP", "VB", "PRP", "JJR", "NN", "DET", "NN", ".");
        return new SentenceContext(tokens, lemmata, pos);
    }
}
