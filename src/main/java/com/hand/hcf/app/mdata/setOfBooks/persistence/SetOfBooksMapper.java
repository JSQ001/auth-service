package com.hand.hcf.app.mdata.setOfBooks.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;

import java.util.List;

/**
 * Created by silence on 2017/9/5.
 */
public interface SetOfBooksMapper extends BaseMapper<SetOfBooks> {

    List<SetOfBooks> getAllSetOfBook(Pagination page);
}
