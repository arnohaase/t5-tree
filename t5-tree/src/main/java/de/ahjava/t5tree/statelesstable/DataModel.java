package de.ahjava.t5tree.statelesstable;

import java.util.List;
import java.util.Map;


public interface DataModel<R, C> {
    List<String> getColumnIds();
    Class<?> getColumnType(String curColumnId);
    String getColumnLabel(String columnId); 
    boolean isSortable(String columnId);
    
    int getTotalNumRows(Map<String, String> filter, C context);
    List<R> getRows(int offset, int pageSize, List<String> sortColumns, List<Boolean> ascending, Map<String, String> filter, C context); 
    
    Object getColumnValue(String columnId, R row);
}
