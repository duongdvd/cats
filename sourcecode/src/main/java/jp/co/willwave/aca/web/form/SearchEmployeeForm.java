package jp.co.willwave.aca.web.form;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchEmployeeForm {
	private Long id;
	private String loginId;
	private String name;
	private String email;
	private String fixedPhone;
	private String mobilePhone;
	private Long divisionsId;
	private Integer roleId;
	private Boolean status;
	private List<Integer> roleIds = new ArrayList<>();
	private List<Long> divisionsIds = new ArrayList<>();
}
