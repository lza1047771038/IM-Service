package org.im.service.common.spring;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.im.service.utils.JSONKt;

/**
 * @author liuzhongao
 * @version 2022/12/7 09:53
 *
 * @apiNote  用户权限校验接口，由Spring端提供实现
 * 这里特指IM服务调用spring 服务，主要针对的是当前用户和对方用户
 *
 * 具体如何从JSONObject里获取数据，可参考{@link JSONKt}
 */
public interface ICrossUserMessageAuthorization {

    /**
     * <p> 检查用户是否被封禁 </p>
     * <p> 需要检查双方，被封禁的用户不允许发送和接收消息 </p>
     * <p> 群聊也需要注意，如果接收方是群聊，也需要检查群聊的状态，是否被封禁 </p>
     *
     * @param jsonObject 当前消息体的json
     * @return 用户如果没有被系统封禁，返回false, 否则返回true
     */
    boolean checkUserIsBlocked(@NotNull final JSONObject jsonObject);

    /**
     * 检查用户是否被发送者拉黑
     *
     * @param jsonObject 当前消息体的json
     * @return 用户被接收方拉黑，返回 true，否则返回 false
     */
    boolean checkUserMuteState(@NotNull final JSONObject jsonObject);

    /**
     * 一些其他的检查，暂时还没想到，默认返回true得了
     *
     * @param jsonObject 当前消息体的json
     * @return 可以发送返回 true，否则返回 false
     */
    boolean canSendToTarget(@NotNull final JSONObject jsonObject);

}
