package jp.co.willwave.aca.web.restController;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.service.UserService;
import jp.co.willwave.aca.web.controller.AbstractController;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserRestController extends AbstractController {
    private static Logger logger = Logger.getLogger(UserRestController.class);


    @Autowired
    private UserService userService;


    /**
     * @return
     * @author RELIPA SOFTWARE
     * @since 2018
     */
    @RequestMapping(value = "/user/{id}", method = {RequestMethod.GET})
    public String getUser(@PathVariable("id") Long id) throws CommonException {
        logger.info("users");
        userService.getUser(id);

        return "users";
    }

}
