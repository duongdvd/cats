package jp.co.willwave.aca.web.form.division;

import java.util.ArrayList;
import java.util.List;


public class TreeDivisionView {
    private String text;
    private Long id;
    private State state = new State();
    private List<TreeDivisionView> children = new ArrayList<>();

    public void addChild(TreeDivisionView divisionNode) {
        children.add(divisionNode);
    }

    public void removeChild(TreeDivisionView divisionNode) {
        children.remove(divisionNode);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<TreeDivisionView> getChildren() {
        return children;
    }

    public void setChildren(List<TreeDivisionView> children) {
        if (children != null) {
            this.children = children;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void visitedTreeDivision() {
        this.state.setOpened(true);
    }
}
