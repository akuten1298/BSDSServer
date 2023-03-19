import java.util.ArrayList;
import java.util.List;

public class MatchesResponse {
    List<String> matchList;

    public MatchesResponse() {
        this.matchList = new ArrayList<>();
        this.matchList.add("Temp");
        this.matchList.add("Temp2");
    }

    public List<String> getMatchList() {
        return matchList;
    }
}
