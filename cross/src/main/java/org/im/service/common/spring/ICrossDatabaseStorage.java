package org.im.service.common.spring;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

/**
 * 使用该接口操作数据库
 *
 * @author liuzhongao
 * @version 2022/12/6 17:03
 */
public interface ICrossDatabaseStorage {

    /**
     * IM服务收到所有消息，都会调用该方法，可选择入库
     *
     * @param jsonObject 消息体json
     */
    void onReceiveMessage(@Nullable final JSONObject jsonObject);
}
