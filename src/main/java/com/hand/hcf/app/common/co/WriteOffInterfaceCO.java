package com.hand.hcf.app.common.co;

import com.hand.hcf.app.common.annotation.InterfaceDataStructure;
import com.hand.hcf.app.common.annotation.InterfaceTransactionType;
import com.hand.hcf.app.common.enums.SourceTransactionType;
import com.hand.hcf.app.common.enums.SourceTransactionTypeDataStructure;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/4/9 17:41
 * @remark
 */
@Data
@InterfaceTransactionType(SourceTransactionType.CSH_WRITE_OFF)
public class WriteOffInterfaceCO extends AccountingBaseCO implements Serializable {

    @Valid
    @InterfaceDataStructure(sequence = 1, type = SourceTransactionTypeDataStructure.WRITE_OFF_DETAIL)
    private List<WriteOffDetailCO> writeOffDetail;
}
