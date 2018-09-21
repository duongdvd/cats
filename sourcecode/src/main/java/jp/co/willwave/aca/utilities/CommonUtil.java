package jp.co.willwave.aca.utilities;

import jp.co.willwave.aca.dto.api.TimeLineDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.*;
import jp.co.willwave.aca.model.enums.CallType;
import jp.co.willwave.aca.model.enums.LogsType;
import jp.co.willwave.aca.model.enums.UserRole;
import jp.co.willwave.aca.web.form.RoleForm;
import jp.co.willwave.aca.web.form.message.MessageAdminDeviceDTO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonUtil {

    public interface C<T> {
        void accept(T t) throws Exception;
    }

    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof Boolean) {
            return !(Boolean) value;
        }
        return StringUtils.isEmpty(value);
    }

    public static String getCompanyID(DivisionsEntity divisionsEntity) {
        String[] companyRootCode = divisionsEntity.getDivisionCode().split("_");
        return companyRootCode[1];
    }

    public static List<TimeLineDTO> mergeTimeline(List<MessagesEntity> messagesEntities,
                                                  List<RouteDetailEntity> routeDetails,
                                                  List<SafetyConfirmLogsEntity> safetyConfirmLogs,
                                                  List<EmergencyLogsEntity> emergencyLogs,
                                                  List<CallLogsEntity> callLogsEntities,
                                                  String loginUsers,
                                                  String loginDevice) {
        List<TimeLineDTO> timeLineDTOs = new ArrayList<>();
        List<MessageAdminDeviceDTO> message = new ArrayList<>();
        if (!CollectionUtils.isEmpty(messagesEntities)) {
            message = convertMessage(messagesEntities);
        }
        if (!CollectionUtils.isEmpty(message)) {
            message.forEach(m -> {
                TimeLineDTO timeLineAdminDTO = new TimeLineDTO();
                timeLineAdminDTO.setTitle(loginUsers);
                timeLineAdminDTO.setContent(m.getAdminMessage().getContent());
                timeLineAdminDTO.setIdObject(m.getAdminMessage().getId());
                timeLineAdminDTO.setOrderTime(m.getAdminMessage().getCreatedTime());
                timeLineAdminDTO.setTimeIn(m.getAdminMessage().getCreatedTime());
                timeLineAdminDTO.setType(LogsType.MESSAGE_ADMIN);
                timeLineDTOs.add(timeLineAdminDTO);
                if (m.getDevicesMessage() != null) {
                    TimeLineDTO timeLineDeviceDTO = new TimeLineDTO();
                    timeLineDeviceDTO.setTitle(loginDevice);
                    timeLineDeviceDTO.setContent(m.getDevicesMessage().getContent());
                    timeLineDeviceDTO.setIdObject(m.getDevicesMessage().getId());
                    timeLineDeviceDTO.setOrderTime(m.getAdminMessage().getCreatedTime());
                    timeLineDeviceDTO.setTimeIn(m.getDevicesMessage().getCreatedTime());
                    timeLineDeviceDTO.setType(LogsType.MESSAGE_DEVICE);
                    timeLineDTOs.add(timeLineDeviceDTO);
                }
            });
        }

        if (!CollectionUtils.isEmpty(routeDetails)) {
            routeDetails.forEach(routeDetail -> {
                TimeLineDTO timeLineDTO = new TimeLineDTO();
                timeLineDTO.setOrderTime(routeDetail.getCreateDate());
                timeLineDTO.setTimeIn(routeDetail.getArrivalTime());
                timeLineDTO.setTimeOut(routeDetail.getReDepartTime());
                timeLineDTO.setTitle(routeDetail.getCustomers().getName());
                timeLineDTO.setContent(routeDetail.getArrivalNote());
                timeLineDTO.setType(LogsType.ROUTE_DETAIL);
                timeLineDTO.setIdObject(routeDetail.getId());
                timeLineDTOs.add(timeLineDTO);
            });
        }

        if (!CollectionUtils.isEmpty(safetyConfirmLogs)) {
            safetyConfirmLogs.forEach(safety -> {
                TimeLineDTO timeLineDTO = new TimeLineDTO();
                timeLineDTO.setOrderTime(safety.getNotificationTime());
                timeLineDTO.setTimeIn(safety.getNotificationTime());
                timeLineDTO.setTitle(String.format("【安否通知】端末「%s」が「%d」分に「%s, %s」に停車した。", loginDevice,
                        safety.getStopTime(), safety.getLongitude(), safety.getLatitude()));
                timeLineDTO.setContent(String.format("システムから「%s」部署の安否確認担当者宛である「%s」にメール送信済み",
                       safety.getDivision().getDivisionName(), safety.getNotificationMail()));
                timeLineDTO.setType(LogsType.SAFETY_CONFIRM);
                timeLineDTO.setIdObject(safety.getId());
                timeLineDTOs.add(timeLineDTO);
            });
        }

        if (!CollectionUtils.isEmpty(emergencyLogs)) {
            emergencyLogs.forEach(emergency -> {
                TimeLineDTO timeLineDTO = new TimeLineDTO();
                timeLineDTO.setOrderTime(emergency.getNotificationTime());
                timeLineDTO.setTimeIn(emergency.getNotificationTime());
                timeLineDTO.setTitle(String.format("【緊急連絡】：『%s, %s』から送信",
                        emergency.getLongitude(), emergency.getLatitude()));
                timeLineDTO.setContent(emergency.getMessage());
                timeLineDTO.setType(LogsType.EMERGENCY);
                timeLineDTO.setIdObject(emergency.getId());
                timeLineDTOs.add(timeLineDTO);
            });
        }

        if (!CollectionUtils.isEmpty(callLogsEntities)) {
            callLogsEntities.forEach(callLogs -> {
                TimeLineDTO timeLineDTO = new TimeLineDTO();
                timeLineDTO.setOrderTime(callLogs.getStartTime());
                timeLineDTO.setTimeIn(callLogs.getStartTime());
                if (callLogs.getType() != null && callLogs.getType().equals(CallType.MISS)) {
                    if (!callLogs.getIsFromDevice()) {
                        timeLineDTO.setContent(String.format("【不在着信】管理者「%s」から＠「%s, %s」",
                                loginUsers, callLogs.getLongitude(), callLogs.getLatitude()));
                        timeLineDTO.setType(LogsType.MISS_CALL_ADMIN);
                    } else {
                        timeLineDTO.setContent(String.format("【不在着信】端末「%s」から＠「%s, %s」",
                                loginDevice, callLogs.getLongitude(), callLogs.getLatitude()));
                        timeLineDTO.setType(LogsType.MISS_CALL_DEVICE);
                    }
                } else {
                    timeLineDTO.setTimeOut(callLogs.getEndTime());
                    if (callLogs.getIsFromDevice()) {
                        timeLineDTO.setContent(String.format("【通話履歴】端末「%s」がオペレター「%s」に掛けた＠「%s, %s」",
                                loginDevice, loginUsers, callLogs.getLongitude(), callLogs.getLatitude()));
                        timeLineDTO.setType(LogsType.CALL_DEVICE_SUCCESS);
                    } else {
                        timeLineDTO.setContent(String.format("【通話履歴】オペレター「%s」が端末「%s」に掛けた＠「%s, %s」",
                                loginUsers, loginDevice, callLogs.getLongitude(), callLogs.getLatitude()));
                        timeLineDTO.setType(LogsType.CALL_ADMIN_SUCCESS);
                    }
                }

                timeLineDTO.setIdObject(callLogs.getId());
                timeLineDTOs.add(timeLineDTO);
            });
        }

        timeLineDTOs.sort((o1, o2) -> {
            Long o1Time = o1.getOrderTime().getTime();
            Long o2Time = o2.getOrderTime().getTime();
            int result = o1Time.compareTo(o2Time);
            if (result == 0) {
                return Long.compare(o1.getType().getType(), o2.getType().getType());
            }
            return result;
        });
        return timeLineDTOs;
    }

    private static List<MessageAdminDeviceDTO> convertMessage(List<MessagesEntity> messagesEntities) {
        Map<Long, MessageAdminDeviceDTO> mapMessage = new HashMap<>();
        messagesEntities.forEach(messages -> {
            if (messages.getIsFromDevice()) {
                MessageAdminDeviceDTO messageAdminDeviceDTO = mapMessage.get(messages.getParentMessageId());
                messageAdminDeviceDTO.setDevicesMessage(messages);
                mapMessage.put(messages.getParentMessageId(), messageAdminDeviceDTO);
            } else {
                MessageAdminDeviceDTO messageAdminDeviceDTO = new MessageAdminDeviceDTO();
                messageAdminDeviceDTO.setAdminMessage(messages);
                mapMessage.put(messages.getId(), messageAdminDeviceDTO);
            }
        });
        return new ArrayList<>(mapMessage.values());
    }

    public static List<RoleForm> getRoleListDefault() {
        List<RoleForm> roleList = new ArrayList<>();
        roleList.add(new RoleForm(UserRole.DIVISION_DIRECTOR.getRole(), UserRole.DIVISION_DIRECTOR.name()));
        roleList.add(new RoleForm(UserRole.OPERATOR.getRole(), UserRole.OPERATOR.name()));
        roleList.add(new RoleForm(UserRole.VIEWER.getRole(), UserRole.VIEWER.name()));
        return roleList;
    }

    public static List<RoleForm> getRoleOperatorAndView() {
        List<RoleForm> roleList = new ArrayList<>();
        roleList.add(new RoleForm(UserRole.OPERATOR.getRole(), UserRole.OPERATOR.name()));
        roleList.add(new RoleForm(UserRole.VIEWER.getRole(), UserRole.VIEWER.name()));
        return roleList;
    }

    public static <T> T[] asArray(T... t) {
        return t;
    }

    public static Long bigIntegerToLong(Object value) {
        return value == null? null : BigInteger.class.cast(value).longValue();
    }

    /**
     * <p>format a text file</p>
     * <p>eg: content file is "message ${param1} is ${params2}";
     * <i>param1</i> and <i>param2</i> is the keys of the params</p>
     * <p>the result is a string has been replaced by the param value in the map</p>
     * @param in input stream
     * @param params Map<param, value>
     * @return the string after format
     * @throws CommonException
     */
    public static String format(InputStream in, Map<String, Object> params) throws CommonException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName(Constant.UTF_8)));
        StringBuilder content = new StringBuilder();
        int characterInt;
        char character;
        boolean isEscape = false;
        try {
            while ((characterInt = reader.read()) != -1) {
                character = (char) characterInt;

                // when escape character, just append next character into the content
                if (isEscape) {
                    content.append(character);
                    isEscape = false;
                    continue;
                }

                // if escape character, set flag isEscape = tre
                if (character == '\\') {
                    isEscape = true;
                    continue;
                }

                // check if has expression
                if (character == '$') {
                    char nextChar = (char) reader.read();
                    if (nextChar == '{') {
                        // when has expression
                        String paramName = CommonUtil.getParamName(reader);
                        content.append(params.get(paramName));
                    } else {
                        //if has no expression, just continues append into the content
                        content.append(character).append(nextChar);
                    }
                    continue;
                }

                // else
                content.append(character);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    /**
     * get param name if the string start with '${' and end with '}'
     * <p>eg: ${param1} will be return "param1" </p>
     * @param reader reader
     * @return  the param name
     * @throws IOException
     * @throws CommonException
     */
    private static String getParamName(BufferedReader reader) throws IOException, CommonException {
        StringBuilder paramName = new StringBuilder();

        boolean endExpression = false;
        int characterInt;
        char character;
        while ((characterInt = reader.read()) != -1) {
            character = (char) characterInt;

            if (character == '}') {
                endExpression = true;
                break;
            }

            paramName.append(character);
        }

        if (endExpression && paramName.length() != 0) {
            return paramName.toString();
        } else {
            throw new CommonException("wrong expression syntax");
        }

    }
}
