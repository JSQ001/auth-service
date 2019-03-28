package com.hand.hcf.app.common.dto;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hand.hcf.core.web.filter.CustomDateTimeSerializer;

import java.time.ZonedDateTime;

/**
 * Created by liuxinyu on 2017/5/7.
 */
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"code","msg","time"})
public class View<T> {
	@JsonProperty("code")
	private String returnCode;
	@JsonProperty("msg")
	private String returnMsg;
	@JsonProperty("time")
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private ZonedDateTime timestamp;

	@JsonIgnore
	private String originalCode;
	@JsonIgnore
	private String originalMsg;

	@JsonUnwrapped
	private T body;

	public View body(T body) {
		setBody(body);
		return this;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String resultCode) {
		this.returnCode = resultCode;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String resultMsg) {
		this.returnMsg = resultMsg;
	}

	public T getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getOriginalCode() {
		return originalCode;
	}

	public void setOriginalCode(String originalCode) {
		this.originalCode = originalCode;
	}

	public String getOriginalMsg() {
		return originalMsg;
	}

	public void setOriginalMsg(String originalMsg) {
		this.originalMsg = originalMsg;
	}

	@Override
	public String toString() {
		return "View [returnCode=" + returnCode + ", returnMsg=" + returnMsg + ", timestamp=" + timestamp + ", originalCode="
				+ originalCode + ", originalMsg=" + originalMsg + ", body=" + body + "]";
	}

}
