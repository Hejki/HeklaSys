package org.hejki.heklasys.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * TODO Document me.
 *
 * @author Petr Hejkal
 */
@Controller
@RequestMapping("/house")
public class HouseController {

    @RequestMapping
    public String index(Model model) {
        return "house/index";
    }
}
