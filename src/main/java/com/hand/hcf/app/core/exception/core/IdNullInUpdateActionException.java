

package com.hand.hcf.app.core.exception.core;

public class IdNullInUpdateActionException extends ValidationException {
    public IdNullInUpdateActionException() {
        super(new ValidationError("id", "should.not.be.null"));
    }
}
