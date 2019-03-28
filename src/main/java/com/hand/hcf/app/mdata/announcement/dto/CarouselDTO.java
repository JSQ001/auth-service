package com.hand.hcf.app.mdata.announcement.dto;

import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.mdata.announcement.domain.Carousel;
import lombok.Data;

@Data
public class CarouselDTO extends Carousel {
    private AttachmentCO attachment;
}
