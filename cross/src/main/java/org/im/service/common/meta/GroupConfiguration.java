package org.im.service.common.meta;

import java.io.Serializable;
import java.util.List;

/**
 * @author liuzhongao
 * @version 2022/12/7 16:58
 */
public class GroupConfiguration implements Serializable {
    /**
     * 群会话id，唯一id
     */
    private String mSessionId;
    /**
     * 群昵称，可重复
     */
    private String mName;
    /**
     * 群主id
     */
    private String mCreatorSessionId;
    /**
     * 管理员
     */
    private List<String> mManagers;
    /**
     * 当前群聊的所有成员，包括管理员和群主
     */
    private List<String> mMembers;
}
