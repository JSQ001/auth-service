package com.hand.hcf.app.base.attachment.enums;


import com.hand.hcf.app.base.attachment.constant.Constants;
import com.hand.hcf.core.enums.SysEnum;

public enum MediaType implements SysEnum {
    IMAGE(1001), VIDEO(1002), PDF(1003), WORD(1004), OTHER(1005);

    private Integer id;

    MediaType(Integer id) {
        this.id = id;
    }

    public static MediaType parse(Integer id) {
        for (MediaType mediaType : MediaType.values()) {
            if (mediaType.getId().equals(id)) {
                return mediaType;
            }
        }
        return null;
    }

    public static MediaType getMediaTypeByFileName(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (Constants.IMAGES_EXTENSION.contains(ext)) {
            return MediaType.IMAGE;
        } else if (Constants.PDF_EXTENSION.equals(ext)) {
            return MediaType.PDF;
        } else if (Constants.VIDEO_EXTENSION.contains(ext)) {
            return MediaType.VIDEO;
        }else if(Constants.WORD_EXTENSION.contains(ext)){
            return MediaType.WORD;
        } else {
            return MediaType.OTHER;
        }
    }

    @Override
    public Integer getId() {
        return this.id;
    }
}
