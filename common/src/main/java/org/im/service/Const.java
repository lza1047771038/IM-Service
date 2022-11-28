package org.im.service;

public interface Const {
    interface Param {
        String PARAM_USER_SESSION_ID = "sessionId";
        String PARAM_USER_ID = "userId";

        String PARAM_CONTENT = "content";
    }

    interface RequestMethod {
        String USER_AUTHORIZATION = "user.authorization.token";

        String MESSAGE_TEXT = "message.text";
    }

    interface ResponseMethod {
        String USER_AUTHORIZATION = "user.authorization";

        String MESSAGE_TEXT = "message.text";
    }
}
