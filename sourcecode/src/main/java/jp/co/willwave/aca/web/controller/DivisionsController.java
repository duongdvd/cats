package jp.co.willwave.aca.web.controller;

import jp.co.willwave.aca.common.LogicException;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.exception.LogicWebException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.model.entity.UsersEntity;
import jp.co.willwave.aca.service.DivisionService;
import jp.co.willwave.aca.service.UserService;
import jp.co.willwave.aca.utilities.ConversionUtil;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.utilities.ValidatorUtil;
import jp.co.willwave.aca.web.form.division.DivisionForm;
import jp.co.willwave.aca.web.form.division.TreeDivisionView;
import jp.co.willwave.aca.web.form.division.UserForm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DivisionsController extends AbstractController {
    private final DivisionService divisionService;
    private final UserService userService;

    public DivisionsController(DivisionService divisionService, UserService userService) {
        this.divisionService = divisionService;
        this.userService = userService;
    }

    @RequestMapping(value = {"divisionList"}, method = RequestMethod.GET)
    public String viewDivisionList(ModelMap model) {
        DivisionForm divisionForm = new DivisionForm();
        model.addAttribute("divisionForm", divisionForm);
        return "division/division";
    }


    @PostMapping(value = {"deleteDivision/{divisionId}"})
    public ResponseEntity addOrEditDivisionView(@PathVariable("divisionId") Long divisionId) throws CommonException {
        List<Message> messages = new ArrayList<>();
        try {
            divisionService.deleteDivision(divisionId);
        } catch (LogicWebException e) {
            messages.add(e.getErrorMessage());
        } catch (CommonException e) {
            throw e;
        }
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/division")
    public ResponseEntity getTreeDivision() {
        try {
            Long divisionRootId = ((UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO)).getDivisionsId();
            List<DivisionsEntity> divisionsEntities = divisionService.searchChildren(divisionRootId);
            TreeDivisionView divisionNode = new TreeDivisionView();
            if (!CollectionUtils.isEmpty(divisionsEntities)) {
                divisionNode = convertTreeDivisionView(divisionsEntities, divisionRootId);
            }
            divisionNode.visitedTreeDivision();
            divisionNode.getChildren().forEach(TreeDivisionView::visitedTreeDivision);
            return ResponseEntity.ok(divisionNode);
        } catch (CommonException e) {
            logger.error("Error getTreeDivision ", e);
            List<Message> messages = new ArrayList<>();
            messages.add(e.getErrorMessage());
            return ResponseEntity.ok(messages);
        }
    }

    @GetMapping("/division/{id}")
    public ResponseEntity getDivisionEdit(@PathVariable("id") Long id) {
        try {
            DivisionsEntity divisionsEntity = divisionService.findById(id);
            DivisionForm divisionForm = ConversionUtil.mapper(divisionsEntity, DivisionForm.class);
            divisionForm.setDivisionStatus(divisionsEntity.getStatus());
            if (divisionForm.getParentDivisionsId() != null) {
                DivisionsEntity divisionParent = divisionService.findById(divisionForm.getParentDivisionsId());
                divisionForm.setParentDivisionName(divisionParent.getDivisionName());
            }
            return ResponseEntity.ok(divisionForm);
        } catch (CommonException e) {
            logger.error("Error getDivisionEdit ", e);
            List<Message> messages = new ArrayList<>();
            messages.add(e.getErrorMessage());
            return ResponseEntity.ok(messages);
        }
    }

    @GetMapping("/division/{id}/users")
    public ResponseEntity getListUsers(@PathVariable("id") Long id) {
        try {
            List<UserForm> userList = userService.getUserByDivisionExceptViewer(id);
            return ResponseEntity.ok(userList);
        } catch (CommonException e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PostMapping("/addDivision")
    public ResponseEntity addDivision(@RequestBody DivisionForm divisionForm) {
        List<Message> messages = new ArrayList<>();
        try {
            messages = validateUser(divisionForm);
            messages.addAll(validateDivision(divisionForm, false));
            if (!CollectionUtils.isEmpty(messages)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messages);
            }
            DivisionsEntity divisionsEntity = ConversionUtil.mapper(divisionForm, DivisionsEntity.class);
            divisionsEntity.setStatus(divisionForm.getDivisionStatus());
            divisionService.createDivision(divisionsEntity, ConversionUtil.mapper(divisionForm, UsersEntity.class));
        } catch (LogicException e) {
            logger.error("Error exception add Division ", e);
            messages.add(e.getErrorMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messages);
        } catch (Exception e) {
            logger.error("Error exception add Division ", e);
            messages.add(messageSource.get(e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messages);
        }
        return ResponseEntity.ok(divisionForm);
    }

    @PostMapping("/editDivision")
    public ResponseEntity editDivision(@RequestBody DivisionForm divisionForm) throws Exception {
        List<Message> messages = new ArrayList<>();
        try {
            messages = validateDivision(divisionForm, true);
            if (!CollectionUtils.isEmpty(messages)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messages);
            }
            DivisionsEntity divisionsEntity = ConversionUtil.mapper(divisionForm, DivisionsEntity.class);
            divisionsEntity.setStatus(divisionForm.getDivisionStatus());
            divisionService.updateDivision(divisionsEntity);

            // Saving division info into login user session.
            UserInfo loginUser = SessionUtil.getLoginUser();
            DivisionsEntity divisionsLoginUser = divisionService.findById(loginUser.getDivisionsId());
            loginUser.setDivisionAddress(divisionsLoginUser.getDivisionAddress());
            SessionUtil.setAttribute(Constant.Session.USER_LOGIN_INFO, loginUser);
        } catch (LogicException e) {
            logger.error("Error exception add Division ", e);
            messages.add(e.getErrorMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(messages);
        }
        return ResponseEntity.ok(divisionForm);
    }

    private List<Message> validateUser(DivisionForm divisionForm) {
        List<Message> messages = new ArrayList<>();
        if (StringUtils.isEmpty(divisionForm.getLoginId())) {
            messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"employee.LoginId"}));
        }
        if (StringUtils.isEmpty(divisionForm.getFirstName())) {
            messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"employee.firstName"}));
        }
        if (StringUtils.isEmpty(divisionForm.getLastName())) {
            messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"employee.lastName"}));
        }
        if (!StringUtils.isEmpty(divisionForm.getUserEmail()) &&
                !ValidatorUtil.validateEmail(divisionForm.getUserEmail())) {
            messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.FORMAT_INVALID, new String[]{"employee.email"}));
        }
        return messages;
    }

    private List<Message> validateDivision(DivisionForm divisionForm, boolean isUpdate) {
        List<Message> messages = new ArrayList<>();
        if (isUpdate) {
            if (divisionForm.getId() == null) {
                messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY,
                        new String[]{"division.division"}));
            }
            if (divisionForm.getUsersId() == null) {
                messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY,
                        new String[]{"division.usersManaged"}));
            }
        }
        if (StringUtils.isEmpty(divisionForm.getDivisionAddress())) {
            messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY,
                    new String[]{"division.divisionAddress"}));
        }
        if (divisionForm.getDivisionStatus() == null) {
            messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY,
                    new String[]{"division.divisionStatus"}));
        }
        if (StringUtils.isEmpty(divisionForm.getDivisionName())) {
            messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"division.divisionName"}));
        }
        if (!isUpdate && divisionForm.getParentDivisionsId() == null ){
            messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"division.parentDivision"}));
        }
        return messages;
    }

    private TreeDivisionView convertTreeDivisionView(List<DivisionsEntity> sourceDivision, Long divisionIdRoot) {
        Map<Long, TreeDivisionView> nodeMap = new HashMap<>();
        sourceDivision.forEach(division -> {
            Long parentId = division.getParentDivisionsId();
            TreeDivisionView treeDivisionNodeParent = nodeMap.get(parentId);
            if (treeDivisionNodeParent == null) {
                treeDivisionNodeParent = new TreeDivisionView();
            }
            TreeDivisionView treeDivisionNodeChild = nodeMap.get(division.getId());
            if (treeDivisionNodeChild == null) {
                treeDivisionNodeChild = new TreeDivisionView();
                treeDivisionNodeChild.setText(division.getDivisionName());
                treeDivisionNodeChild.setId(division.getId());
            } else {
                treeDivisionNodeChild.setText(division.getDivisionName());
                treeDivisionNodeChild.setId(division.getId());
            }
            nodeMap.put(division.getId(), treeDivisionNodeChild);
            treeDivisionNodeParent.addChild(treeDivisionNodeChild);
            nodeMap.put(parentId, treeDivisionNodeParent);
        });
        return nodeMap.get(divisionIdRoot);
    }
}
