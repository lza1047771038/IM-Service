package org.im.service.rpc;

import org.im.service.client.metadata.SessionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author: liuzhongao
 * @date: 2022/12/6 17:04
 *
 * Spring端获取的操作对象，通过接口调用，由服务端主动向某个用户发送消息，如公众号、群小助手等...
 */
public interface ICrossMessageOperator {

    /**
     * 给指定用户发送文字消息
     *
     * 注意：不用调用端主动入库，IM消息入库逻辑按照Spring端的回调提供
     *
     * 耗时操作
     *
     * @param sessionId 指定用户的会话id
     * @param sessionType 会话类型，P2P，Group分别表示点对点，群聊
     * @param content 文字消息内容
     * @param remoteExtension 服务端的扩展字段，可以传空值
     * @return true表示发送成功，false表示发送失败
     */
    boolean sendTextMessage(@NotNull String sessionId, @NotNull SessionType sessionType, @NotNull String content, @Nullable Map<String, Object> remoteExtension);

    /**
     * 发送图片消息，数据结构还没定义，暂时不用
     * @param sessionId 指定用户的会话id
     * @param sessionType 会话类型，P2P，Group分别表示点对点，群聊
     * @param remoteExtension 服务端的扩展字段，可以传空值
     * @return true表示发送成功，false表示发送失败
     */
    boolean sendImageMessage(@NotNull String sessionId, @NotNull SessionType sessionType, @Nullable Map<String, Object> remoteExtension);

}
