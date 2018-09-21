package jp.co.willwave.aca.web.form;

import lombok.Data;

@Data
public class DivisionSelectForm {
    private Long id;
    private String name;

    public DivisionSelectForm() {
    }

    public DivisionSelectForm(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
