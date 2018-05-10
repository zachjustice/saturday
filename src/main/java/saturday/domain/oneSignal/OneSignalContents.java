package saturday.domain.oneSignal;

import com.fasterxml.jackson.annotation.JsonProperty;

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
}
