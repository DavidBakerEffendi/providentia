import { Component, OnInit } from '@angular/core';
import { ClassifierService, InfoMessage } from '../shared';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'prv-classifier',
    templateUrl: './classifier.component.html',
    styleUrls: ['classifier.scss']
})
export class ClassifierComponent extends InfoMessage implements OnInit {

    sentimentReady = false;
    sentimentClassifierResult: string;

    constructor(
        private classifierService: ClassifierService
    ) {
        super();
    }

    ngOnInit() {
        this.testSentimentClassifier();
    }

    /**
     * Checks the status of the sentiment classifier.
     */
    testSentimentClassifier() {
        this.classifierService.testReady().subscribe((res: HttpResponse<any>) => {
            this.sentimentReady = true;
            this.showError = false;
        }, (res: HttpErrorResponse) => {
            if (res.status === 0) {
                this.showErrorMsg('Server did not reply to request. The server is most likely down or encountered an exception.');
            }
            this.sentimentReady = false;
        })
    }

    /**
     * Sends the given text to be classified by the sentiment classifier on the server.
     */
    classifySentiment(text: string) {
        this.classifierService.submitText(text)
            .subscribe((res: HttpResponse<any>) => {
                this.showError = false;
                this.sentimentClassifierResult = res.body.result;
                if (this.sentimentClassifierResult == 'pos') {
                    this.sentimentClassifierResult = 'Positive';
                } else {
                    this.sentimentClassifierResult = 'Negative';
                }
            },
                (res: HttpErrorResponse) => {
                    if (res.status === 0) {
                        this.showErrorMsg('Server did not reply to request. The server is most likely down or encountered an exception.');
                    } else if (res.status == 500) {
                        this.showErrorMsg(res.error.error);
                    } else {
                        this.showErrorMsg(res.statusText);
                    }
                });
    }
}
