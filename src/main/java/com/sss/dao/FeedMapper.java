package com.sss.dao;

import com.sss.model.Feed;
import java.util.List;

public interface FeedMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Feed record);

    Feed selectByPrimaryKey(Integer id);

    List<Feed> selectAll();

    int updateByPrimaryKey(Feed record);
}