
export class InfoMessage {

    showError = false;
    showSuccess = false;
    showWarn = false;

    errorMsg: string;
    successMsg: string;
    warnMsg: string;

    showErrorMsg(message: string) {
        this.showError = true;
        this.showSuccess = false;
        this.showWarn = false;

        this.errorMsg = message;
    }

    showSuccessMsg(message: string) {
        this.showError = false;
        this.showSuccess = true;
        this.showWarn = false;

        this.successMsg = message;
    }

    showWarnMsg(message: string) {
        this.showError = false;
        this.showSuccess = false;
        this.showWarn = true;

        this.warnMsg = message;
    }

}