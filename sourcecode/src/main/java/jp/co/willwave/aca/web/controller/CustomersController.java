package jp.co.willwave.aca.web.controller;

import jp.co.willwave.aca.constants.UploadConstant;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.CustomersEntity;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.model.enums.CustomerType;
import jp.co.willwave.aca.service.CustomersService;
import jp.co.willwave.aca.service.DivisionService;
import jp.co.willwave.aca.utilities.ConversionUtil;
import jp.co.willwave.aca.utilities.FileUtil;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.CustomerForm;
import jp.co.willwave.aca.web.form.CustomerImportForm;
import jp.co.willwave.aca.web.form.DivisionSelectForm;
import jp.co.willwave.aca.web.form.SearchCustomerForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jp.co.willwave.aca.model.constant.Constant.Session.USER_LOGIN_INFO;

@Controller
@ControllerAdvice
public class CustomersController extends AbstractController {
	private final CustomersService customersService;
	private final DivisionService divisionService;

	private final FileUtil<CustomerForm> customerFormFileUtil;
	private final FileUtil<CustomerImportForm> customerImportFileUtil;

	@Autowired
	public CustomersController(CustomersService customersService, DivisionService divisionService,
							   FileUtil<CustomerForm> customerFormFileUtil, FileUtil<CustomerImportForm> customerImportFileUtil) {
		this.customersService = customersService;
		this.divisionService = divisionService;
		this.customerFormFileUtil = customerFormFileUtil;
		this.customerImportFileUtil = customerImportFileUtil;
	}

	@RequestMapping(value = "customersList", method = RequestMethod.GET)
	public String customersView(Integer offset, Integer maxResults, ModelMap model) throws CommonException {
		return commonView(offset, maxResults, CustomerType.CUSTOMER.getType(), model);
	}

	@RequestMapping(value = "garagesList", method = RequestMethod.GET)
	public String garagesView(Integer offset, Integer maxResults, ModelMap model) throws CommonException {
		return commonView(offset, maxResults, CustomerType.GARAGE.getType(), model);
	}

	@RequestMapping(value = "addCustomerView", method = RequestMethod.GET)
	public String addCustomerView(Model model) throws CommonException {
		SessionUtil.setAttribute(Constant.Session.CUSTOMER_TYPE, CustomerType.CUSTOMER.getType());

		//init form
		this.initAddCustomerForm(model);

		return "customers/addCustomers";
	}

	@RequestMapping(value = "addGarageView", method = RequestMethod.GET)
	public String addGarageView(Model model) throws CommonException {
		SessionUtil.setAttribute(Constant.Session.CUSTOMER_TYPE, CustomerType.GARAGE.getType());

		//init form
		this.initAddCustomerForm(model);

		return "customers/addCustomers";
	}

	@RequestMapping(value = "addCustomer", method = RequestMethod.POST)
	public String addCustomer(@Valid @ModelAttribute("customerForm") CustomerForm customerForm,
	                          BindingResult bindingResult, Model model) throws CommonException {
		List<Message> messages = validatorUtil.validate(bindingResult);
		if (CollectionUtils.isEmpty(messages)) {
			Integer customerType = (Integer) SessionUtil.getAttribute(Constant.Session.CUSTOMER_TYPE);
			customerForm.setCustomerType(CustomerType.getCustomerType(customerType));
			if (!customerForm.getIconMarkerFile().isEmpty()) {
				customerForm.setIconMarker(customerFormFileUtil.uploadIcon(customerForm.getIconMarkerFile()));
			}

			customersService.create(customerForm);
			return redirectToCustomerList();
		}

		model.addAttribute(Constant.Session.MESSAGES, messages);
		return "customers/addCustomers";
	}

	@RequestMapping(value = "searchCustomer", method = RequestMethod.GET)
	public String search(@ModelAttribute("searchCustomerForm") SearchCustomerForm searchCustomerForm,
						 BindingResult bindingResult) {
		SessionUtil.setAttribute("searchCustomerForm", searchCustomerForm);
		SessionUtil.setAttribute(Constant.Session.CUSTOMER_TYPE, searchCustomerForm.getIsCustomer() ? 1 : 2);
		return redirectToCustomerList();
	}

	@RequestMapping(value = "changeCustome/status/{id}", method = RequestMethod.GET)
	public String editStatusCustomer(@PathVariable("id") Long id) throws CommonException {
		Integer customerType = (Integer) SessionUtil.getAttribute(Constant.Session.CUSTOMER_TYPE);
		customersService.changeStatus(id, CustomerType.getCustomerType(customerType));
		return redirectToCustomerList();
	}

	@RequestMapping(value = "editCustomer/{id}", method = RequestMethod.GET)
	public String editCustomerView(@PathVariable("id") Long id, Model model) throws CommonException {
		CustomersEntity customersEntity = customersService.getCustomer(id);
		CustomerForm customerForm = ConversionUtil.mapper(customersEntity, CustomerForm.class);
		model.addAttribute("customerForm", customerForm);
		//init
		this.initEditCustomerForm(model, id);

		SessionUtil.setAttribute(Constant.Session.CUSTOMER_TYPE, CustomerType.CUSTOMER.getType());
		return "/customers/editCustomers";
	}

	@RequestMapping(value = "editGarage/{id}", method = RequestMethod.GET)
	public String editGarageView(@PathVariable("id") Long id, Model model) throws CommonException {
		CustomersEntity customersEntity = customersService.getCustomer(id);
		CustomerForm customerForm = ConversionUtil.mapper(customersEntity, CustomerForm.class);
		model.addAttribute("customerForm", customerForm);
		//init
		this.initEditCustomerForm(model, id);

		SessionUtil.setAttribute(Constant.Session.CUSTOMER_TYPE, CustomerType.GARAGE.getType());
		return "/customers/editCustomers";
	}

	@RequestMapping(value = "editCustomer", method = RequestMethod.POST)
	public String editCustomer(@Valid @ModelAttribute("customerForm") CustomerForm customerForm,
							   BindingResult bindingResult, Model model) throws CommonException {
		List<Message> messages = validatorUtil.validate(bindingResult);
		//init
		this.initEditCustomerForm(model, customerForm.getId());
		Integer customerType = (Integer) SessionUtil.getAttribute(Constant.Session.CUSTOMER_TYPE);
		customerForm.setCustomerType(CustomerType.getCustomerType(customerType));

		if (!customerForm.getIconMarkerFile().isEmpty()) {
			customerForm.setIconMarker(customerFormFileUtil.uploadIcon(customerForm.getIconMarkerFile()));
		}

		if (CollectionUtils.isEmpty(messages)) {
			customersService.update(customerForm);
			return redirectToCustomerList();
		}

		model.addAttribute(Constant.Session.MESSAGES, messages);
		return "customers/editCustomers";
	}

	@RequestMapping(value = "/deleteCustomer/{id}", method = RequestMethod.GET)
	public String deleteCustomer(@PathVariable("id") Long id) throws CommonException {
		Integer customerType = (Integer) SessionUtil.getAttribute(Constant.Session.CUSTOMER_TYPE);
		customersService.delete(id, CustomerType.getCustomerType(customerType));
		return redirectToCustomerList();
	}

	@RequestMapping(value = "/deleteGarage/{id}", method = RequestMethod.GET)
	public String deleteGarage(@PathVariable("id") Long id) throws CommonException {
		Integer customerType = (Integer) SessionUtil.getAttribute(Constant.Session.CUSTOMER_TYPE);
		customersService.delete(id, CustomerType.getCustomerType(customerType));
		return redirectToCustomerList();
	}

	@RequestMapping(value = "/searchCustomerPaging", method = RequestMethod.GET)
	public String searchPagingCustomer(Integer offset, Integer maxResults) {
		offset = (offset == null ? 0 : offset);
		maxResults = (maxResults == null ? Constant.MAX_SEARCH_RESULT : maxResults);
		return redirectToCustomerList() + "?offset=" + offset + "&maxResults=" + maxResults;
	}

	/**
	 * @return
	 * @throws CommonException
	 * @ExportFile
	 */
	@RequestMapping(value = "/exportCustomer", method = RequestMethod.GET)
	public void exportCustomer(HttpServletResponse response) throws CommonException, IOException {
		SearchCustomerForm searchCustomerForm = null;
		try {
			searchCustomerForm = (SearchCustomerForm) SessionUtil.getAttribute("searchCustomerForm");
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		if (searchCustomerForm == null) {
			searchCustomerForm = new SearchCustomerForm();
		}

		Integer customerType = (Integer) SessionUtil.getAttribute(Constant.Session.CUSTOMER_TYPE);
		Long totalCount = customersService.countSearchCustomer(searchCustomerForm,
			CustomerType.getCustomerType(customerType));


		List<CustomerForm> customers = customersService.searchCustomer(searchCustomerForm, null,
			Math.toIntExact(totalCount), CustomerType.getCustomerType(customerType));

		customerFormFileUtil.downloadCsvFile(response, customers, UploadConstant.CustomerForm, "customerList.csv");

	}

	/**
	 * @param file
	 * @return
	 * @throws CommonException
	 * @throws IOException
	 * @ImportFile
	 */
	@RequestMapping(value = "/importCustomer", method = RequestMethod.POST)
	public String importCustomer(@RequestParam("fileInput") MultipartFile file,
								 @RequestParam(value = "type", required = false) Integer type, Model model)
		throws CommonException {
		if (!file.isEmpty()) {
			// Read imported file.
			List<CustomerImportForm> customerFormList = customerImportFileUtil.readCsv(
				file, CustomerImportForm.class);

			if (!CollectionUtils.isEmpty(customerFormList)) {
				for (CustomerImportForm customerImportForm : customerFormList) {
					customerImportForm.setCustomerType(CustomerType.getCustomerType(type));
					CustomerForm customerForm = ConversionUtil.mapper(customerImportForm, CustomerForm.class);
                    customersService.create(customerForm);
				}
			}
		} else {
			List<Message> messages = new ArrayList<>();
			messages.add(messageSource.getMessage(Constant.ErrorCode.NOT_EMPTY, new String[]{"file.upload"}));
			model.addAttribute(Constant.Session.MESSAGES, messages);
		}

		return redirectToCustomerList();
	}

	private String commonView(Integer offset, Integer maxResults, Integer type,
	                          ModelMap model) throws CommonException {
		List<DivisionSelectForm> divisionList = customersService.getDivisionList(getLoginUserId());

		SearchCustomerForm searchCustomerForm = (SearchCustomerForm) SessionUtil.getAttribute("searchCustomerForm");
		searchCustomerForm = (searchCustomerForm == null ? new SearchCustomerForm() : searchCustomerForm);

		List<CustomerForm> customers = customersService.searchCustomer(
									searchCustomerForm, offset, maxResults, CustomerType.getCustomerType(type));
		Long totalCount = customersService.countSearchCustomer(searchCustomerForm, CustomerType.getCustomerType(type));
		//set edit, delete, change status permission
		customersService.setPermission(customers);

		model.addAttribute("customersList", customers);
		model.addAttribute("divisionsList", divisionList);
		model.addAttribute("searchCustomerForm", searchCustomerForm);
		model.addAttribute("offset", offset);
		model.addAttribute("maxResults", maxResults);
		model.addAttribute("count", totalCount);
		model.addAttribute(Constant.Session.CUSTOMER_TYPE,
				CustomerType.getCustomerType(type) == CustomerType.CUSTOMER);

		SessionUtil.setAttribute(Constant.Session.CUSTOMER_TYPE, type);
		return "customers/customersList";
	}

	private String redirectToCustomerList() {
		Integer customerType = (Integer) SessionUtil.getAttribute(Constant.Session.CUSTOMER_TYPE);
		String returnValue;
		if (customerType == null || customerType == 1){
			returnValue = "customersList";
		}
		else {
			returnValue = "garagesList";
		}
		return redirect(returnValue);
	}

	private void initAddCustomerForm(Model model) throws CommonException {
		Long userId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
		DivisionsEntity belongedDivision = customersService.getBelongedDivision(userId);

        CustomerForm customerForm = new CustomerForm();
        if (belongedDivision != null) {
            customerForm.setDivisionsId(belongedDivision.getId());
            customerForm.setDivisionName(belongedDivision.getDivisionName());
        }
        model.addAttribute("customerForm", customerForm);
	}

	private void initEditCustomerForm(Model model, Long customerId) throws CommonException {
		DivisionsEntity division = divisionService.findByCustomerId(customerId);
		model.addAttribute("division", Optional.ofNullable(division).orElse(new DivisionsEntity()));
		model.addAttribute("editPermission", customersService.loginUserHasEditPermission(customerId));
	}
}