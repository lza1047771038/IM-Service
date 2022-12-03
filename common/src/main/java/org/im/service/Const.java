package org.im.service;

public interface Const {
    interface Param {
        String PARAM_USER_SESSION_ID = "sid"; // string

        String PARAM_CONTENT = "c"; // string

        String PARAM_METHOD = "m"; // string
        String PARAM_TO_USER = "tu"; // obj
        String PARAM_FROM_USER = "fu"; // obj
        String PARAM_TO_USER_ID = "tuid"; // string
        String PARAM_FROM_USER_ID = "fuid"; // string
        String PARAM_UUID = "uuid"; // string
        String PARAM_REMOTE_EXTENSION = "rext"; // map
        String PARAM_CLIENT_EXTENSION = "cext"; // map
        String PARAM_TYPE = "t"; // int
        String PARAM_ATTACHMENT = "atmt"; // String
        String PARAM_SESSION_TYPE = "st"; // int
    }

    interface Method {
        String USER_AUTHORIZATION = "uato";
        String MESSAGE_TEXT = "mstx";
        String MESSAGE_STATE_UPDATE = "msu";
    }

    interface Code {
        int CONNECTION_ESTABLISHED = 1;
        int SESSION_AUTHORIZATION_SUCCESS = 2;
        int SESSION_DISCONNECTED = 3;
        int MESSAGE_RECEIVED = 4;
        int MESSAGE_UPDATE = 5;
        int MESSAGE_SEND_ERROR = 6;
        int MESSAGE_SEND_SUCCESS = 7;
        int SESSION_AUTHORIZATION_FAILED = 8;
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