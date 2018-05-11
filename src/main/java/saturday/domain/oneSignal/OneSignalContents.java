package saturday.domain.oneSignal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class OneSignalContents {

    private String englishMessage;

    @JsonProperty("en")
    public String getEnglishMessage() {
        return englishMessage;
    }

    @JsonProperty("en")
    public void setEnglishMessage(String englishMessage) {
        this.englishMessage = englishMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OneSignalContents contents = (OneSignalContents) o;
        return Objects.equals(englishMessage, contents.englishMessage);
    }

    @Override
    public int hashCode() {

        return Objects.hash(englishMessage);
    }
}
