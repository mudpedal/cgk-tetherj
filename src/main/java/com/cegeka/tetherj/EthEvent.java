package com.cegeka.tetherj;

import com.cegeka.tetherj.pojo.FilterLogObject;
import lombok.Data;

/**
 * Created by andreicg on 6/16/16.
 */
@Data
public class EthEvent {
    private Object[] data;
    private FilterLogObject filterLogObject;
}
