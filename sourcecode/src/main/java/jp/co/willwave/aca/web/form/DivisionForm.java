package jp.co.willwave.aca.web.form;


import lombok.Data;

@Data
public class DivisionForm {
    private Long id;
    private String divisionName;
    private String address;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
