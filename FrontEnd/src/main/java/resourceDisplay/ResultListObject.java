package resourceDisplay;

import java.util.List;

public class ResultListObject {
    List<String> columnDisplays;
    List<List<String>> columnResults;

    public ResultListObject(List<String> columnDisplays, List<List<String>> columnResults) {
        this.columnDisplays = columnDisplays;
        this.columnResults = columnResults;
    }
}
