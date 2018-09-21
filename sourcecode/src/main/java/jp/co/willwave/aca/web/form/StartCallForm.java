package jp.co.willwave.aca.web.form;

import lombok.Data;

@Data
public class StartCallForm {
    private Long fromCallId;
    private Long toCallId;
    private String toCallName;
    private String userTags;
}
