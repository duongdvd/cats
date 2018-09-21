package jp.co.willwave.aca.dto.api;

import jp.co.willwave.aca.model.entity.DivisionsEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DivisionDTO {
    private Long divisionId;
    private String divisionName;
	private Long parentDivisionId;

    private boolean canEditFlg;

    private DivisionsEntity currentDivision;

    private List<DivisionsEntity> childrenList = new ArrayList<>();
    private List<DivisionsEntity> parentList = new ArrayList<>();

	public DivisionDTO() {
	}

	public DivisionDTO(Long divisionId, String divisionName, Long parentDivisionId, boolean canEditFlg) {
        this.divisionId = divisionId;
        this.divisionName = divisionName;
		this.parentDivisionId = parentDivisionId;
        this.canEditFlg = canEditFlg;
    }

    public List<DivisionsEntity> getDivisionRelatives() {
        List<DivisionsEntity> divisionsEntities = new ArrayList<>(childrenList);
        divisionsEntities.add(currentDivision);
        divisionsEntities.addAll(parentList);

        return divisionsEntities;
    }
}
