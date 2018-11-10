

package com.hand.hcf.app.base.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ArtemisAuthorizationCode implements Serializable {

	private static final long serialVersionUID = 914967629530460646L;

	private String value;

	private Date expiration;

	public ArtemisAuthorizationCode(String value, Date expiration) {
		this.value = value;
		setExpiration(expiration);
	}

	public int getExpiresIn() {
		return expiration != null ? Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L)
				.intValue() : 0;
	}

	protected void setExpiresIn(int delta) {
		setExpiration(new Date(System.currentTimeMillis() + delta));
	}

	/**
	 * Convenience method for checking expiration
	 *
	 * @return true if the expiration is befor ethe current time
	 */
	public boolean isExpired() {
		return expiration != null && expiration.before(new Date());
	}


	@Override
	public boolean equals(Object obj) {
		return obj != null && toString().equals(obj.toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return String.valueOf(getValue());
	}

}
