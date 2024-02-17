interface ValidationErrors {
    [key: string]: string;
}

export class ApiError {
    status: number | string;
    message: string;
    debugMessage: string;
    validationErrors: ValidationErrors;

    constructor(status: number | string, message: string, debugMessage: string, validationErrors: ValidationErrors = {}) {
        this.status = status;
        this.message = message;
        this.debugMessage = debugMessage;
        this.validationErrors = validationErrors;
    }
}
