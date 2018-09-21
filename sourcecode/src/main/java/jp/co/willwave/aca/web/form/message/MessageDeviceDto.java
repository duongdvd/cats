package jp.co.willwave.aca.web.form.message;

import jp.co.willwave.aca.model.entity.MessagesEntity;
import lombok.Data;

@Data
public class MessageDeviceDto {
	private MessagesEntity messagesEntity;
	private String deviceLoginId;

	public MessageDeviceDto(MessagesEntity messagesEntity, String deviceLoginId) {
		this.messagesEntity = messagesEntity;
		this.deviceLoginId = deviceLoginId;
	}
}
