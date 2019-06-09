
export class InfoMessage {

    showError = false;
    showSuccess = false;
    showWarn = false;
    showInfo = false;

    errorMsg: string;
    successMsg: string;
    warnMsg: string;
    infoMsg: string;

    showErrorMsg(message: string) {
        this.showError = true;
        this.showSuccess = false;
        this.showWarn = false;
        this.showInfo = false;

        this.errorMsg = message;
    }

    showSuccessMsg(message: string) {
        this.showError = false;
        this.showSuccess = true;
        this.showWarn = false;
        this.showInfo = false;

        this.successMsg = message;
    }

    showWarnMsg(message: string) {
        this.showError = false;
        this.showSuccess = false;
        this.showWarn = true;
        this.showInfo = false;

        this.warnMsg = message;
    }

    showInfoMsg(message: string) {
        this.showError = false;
        this.showSuccess = false;
        this.showWarn = false;
        this.showInfo = true;

        this.infoMsg = message;
    }

}