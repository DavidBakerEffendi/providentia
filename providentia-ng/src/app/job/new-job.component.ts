import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { IDatabase, DatabaseService, IDataset, DatasetService, IAnalysis, AnalysisService, IBenchmark, NewJobService } from '../shared';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { InfoMessage } from '../shared';

@Component({
    selector: 'prv-new-job',
    templateUrl: './new-job.component.html',
    styleUrls: ['new-job.scss']
})
export class NewJobComponent extends InfoMessage implements OnInit {

    databases: IDatabase[];
    datasets: IDataset[];
    analysis: IAnalysis[];

    dataOptions: FormGroup;
    descriptionOptions: FormGroup;

    constructor(
        private datasetService: DatasetService,
        private databaseService: DatabaseService,
        private analysisService: AnalysisService,
        private newJobService: NewJobService,
        private fb: FormBuilder
    ) {
        super();
        this.dataOptions = fb.group({
            hideRequired: false,
            floatLabel: 'auto',
            dbCtrl: new FormControl('', Validators.required),
            dsCtrl: new FormControl('', Validators.required),
            anCtrl: new FormControl('', Validators.required)
        });
    }

    ngOnInit() {
        this.databaseService.query().subscribe((res: HttpResponse<IDatabase[]>) => {
            this.databases = res.body;
            this.showError = false;
        },
            (res: HttpErrorResponse) => {
                console.error(res.statusText);
                if (res.status === 0) {
                    this.showErrorMsg('Server did not reply to request. The server is most likely down or encountered an exception.');
                } else if (res.status == 500) {
                    this.showErrorMsg(res.error.error);
                } else {
                    this.showErrorMsg(res.statusText);
                }
            });
        this.datasetService.query().subscribe((res: HttpResponse<IDataset[]>) => {
            this.datasets = res.body;
            this.showError = false;
        },
            (res: HttpErrorResponse) => {
                console.error(res.statusText);
                if (res.status === 0) {
                    this.showErrorMsg('Server did not reply to request. The server is most likely down or encountered an exception.');
                } else if (res.status == 500) {
                    this.showErrorMsg(res.error.error);
                } else {
                    this.showErrorMsg(res.statusText);
                }
            });
        this.analysisService.query().subscribe((res: HttpResponse<IDataset[]>) => {
            this.analysis = res.body;
            this.showError = false;
        },
            (res: HttpErrorResponse) => {
                console.error(res.statusText);
                if (res.status === 0) {
                    this.showErrorMsg('Server did not reply to request. The server is most likely down or encountered an exception.');
                } else if (res.status == 500) {
                    this.showErrorMsg(res.error.error);
                } else {
                    this.showErrorMsg(res.statusText);
                }
            });
    }

    postNewJob() {
        let newJob: IBenchmark = {
            database: this.dataOptions.value.dbCtrl,
            dataset: this.dataOptions.value.dsCtrl,
            analysis: this.dataOptions.value.anCtrl,
            status: 'WAITING'
        }
        console.debug(newJob);
        this.newJobService.create(newJob)
            .subscribe((res: HttpResponse<IBenchmark>) => {
                this.showSuccessMsg('New job successfully added to the pipeline!');
            }, (res: HttpErrorResponse) => {
                console.error(res.message)
                if (res.status === 0) {
                    this.showErrorMsg('Server did not reply to request. The server is most likely down or encountered an exception.');
                } else if (res.status === 500) {
                    this.showErrorMsg(res.error.error);
                } else if (res.status === 400) {
                    this.showWarnMsg(res.error.error);
                    // Point user to field to fix
                    this.descriptionOptions.reset();
                } else if (res.status === 404) {
                    this.showWarnMsg(res.error.error);
                    // Point user to field to fix
                    this.dataOptions.reset();
                } else {
                    this.showErrorMsg(res.statusText);
                }
            });
    }
}
