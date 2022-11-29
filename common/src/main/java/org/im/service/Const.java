package org.im.service;

public interface Const {
    interface Param {
        String PARAM_USER_SESSION_ID = "sessionId";

        String PARAM_CONTENT = "content";

        String PARAM_METHOD = "method";
        String PARAM_TO_USER = "toUser";
        String PARAM_FROM_USER = "fromUser";
        String PARAM_TO_USER_ID = "toUserId";
        String PARAM_FROM_USER_ID = "fromUserId";
        String PARAM_UUID = "uuid";
        String PARAM_REMOTE_EXTENSION = "removeExt";
        String PARAM_CLIENT_EXTENSION = "clientExt";
        String PARAM_TYPE = "type";
        String PARAM_ATTACHMENT = "attachment";
    }

    interface Method {
        String USER_AUTHORIZATION = "user.authorization.token";
        String MESSAGE_TEXT = "message.text";
    }
}

// {
//     "method": "",
//     "content": {
//         "toUSer": {},
//         "fromUser": {},
//         "content": ""
//     }
// }