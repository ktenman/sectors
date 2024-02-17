interface ValidationErrors {
    [key: string]: string;
}

export class ApiError {
    constructor(
        public status: number | string,
        public message: string,
        public debugMessage: string,
        public validationErrors: ValidationErrors = {}
    ) {
    }
}
