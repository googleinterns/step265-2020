package resourceDisplay;

import java.util.List;

public class TableQueryObject {
    List<String> columnDisplays;
    List<String> columnNames;
    List<String> columnTypes;
    String Query;

    public TableQueryObject(List<String> columnDisplays, List<String> columnNames,
                            List<String> columnTypes, String query) {
        this.columnDisplays = columnDisplays;
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
        Query = query;
    }

}
