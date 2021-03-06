package nlp.floschne.thumbnailAnnotator.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptionToken extends DomainObject {

    public enum Type {
        NOUN("Noun"),
        COMPOUND("Compound"),
        PERSON("Person"),
        ORGANIZATION("Organization"),
        LOCATION("Location");

        private final String type;

        Type(final String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    private String value;
    private Type type;

    private List<String> posTags;
    private List<String> tokens;
    private List<UDependency> udContext;
    private List<String> wordNetSenses;

    private List<Thumbnail> thumbnails;

    private List<String> lemmata;

    private SentenceContext sentenceContext;

    private List<String> biGrams;
    private List<String> triGrams;

    public List<String> getPosTags() {
        return posTags != null ? posTags : new ArrayList<>();
    }

    public List<String> getTokens() {
        return tokens != null ? tokens : new ArrayList<>();
    }

    public List<String> getLemmata() {
        return lemmata != null ? lemmata : new ArrayList<>();
    }

    public List<String> getBiGrams() {
        return biGrams != null ? biGrams : new ArrayList<>();
    }

    public List<String> getTriGrams() {
        return triGrams != null ? triGrams : new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class UDependency extends DomainObject {

        String type;
        String governor;
        String dependent;

        @Override
        public String toString() {
            return this.type + "(" + this.governor + "," + this.dependent + ")";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CaptionToken that = (CaptionToken) o;

        return new EqualsBuilder()
                .append(getValue(), that.getValue())
                .append(getType(), that.getType())
                .append(getPosTags(), that.getPosTags())
                .append(getTokens(), that.getTokens())
                .append(getUdContext(), that.getUdContext())
                .append(getLemmata(), that.getLemmata())
                .append(getSentenceContext(), that.getSentenceContext())
                .append(getBiGrams(), that.getBiGrams())
                .append(getTriGrams(), that.getTriGrams())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getValue())
                .append(getType())
                .append(getPosTags())
                .append(getTokens())
                .append(getUdContext())
                .append(getLemmata())
                .append(getSentenceContext())
                .toHashCode();
    }

    public static CaptionToken createDummyTestingCaptionToken() {
        return new CaptionToken(
                "smaller car",
                CaptionToken.Type.NOUN,
                Arrays.asList("JJ", "NN"),
                Arrays.asList("smaller", "car"),
                Collections.singletonList(new UDependency("amod", "small", "car")),
                Collections.singletonList("A car is a vehicle with four wheels."),
                Collections.singletonList(Thumbnail.createDummyTestingThumbnail()),
                Arrays.asList("small", "car"),
                SentenceContext.createDummyEntityTestingSentenceContext(),
                Collections.singletonList("smaller car"),
                null);
    }
}
