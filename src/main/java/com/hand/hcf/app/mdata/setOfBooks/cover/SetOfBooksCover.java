package com.hand.hcf.app.mdata.setOfBooks.cover;

import com.hand.hcf.app.mdata.setOfBooks.domain.SetOfBooks;
import com.hand.hcf.app.mdata.setOfBooks.dto.SetOfBooksDTO;

/**
 * Created by fanfuqiang 2018/11/20
 */
public class SetOfBooksCover {
    public static SetOfBooksDTO toDTO(SetOfBooks setOfBooks) {
        SetOfBooksDTO setOfBooksDTO = new SetOfBooksDTO();
        setOfBooksDTO.setSetOfBooksId(setOfBooks.getId());
        setOfBooksDTO.setSetOfBooksCode(setOfBooks.getSetOfBooksCode());
        setOfBooksDTO.setSetOfBooksName(setOfBooks.getSetOfBooksName());
        setOfBooksDTO.setPeriodSetCode(setOfBooks.getPeriodSetCode());
        setOfBooksDTO.setAccountSetId(setOfBooks.getAccountSetId());
        setOfBooksDTO.setFunctionalCurrencyCode(setOfBooks.getFunctionalCurrencyCode());
        setOfBooksDTO.setEnabled(setOfBooks.getEnabled());
        return setOfBooksDTO;
    }
}
