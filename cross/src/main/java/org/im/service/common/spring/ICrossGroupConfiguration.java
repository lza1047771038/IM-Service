package org.im.service.common.spring;

import org.im.service.common.meta.GroupConfiguration;
import org.im.service.common.meta.PagedResult;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * 读取群聊配置，服务启动时需要从数据库读取已经创建的群聊配置，所以在IM服务初始化的时候，请确保数据库服务已经初始化完成
 * </p>
 *
 * @author liuzhongao
 * @version 2022/12/7 16:34
 */
public interface ICrossGroupConfiguration {

    /**
     * 分页获取群聊配置信息
     *
     * @param pageSize 获取的每一页群聊配置的数量，对应{@link PagedResult#getPageSize()}的大小
     * @param offset 获取数据的偏移量，该数值由pageSize * pageCount得到，pageCount由调用端维护
     * @return 分页的返回值，根据 {@link PagedResult#isHasMore()} 来判断是否到达结束点
     */
    @NotNull
    PagedResult<GroupConfiguration> fetchGroupConfiguration(final int pageSize, final int offset);
}
