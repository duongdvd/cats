package jp.co.willwave.aca.service;

import jp.co.willwave.aca.constants.StatusEnum;
import jp.co.willwave.aca.dao.DivisionsDao;
import jp.co.willwave.aca.dao.UsersDao;
import jp.co.willwave.aca.dto.api.quickblox.QuickBloxUserResponse;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.model.entity.UsersEntity;
import jp.co.willwave.aca.model.enums.UserRole;
import jp.co.willwave.aca.utilities.CatsMessageResource;
import jp.co.willwave.aca.utilities.PasswordUtils;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.CompanyForm;
import jp.co.willwave.aca.web.form.SearchCompanyForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static jp.co.willwave.aca.model.constant.Constant.Session.USER_LOGIN_INFO;

@Service
@Transactional(rollbackFor = Exception.class)
public class CompaniesServiceImpl implements CompaniesService {

    private final DivisionsDao companiesDao;
    private final UsersDao usersDao;
    private final UserService userService;
    private final CatsMessageResource messageSource;
    private final QuickBloxService quickBloxService;

    @Autowired
    public CompaniesServiceImpl(DivisionsDao companiesDao, UsersDao usersDao, UserService userService, CatsMessageResource messageSource, QuickBloxService quickBloxService) {
        this.companiesDao = companiesDao;
        this.usersDao = usersDao;
        this.userService = userService;
        this.messageSource = messageSource;
        this.quickBloxService = quickBloxService;
    }


    @Override
    public DivisionsEntity getCompany(Long id) throws CommonException {
        return companiesDao.findById(id, DivisionsEntity.class);
    }

    @Override
    public List<DivisionsEntity> searchCompanies(SearchCompanyForm searchCompanyForm, Integer offset, Integer maxResults, Boolean havePaging) throws CommonException, ParseException {
        return companiesDao.findCompanyList(searchCompanyForm, offset, maxResults, havePaging);
    }

    @Override
    public List<Message> createCompany(UsersEntity user, DivisionsEntity company) throws CommonException {
        List<Message> messages = new ArrayList<>();
        Date date = new Date();
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        user.setRoleId(UserRole.COMPANY_DIRECTOR.getRole());
        messages.addAll(userService.createUser(user));
        //user already exists
        if (!CollectionUtils.isEmpty(messages)) return messages;

        company.setUsersId(user.getId());
        company.setCreateBy(userLoginId);
        company.setCreateDate(date);
        company.setUpdateDate(date);
        companiesDao.insert(company);
        user.setDivisionsId(company.getId());
        user.setCreateDate(date);
        user.setUpdateDate(date);
        usersDao.update(user);
        company.setUsersId(user.getId());
        company.setDivisionCode("D_" + company.getId());
        companiesDao.update(company);

//            String companyCode = quickBloxService.generateQuickBloxTagByCompanyId(company.getId());
//            QuickBloxUserResponse quickBloxUserResponse = quickBloxService.createQuickBloxUser(companyCode, "D_" + company.getId());
//
//            if (quickBloxUserResponse.getErrorMessage() != null) {
//                messages.add(messageSource.getMessage(Constant.ErrorCode.QUICKBLOX_ERROR, new String[]{quickBloxUserResponse.getErrorMessage()}));
//                throw new CommonException(quickBloxUserResponse.getErrorMessage());
//            }
//            user.setCallName(quickBloxUserResponse.getLogin());
//            user.setCallPassword(quickBloxUserResponse.getPassword());
//            user.setUserTags(quickBloxUserResponse.getUser_tags());
//            user.setCallId(quickBloxUserResponse.getId());
//
//            usersDao.update(user);

        return messages;
    }

    @Override
    public List<Message> update(UsersEntity user, DivisionsEntity company) throws CommonException {
        Date date = new Date();
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        company.setUpdateBy(userLoginId);
        company.setUpdateDate(date);
        companiesDao.update(company);

        if (StringUtils.isNotBlank(user.getPasswd())) {
            user.setPasswd(PasswordUtils.generateSecurePassword(user.getPasswd(), Constant.SHA_512_PASSWORD_KEY));
        }
        user.setUpdateBy(userLoginId);
        user.setUpdateDate(date);
        usersDao.update(user);
        return new ArrayList<>();
    }

    @Override
    public List<Message> deleteCompany(Long id) throws CommonException {
        List<Message> messages = new ArrayList<>();
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        DivisionsEntity company = companiesDao.findById(id, DivisionsEntity.class);
        if (company == null) {
            messages.add(messageSource.getMessage(Constant.ErrorCode.NOT_FOUND_USERID, new String[]{id.toString()}));
            return messages;
        }

        List<DivisionsEntity> companyChildList = companiesDao.findDivisionChildListByDivisionId(id);

        if (!CollectionUtils.isEmpty(companyChildList)) {
            messages.add(messageSource.getMessage(Constant.ErrorCode.COMPANY_EXIST_DIVISION, new Object[]{id}));
            return messages;
        }
        company.setUpdateBy(userLoginId);
        companiesDao.delete(company);

        UsersEntity usersEntity = usersDao.findById(company.getUsersId(), UsersEntity.class);
        usersEntity.setUpdateBy(userLoginId);
        usersDao.delete(usersEntity);

        return new ArrayList<>();
    }

    @Override
    public List<Message> updateCompany(DivisionsEntity company) throws CommonException {
        List<Message> messages = new ArrayList<>();
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();

        if (company.getId() == null) {
            messages.add(messageSource.getMessage(Constant.ErrorCode.NOT_EMPTY, new String[]{"company.notId"}));
            return messages;
        }
        DivisionsEntity companyExist = companiesDao.findById(company.getId(), DivisionsEntity.class);
        if (companyExist == null) {
            messages.add(messageSource.getMessage(Constant.ErrorCode.NOT_FOUND_COMPANY, new Object[]{company.getId()}));
            return messages;
        }
        company.setUpdateBy(userLoginId);
        companiesDao.update(company);
        return new ArrayList<>();
    }

    @Override
    public List<Message> validateForm(CompanyForm companyForm, Boolean addNew) {
        List<Message> messages = new ArrayList<>();

        if (addNew) {
            if (companyForm.getPasswd() == null || companyForm.getConfirmPasswd() == null) {
                messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"password.passwordconfirmPassword"}));
            }
            if (StringUtils.isBlank(companyForm.getPasswd())) {
                messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"password.password"}));
            }
            if (StringUtils.isBlank(companyForm.getConfirmPasswd())) {
                messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"password.confirmPassword"}));
            }
            if (!companyForm.getPasswd().equals(companyForm.getConfirmPasswd())) {
                messages.add(messageSource.get(Constant.ErrorCode.PASS_CONFIRM_NOT_MATCH));
            }
        } else {
            if (companyForm.getPasswd() != null || companyForm.getConfirmPasswd() != null) {
                if (StringUtils.isNotBlank(companyForm.getPasswd()) || StringUtils.isNotBlank(companyForm.getConfirmPasswd())) {
                    if (companyForm.getPasswd() == null || companyForm.getConfirmPasswd() == null) {
                        messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"password.passwordconfirmPassword"}));
                    }
                    if (StringUtils.isBlank(companyForm.getPasswd())) {
                        messages.add(messageSource.getMessage(Constant.ErrorCode.NOT_EMPTY, new String[]{"password.password"}));
                    }
                    if (StringUtils.isBlank(companyForm.getConfirmPasswd())) {
                        messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"password.confirmPassword"}));
                    }
                    if (!companyForm.getPasswd().equals(companyForm.getConfirmPasswd())) {
                        messages.add(messageSource.get(Constant.ErrorCode.PASS_CONFIRM_NOT_MATCH));
                    }
                }
            }
        }

        if (companyForm.getStatus() == null || !StatusEnum.contains(companyForm.getStatus())) {
            messages.add(messageSource.getMessage(Constant.ErrorCode.FORMAT_INVALID, new String[]{"status"}));
        }

        return messages;
    }

}
