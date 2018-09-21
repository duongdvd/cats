package jp.co.willwave.aca.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.willwave.aca.config.QuickBlox;
import jp.co.willwave.aca.dto.api.quickblox.*;
import jp.co.willwave.aca.utilities.CryptUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Random;

@Service
@Transactional(rollbackFor = Exception.class)
public class QuickBloxServiceImpl implements QuickBloxService {

    private final QuickBlox quickBlox;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public QuickBloxServiceImpl(QuickBlox quickBlox, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.quickBlox = quickBlox;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    @Override
    public String getTokenQuickBlox() throws IOException {
        SessionRequestDTO sessionRequestDTO = new SessionRequestDTO();
        StringBuilder rawSignature = new StringBuilder();

        sessionRequestDTO.setApplication_id(quickBlox.getAppId());
        rawSignature.append("application_id");
        rawSignature.append("=");
        rawSignature.append(quickBlox.getAppId());

        sessionRequestDTO.setAuth_key(quickBlox.getAuthKey());
        rawSignature.append("&");
        rawSignature.append("auth_key");
        rawSignature.append("=");
        rawSignature.append(quickBlox.getAuthKey());
        Random r = new Random(System.currentTimeMillis());
        Integer nonce = ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));

        sessionRequestDTO.setNonce(nonce);
        rawSignature.append("&");
        rawSignature.append("nonce");
        rawSignature.append("=");
        rawSignature.append(nonce);

        Long timestamp = System.currentTimeMillis() / 1000;
        sessionRequestDTO.setTimestamp(timestamp);
        rawSignature.append("&");
        rawSignature.append("timestamp");
        rawSignature.append("=");
        rawSignature.append(timestamp);

        String signature = CryptUtil.hmacSha(quickBlox.getAuthSecret(), rawSignature.toString(), "HmacSHA1");
        sessionRequestDTO.setSignature(signature);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(sessionRequestDTO), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(quickBlox.getSessionGetUrl(), HttpMethod.POST, entity, String.class);

        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        SessionResponse sessionResponse = objectMapper.readValue(jsonObject.get("session").toString(), SessionResponse.class);

        return sessionResponse.getToken();
    }

    public QuickBloxUserResponse createQuickBloxUser(String companyCode, String rawLoginIdAndPasword) {
        try {
            String token = getTokenQuickBlox();
            QuickBloxUserRequest userRequest = new QuickBloxUserRequest();
            Random r = new Random(System.currentTimeMillis());
            Integer nonce = ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));

            String userName = CryptUtil.hmacSha(quickBlox.getAuthSecret(), rawLoginIdAndPasword + nonce, "HmacSHA1");

            r = new Random(System.currentTimeMillis());
            nonce = ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
            String password = CryptUtil.hmacSha(quickBlox.getAuthSecret(), rawLoginIdAndPasword + nonce, "HmacSHA1");

            userRequest.setLogin(userName);
            userRequest.setTag_list(companyCode);
            userRequest.setPassword(password);

            CreateQuickBloxRequest quickBloxRequest = new CreateQuickBloxRequest();
            quickBloxRequest.setUser(userRequest);


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add(quickBlox.getHeaderToken(), token);

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(quickBloxRequest), headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(quickBlox.getUserRegisterUrl(), HttpMethod.POST, entity, String.class);
            JSONObject jsonObject = new JSONObject(responseEntity.getBody());
            QuickBloxUserResponse quickBloxUserResponse = objectMapper.readValue(jsonObject.get("user").toString(), QuickBloxUserResponse.class);
            quickBloxUserResponse.setPassword(password);

            return quickBloxUserResponse;
        } catch (Exception e) {
            QuickBloxUserResponse quickBloxUserResponse = new QuickBloxUserResponse();
            quickBloxUserResponse.setErrorMessage(e.getMessage());
            return quickBloxUserResponse;
        }
    }

    @Override
    public String generateQuickBloxTagByCompanyId(Long i) {
        //return i > 0 && i < 27 ? String.valueOf((char) (i + 64)) : null;
        return "company" + i.toString();
    }
}
