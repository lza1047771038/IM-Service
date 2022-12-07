package org.im.service.common.message;

import org.jetbrains.annotations.NotNull;

/**
 *
 * 该接口用来创建和销毁群聊
 *
 * @author liuzhongao
 * @version 2022/12/7 17:16
 */
public interface ICrossGroupOperator {

    /**
     * 创建群聊
     *
     * @param sessionId 群聊的会话id
     * @return 创建成功返回 true, 否则返回 false
     */
    boolean createGroup(@NotNull final String sessionId);

    /**
     * 销毁群聊
     *
     * @param sessionId 群聊的会话id
     * @return 如果sessionId对应的群聊存在、且销毁成功，返回 true， 否则返回 false
     */
    boolean destroyGroup(@NotNull final String sessionId);

    /**
     * 加入群聊，调用该方法，请确保用户与群聊的关系已经入库
     *
     * @param groupSessionId 群聊会话id
     * @param userSessionId 用户的会话id
     * @return 用户已经加入返回true，加入失败返回false
     */
    boolean joinGroup(@NotNull final String groupSessionId, @NotNull final String userSessionId);

    /**
     * 退出群聊，调用该方法，请确保用户与群聊的关系已经入库
     *
     * @param groupSessionId 群聊会话id
     * @param userSessionId 用户的会话id
     * @return 用户已经不在该群聊，返回 true， 否则返回 false
     */
    boolean quitGroup(@NotNull final String groupSessionId, @NotNull final String userSessionId);
}
