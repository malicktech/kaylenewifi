package com.kaylene.wifi.web.rest.errors;

public class CodeAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public CodeAlreadyUsedException() {
        super(ErrorConstants.LOGIN_ALREADY_USED_TYPE, "Code already used!", "userManagement", "codeexists");
    }
}
