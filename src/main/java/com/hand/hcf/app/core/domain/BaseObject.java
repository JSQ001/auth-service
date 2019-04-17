

package com.hand.hcf.app.core.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
abstract public class BaseObject implements Serializable {
    @TableId
    protected Long id;
}
