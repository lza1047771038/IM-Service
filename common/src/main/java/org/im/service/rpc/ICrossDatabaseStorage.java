package org.im.service.rpc;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

/**
 * @author: liuzhongao
 * @date: 2022/12/6 17:03
 */
public interface ICrossDatabaseStorage {
    void onReceiveMessage(@Nullable final JSONObject jsonObject);
}
