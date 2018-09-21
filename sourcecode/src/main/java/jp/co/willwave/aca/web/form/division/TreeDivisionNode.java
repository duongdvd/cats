package jp.co.willwave.aca.web.form.division;


import jp.co.willwave.aca.model.entity.DivisionsEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TreeDivisionNode implements TreeDivision {
    private DivisionsEntity data;
    private List<TreeDivisionNode> childrenDivision = new ArrayList<>();

    public TreeDivisionNode(DivisionsEntity data) {
        this.data = data;
    }

    @Override
    public void addChild(TreeDivisionNode divisionNode) {
        childrenDivision.add(divisionNode);
    }

    @Override
    public void removeChild(TreeDivisionNode divisionNode) {
        childrenDivision.remove(divisionNode);
    }

    public List<TreeDivisionNode> getChildrenDivision() {
        return childrenDivision;
    }

    public void setChildrenDivision(List<TreeDivisionNode> childrenDivision) {
        this.childrenDivision = childrenDivision;
    }


    public DivisionsEntity getData() {
        return data;
    }

    public void setData(DivisionsEntity data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TreeDivisionNode)) return false;
        TreeDivisionNode that = (TreeDivisionNode) o;
        return Objects.equals(data.getId(), that.getData().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getData().getId(), 58);
    }
}
