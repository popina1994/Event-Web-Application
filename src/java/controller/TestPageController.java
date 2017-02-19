/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author popina
 */
@ManagedBean
@ViewScoped
public class TestPageController {
    public static final String TEST_PAGE = "TestPage";
    
    public static String beginPageStatic()
    {
        return TEST_PAGE + HomePageController.REDIRECT_EXT;
    }
    
    public String beginPage()
    {
        return beginPageStatic();
    }
}
