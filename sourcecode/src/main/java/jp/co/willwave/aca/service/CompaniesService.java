package jp.co.willwave.aca.service;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.model.entity.UsersEntity;
import jp.co.willwave.aca.web.form.CompanyForm;
import jp.co.willwave.aca.web.form.SearchCompanyForm;

import java.text.ParseException;
import java.util.List;

public interface CompaniesService {
    DivisionsEntity getCompany(Long id) throws CommonException;

    List<DivisionsEntity> searchCompanies(SearchCompanyForm searchCompanyForm, Integer offset, Integer maxResults,
                                          Boolean havePaging) throws CommonException, ParseException;

    List<Message> createCompany(UsersEntity user, DivisionsEntity company) throws CommonException;

    List<Message> update(UsersEntity user, DivisionsEntity company) throws CommonException;


    List<Message> deleteCompany(Long id) throws CommonException;

    List<Message> updateCompany(DivisionsEntity DivisionsEntity) throws CommonException;

    List<Message> validateForm(CompanyForm companyForm, Boolean addNew);

}
