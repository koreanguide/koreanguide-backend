package com.koreanguide.koreanguidebackend.domain.credit.exception;

public class BankAccountsNotFoundException extends RuntimeException {
    public BankAccountsNotFoundException() {
        super();
    }
    public BankAccountsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public BankAccountsNotFoundException(String message) {
        super(message);
    }
    public BankAccountsNotFoundException(Throwable cause) {
        super(cause);
    }
}
