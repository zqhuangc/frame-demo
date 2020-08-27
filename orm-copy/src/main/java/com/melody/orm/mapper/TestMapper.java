package com.melody.orm.mapper;


import com.melody.orm.domain.entity.Test;

/**
 * @author zqhuangc
 */
public interface TestMapper {
    Test selectByPrimaryKey(Integer testId);
}
