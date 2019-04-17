

package com.hand.hcf.app.core.exception.core;

public class IdNotNullInCreateActionException extends ValidationException {

    public IdNotNullInCreateActionException() {
        super(new ValidationError("id", "should.be.null"));
    }
}
