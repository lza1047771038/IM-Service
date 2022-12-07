package org.im.service.common.spring;

import kotlin.jvm.functions.Function1;
import org.json.JSONObject;
import org.im.service.utils.JSONKt;

/**
 * IM服务在发送消息前最后做的修改，请勿异步执行，当前方法同步结束后，会直接调用通信服务转发消息
 *
 * @author liuzhongao
 * @version 2022/12/7 14:43
 */
public interface ICrossUserMessageModifier {

    /**
     * 可在该方法里修改消息里的内容
     * 可参考该方法 {@link JSONKt#updateRemoteExtensionsThroughMap(JSONObject, Function1) }
     * 或者 {@link JSONKt#updateRemoteExtensionsThroughJSONObject(JSONObject, Function1) }
     *
     * @param jsonObject 消息体
     */
    void appendServerExtensions(final JSONObject jsonObject);
}
