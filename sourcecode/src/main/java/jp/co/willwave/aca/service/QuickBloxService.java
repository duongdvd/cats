package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dto.api.quickblox.QuickBloxUserResponse;

import java.io.IOException;

public interface QuickBloxService {

    String getTokenQuickBlox() throws IOException;

    QuickBloxUserResponse createQuickBloxUser(String companyCode, String rawLoginIdAndPasword);

    String generateQuickBloxTagByCompanyId(Long i);
}
