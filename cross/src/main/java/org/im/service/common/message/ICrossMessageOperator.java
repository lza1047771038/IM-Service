package org.im.service.common.message;

import org.im.service.client.metadata.SessionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.im.service.utils.MessageKt;

import java.util.Map;

/**
 * @author liuzhongao
 * @version 2022/12/6 17:04
 *
 * Spring端获取的操作对象，通过接口调用，由服务端主动向某个用户发送消息，如公众号、群小助手等...
 */
public interface ICrossMessageOperator {

    /**
     * <p> 给指定用户、群聊发送文字消息 </p>
     * <p> 注意：不用调用端主动入库，IM消息入库逻辑按照Spring端的回调提供 </p>
     * <p> 耗时操作 </p>
     * <p> 如何构造对象，请参考{@link MessageKt#createTextMessage(String, SessionType)}</p>
     *
     * @param fromSessionId     由哪个用户发送
     * @param targetSessionId   指定用户的会话id
     * @param sessionType       会话类型，P2P，Group分别表示点对点，群聊
     * @param content           文字消息内容
     * @param remoteExtension   服务端的扩展字段，可以传空值
     * @return true表示发送成功，false表示发送失败
     */
    boolean sendTextMessage(@NotNull final String fromSessionId, @NotNull final String targetSessionId, @NotNull final SessionType sessionType, @NotNull final String content, @Nullable final Map<String, Object> remoteExtension);

    /**
     * 发送图片消息，数据结构还没定义，暂时不用
     *
     * @param fromSessionId     由哪个用户发送
     * @param targetSessionId   指定用户的会话id
     * @param sessionType       会话类型，P2P，Group分别表示点对点，群聊
     * @param remoteExtension   服务端的扩展字段，可以传空值
     * @return true表示发送成功，false表示发送失败
     */
    @Deprecated
    boolean sendImageMessage(@NotNull final String fromSessionId, @NotNull final String targetSessionId, @NotNull final SessionType sessionType, @Nullable final Map<String, Object> remoteExtension);

}
