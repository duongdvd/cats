package jp.co.willwave.aca.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MapController  extends AbstractController {
    @RequestMapping(value = {"viewMap"}, method = RequestMethod.GET)
    public String viewMap() {
        return "map";
    }
}
