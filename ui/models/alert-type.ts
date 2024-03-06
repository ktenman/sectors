/* eslint-disable no-unused-vars */
export enum AlertType {
    ERROR = 'error',
    SUCCESS = 'success',
}

export function getAlertBootstrapClass(type: AlertType | null): string {
    if (!type) {
        return 'alert-info'
    }

    const mapping: Record<AlertType, string> = {
        [AlertType.ERROR]: 'alert-danger',
        [AlertType.SUCCESS]: 'alert-success',
    }

    return mapping[type] || 'alert-info' // Default to 'alert-info' if no match
}
/* eslint-enable no-unused-vars */
