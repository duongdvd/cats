package jp.co.willwave.aca.web.form;

import jp.co.willwave.aca.model.entity.DivisionsEntity;
import org.hibernate.validator.constraints.NotEmpty;

public class UserProfilesForm extends UsersForm {

	@NotEmpty(message = "E0001,profiles.title.divisionName")
	private String divisionName;

	@NotEmpty(message = "E0001,profiles.title.divisionAddress")
	private String divisionAddress;

	public void mappingForm(DivisionsEntity division) {
		if (division != null) {
			this.setDivisionName(division.getDivisionName());
			this.setDivisionAddress(division.getDivisionAddress());
		}
	}

	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	public String getDivisionAddress() {
		return divisionAddress;
	}

	public void setDivisionAddress(String divisionAddress) {
		this.divisionAddress = divisionAddress;
	}

}
