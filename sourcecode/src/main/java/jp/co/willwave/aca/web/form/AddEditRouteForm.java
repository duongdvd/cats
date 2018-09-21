package jp.co.willwave.aca.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class AddEditRouteForm {
	private Long id;

	@NotEmpty(message = "E0001,route.routeName")
	private String name;

	@NotNull(message = "E0001,route.plateNumber")
	private Long devicesId;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "E0001,route.startDate")
	private Date startDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "E0001,route.endDate")
	private Date endDate;

	private String routeMemo;

	@NotNull(message = "E0001,route.startGarage")
	private Long startGarageId;

	@NotNull(message = "E0001,route.endGarage")
	private Long endGarageId;

	@NotNull(message = "E0001,route.status")
	private Boolean active;

	private List<Long> garageIds = new ArrayList<>();
	private List<Long> customerIds = new ArrayList<>();
	private String mapDetails;

	private Boolean fromCarStatus = Boolean.FALSE;
	/** <b>true</b> when update successful (case from car status) */
	private Boolean updateCarStatusRoute = Boolean.FALSE;
	private Boolean editPermission = Boolean.TRUE;
	private String routeDetails;
}
