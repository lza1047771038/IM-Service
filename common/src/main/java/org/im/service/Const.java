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
        String PARAM_SESSION_TYPE = "sessionType";
    }

    interface Method {
        String USER_AUTHORIZATION = "user.authorization.token";
        String MESSAGE_TEXT = "message.text";
    }

    interface Code {
        int CONNECTION_ESTABLISHED = 1;
        int SESSION_AUTHORIZATION_SUCCESS = 2;
        int SESSION_DISCONNECTED = 3;
        int MESSAGE_RECEIVED = 4;
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