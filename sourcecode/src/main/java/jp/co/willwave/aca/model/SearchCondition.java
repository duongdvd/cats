package jp.co.willwave.aca.model;

import jp.co.willwave.aca.model.enums.Type;
import jp.co.willwave.aca.utilities.CatStringUtil;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchCondition {
    private static final long LIMIT_DEFAULT = 100;
    private static final long SKIP_DEFAULT = 0;
    private Long limit;
    private Long skip;

    private List<String> groupColumns = new ArrayList<>();
    private List<String> orderColumns = new ArrayList<>();

    private List<ColumnCondition> columnConditions = new ArrayList<>();

    public void addColumnCondition(String columnName, String operator, Object value, Type type) {
        if (CatStringUtil.isNotEmpty(columnName)
                && CatStringUtil.isNotEmpty(operator)
                && CatStringUtil.isNotEmpty(value) && !"null".equals(value)) {
            ColumnCondition columnCondition = new ColumnCondition();
            columnCondition.setType(type);
            columnCondition.getConditions().add(new Condition(columnName, operator, value));
            columnConditions.add(columnCondition);
        }
    }

    public void addColumnCondition(String columnName, String operator) {
        if (CatStringUtil.isNotEmpty(columnName)
                && CatStringUtil.isNotEmpty(operator)) {
            ColumnCondition columnCondition = new ColumnCondition();
            columnCondition.getConditions().add(new Condition(columnName, operator, new ArrayList()));
            columnConditions.add(columnCondition);
        }
    }

    public void addColumnCondition(String columnName, String operator, List values, Type type) {
        if (CatStringUtil.isNotEmpty(columnName)
                && CatStringUtil.isNotEmpty(operator)
                && !CollectionUtils.isEmpty(values)) {
            ColumnCondition columnCondition = new ColumnCondition();
            columnCondition.setType(type);
            columnCondition.getConditions().add(new Condition(columnName, operator, values));
            columnConditions.add(columnCondition);
        }
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Long getSkip() {
        return skip;
    }

    public void setSkip(Long skip) {
        this.skip = skip;
    }

    public List<ColumnCondition> getColumnConditions() {
        return columnConditions;
    }

    public void setColumnConditions(List<ColumnCondition> columnConditions) {
        this.columnConditions = columnConditions;
    }

    public List<String> getGroupColumns() {
        return groupColumns;
    }

    public void setGroupColumns(List<String> groupColumns) {
        this.groupColumns = groupColumns;
    }

    public List<String> getOrderColumns() {
        return orderColumns;
    }

    public void setOrderColumns(List<String> orderColumns) {
        this.orderColumns = orderColumns;
    }
}
