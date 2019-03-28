package com.hand.hcf.app.mdata.utils;

import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.util.Locale;

/**
 * Created by liuxinyu on 2017/5/7.
 */
@Component
@Slf4j
public class I18NUtil {

	public static final String DEFAULT_MESSAGE = "Unknown Code";
	protected static I18NUtil instance;

	@Autowired
    MessageService messageService;

	public I18NUtil() {
		instance = this;
	}

	public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		String s;
		s = instance.messageService.getMessageDetailByCode(code, args);
		if (s == null) {
			log.warn("cannot find message for code [{}], use default message.", code);
			s = DEFAULT_MESSAGE;
		}
		return s;
	}

	public static String getMessage(String code, Object[] args, String defaultMessage) {
		return getMessage(code, args, defaultMessage, null);
	}

	//哈希函数
	public static String getSha1(String str) {
		if (null == str || 0 == str.length()) {
			return null;
		}
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
			mdTemp.update(str.getBytes("ISO-8859-1"));

			byte[] md = mdTemp.digest();
			int j = md.length;
			char[] buf = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
				buf[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(buf);
		} catch (Exception e) {
			throw new BizException(RespCode.SYS_ERROR, new Object[]{"getSha1 error "}, e);
		}
	}
}
