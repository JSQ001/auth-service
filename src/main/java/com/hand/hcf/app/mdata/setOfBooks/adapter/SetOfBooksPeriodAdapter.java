package com.hand.hcf.app.mdata.setOfBooks.adapter;

import com.hand.hcf.app.mdata.period.domain.PeriodSet;
import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.dto.SetOfBooksPeriodDTO;

/**
 * Created by silence on 2017/11/7.
 */
public class SetOfBooksPeriodAdapter {
    public static SetOfBooksPeriodDTO toDTO(SetOfBooks setOfBooks, PeriodSet periodSet){
        SetOfBooksPeriodDTO dto = new SetOfBooksPeriodDTO();
        dto.setSetOfBooksId(setOfBooks.getId());
        dto.setSetOfBooksCode(setOfBooks.getSetOfBooksCode());
        dto.setSetOfBooksName(setOfBooks.getSetOfBooksName());
        dto.setPeriodSetId(periodSet.getId());
        dto.setPeriodSetCode(periodSet.getPeriodSetCode());
        dto.setPeriodSetName(periodSet.getPeriodSetName());
        dto.setTotalPeriodNum(periodSet.getTotalPeriodNum());
        return dto;
    }
}
