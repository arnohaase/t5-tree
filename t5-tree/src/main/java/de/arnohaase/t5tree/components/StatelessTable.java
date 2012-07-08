package de.arnohaase.t5tree.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.Translator;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.BeforeRenderTemplate;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.corelib.components.Hidden;
import org.apache.tapestry5.services.FormSupport;
import org.apache.tapestry5.services.TranslatorSource;

import de.arnohaase.t5tree.statelesstable.DataModel;


//TODO improve styling for sorted column headers
//TODO reorder, remove col
@SupportsInformalParameters
@SuppressWarnings("unused")
public class StatelessTable {
    @Parameter private TranslatorSource translatorSource;
    
    @Property @Parameter(required=true) private DataModel<Object, Object> model;

    @Parameter
    @Property private Object curRow;

    @Property @Parameter(defaultPrefix="literal", value="table table-striped table-condensed") private String tableCssClass;
    @Property @Parameter(defaultPrefix="literal", value="") private String pagingCssClass;

    @Property @Parameter(defaultPrefix="literal", value="true") private boolean showTopPaging;
    @Property @Parameter(defaultPrefix="literal", value="true") private boolean showBottomPaging;
    @Property @Parameter(defaultPrefix="literal", value="right") private String pagingAlignment;
    
    /**
     * comma separated list of filter-enabled column ids. Default: no columns enabled
     */
    @Parameter(defaultPrefix="literal") private String filteredColumns;
    
    @Parameter private Object context;

    @Parameter @Property private String curColumnId;
    @Parameter @Property private Object curColumnValue;
    @Parameter @Property private String sortColumn;
    @Parameter @Property private boolean sortAscending;
    
    @Parameter(name="firstRow") @Property private int indFirstRow;
    @Parameter(value="20") @Property private int pageSize;
    
    @Parameter @Property private ValueEncoder<?> rowValueEncoder;
    
    @Parameter(defaultPrefix=BindingConstants.LITERAL) @Property private String dirtyClass;
    @Parameter @Property private String submitConfirmQuestion;
    
    @Inject private FormSupport formSupport;
    @Inject private Block defaultCellRenderer;
    
    @InjectComponent("sortColumn") private Hidden sortColumnField;
    @InjectComponent("sortAscending") private Hidden sortAscendingField;

    @Inject private Block defaultPaging;
    
    @Inject private ComponentResources resources;

    public boolean getHasRowValueEncoder() {
        return rowValueEncoder != null;
    }
    
    @Cached
    private Map<String, String> getColumnFilterValues() {
        return new HashMap<String, String>();
    }
    
    @Cached
    public List<Object> getRows() {
        final List<String> sortColumns = new ArrayList<String>();
        if(sortColumn != null && sortColumn.length() > 0) {
            sortColumns.add(sortColumn);
        }
        
        return model.getRows(indFirstRow, pageSize, sortColumns, Arrays.asList(sortAscending), getColumnFilterValues(), context);
    }
    
    @Cached
    public int getTotalNumRows() {
        return model.getTotalNumRows(getColumnFilterValues(), context);
    }
    
    @Cached
    public int getTotalNumRowsUnfiltered() {
        return model.getTotalNumRows(new HashMap<String, String>(), context);
    }
    
    @Cached
    private Set<String> getFilteredColumns() {
        final Set<String> result = new HashSet<String>();
        
        if (filteredColumns == null) {
            return result;
        }
        
        for(String columnId: filteredColumns.split(",")) {
            columnId = columnId.trim();
            if (model.getColumnIds().contains(columnId)) {
                result.add(columnId);
            }
        }
        return result;
    }
                     
    public boolean getHasSubmitConfirm() {
        return submitConfirmQuestion != null;
    }
    
    public String getColumnSortCssClass() {
        if (model.isSortable(curColumnId)) {
            if (curColumnId.equals(sortColumn)) {
                return sortAscending ? "icon-chevron-down" : "icon-chevron-up";
            }
            else {
                return "icon-resize-vertical";
            }
        }
        else {
            return "";
        }
    }
    
    public Block getPagingBlock() {
        return defaultPaging;
    }

    public boolean getColumnFiltersEnabled() {
        return ! getFilteredColumns().isEmpty();
    }
    
    public boolean getHasFilter() {
        return getFilteredColumns().contains(curColumnId);
    }
    
    public String getColumnFilter() {
        return getColumnFilterValues().get(curColumnId);
    }
    
    public void setColumnFilter(String filter) {
        getColumnFilterValues().put(curColumnId, filter);
    }
    
    public int getIndFirstRowOneBased() {
        return indFirstRow  + 1;
    }
    
    public int getIndLastRowOneBased() {
        return Math.min(indFirstRow + pageSize, getTotalNumRows());
    }
    
    public String getCurColumnLabel() {
        return model.getColumnLabel(curColumnId);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Block getCurCellRenderer() {
        // side effect: initialize the current column value whenever the processing of a new cell starts
        curColumnValue = model.getColumnValue(curColumnId, curRow);
        if (translatorSource != null) {
            final Translator translator = translatorSource.findByType(model.getColumnType(curColumnId));
            if (translator != null) {
                curColumnValue = translator.toClient(curColumnValue);
            }
        }
        
        final Block paramBlock = resources.getBlockParameter(curColumnId + "Cell");
        if (paramBlock != null) {
            return paramBlock;
        }
        return defaultCellRenderer;
    }
    
    private String newAscendingValue() {
        if (curColumnId.equals(sortColumn)) {
            return String.valueOf(! sortAscending);
        }
        else {
            return "true";
        }
    }
    
    public String getJsForColumnSort() {
        if (!model.isSortable(curColumnId)) {
            return "";
        }
        
        final StringBuilder result = new StringBuilder();
        
        result.append(String.format ("$j('#%s').val('%s');", sortColumnField.getClientId(), curColumnId));
        result.append(String.format ("$j('#%s').val('%s');", sortAscendingField.getClientId(), newAscendingValue()));
        result.append(String.format ("$j('#%s').get(0).performSubmit(event);", formSupport.getClientId()));
        
        return result.toString();
    }
    
    @BeforeRenderTemplate
    public void beforeRender() {
        indFirstRow = Math.min(indFirstRow, getLastPageIndex());
    }
    
    @OnEvent(component="firstPage", value=EventConstants.SELECTED)
    public void onFirstPage() {
        indFirstRow = 0;
    }

    @OnEvent(component="prevPage", value=EventConstants.SELECTED)
    public void onPrevPage() {
        indFirstRow = Math.max(0,  indFirstRow - pageSize);
    }

    @OnEvent(component="nextPage", value=EventConstants.SELECTED)
    public void onNextPage() {
        indFirstRow = Math.min(indFirstRow + pageSize, getLastPageIndex());
    }
    
    private int getLastPageIndex() {
        int result = getTotalNumRows() -1;
        return result - result%pageSize;
    }
    
    @OnEvent(component="lastPage", value=EventConstants.SELECTED)
    public void onLastPage() {
        indFirstRow = getLastPageIndex();
    }
}



