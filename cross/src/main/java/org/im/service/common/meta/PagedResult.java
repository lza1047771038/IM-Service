package org.im.service.common.meta;

import java.io.Serializable;
import java.util.List;

/**
 * @author liuzhongao
 * @version 2022/12/7 17:03
 */
public class PagedResult<T extends Serializable> {
    // 分页参数，偏移量
    private final int offset;
    // 每一页有多少数据
    private final int pageSize;

    // 是否还有更多数据
    private boolean hasMore;
    // 具体分页的数据
    private List<T> data;

    public PagedResult(int offset, int pageSize, boolean hasMore) {
        this.offset = offset;
        this.pageSize = pageSize;
        this.hasMore = hasMore;
    }

    public int getOffset() {
        return offset;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
