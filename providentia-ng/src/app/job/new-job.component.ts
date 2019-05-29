import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { IDatabase, DatabaseService, IDataset, DatasetService, IAnalysis, AnalysisService, Benchmark, IBenchmark, NewJobService } from '../shared';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
    selector: 'prv-new-job',
    templateUrl: './new-job.component.html',
    styleUrls: ['new-job.scss']
})
export class NewJobComponent implements OnInit {

    databases: IDatabase[];
    datasets: IDataset[];
    analysis: IAnalysis[];

    dataOptions: FormGroup;
    analysisOptions: FormGroup;
    descriptionOptions: FormGroup;

    errorMsg: string;
    successMsg: string;

    constructor(
        private datasetService: DatasetService,
        private databaseService: DatabaseService,
        private analysisService: AnalysisService,
        private newJobService: NewJobService,
        private fb: FormBuilder
    ) {
        this.dataOptions = fb.group({
            hideRequired: false,
            floatLabel: 'auto',
            firstCtrl: ['', Validators.required],
            secondCtrl: ['', Validators.required]
        });
        this.analysisOptions = fb.group({
            hideRequired: false,
            floatLabel: 'auto',
            firstCtrl: ['', Validators.required]
        });
        this.descriptionOptions = fb.group({
            hideRequired: false,
            floatLabel: 'auto',
            firstCtrl: ['', Validators.required],
            secondCtrl: [''],
        });
    }

    ngOnInit() {
        this.databaseService.query().subscribe((res: HttpResponse<IDatabase[]>) => {
            this.databases = res.body;
        },
            (res: HttpErrorResponse) => {
                console.error(res.statusText);
                if (res.status === 0) {
                    this.errorMsg = 'Server did not reply to request. The server is most likely down or encountered an exception.';
                } else if (res.status == 500) {
                    this.errorMsg = res.error.error;
                } else {
                    this.errorMsg = res.statusText;
                }
            });
        this.datasetService.query().subscribe((res: HttpResponse<IDataset[]>) => {
            this.datasets = res.body;
        },
            (res: HttpErrorResponse) => {
                console.error(res.statusText);
                if (res.status === 0) {
                    this.errorMsg = 'Server did not reply to request. The server is most likely down or encountered an exception.';
                } else if (res.status == 500) {
                    this.errorMsg = res.error.error;
                } else {
                    this.errorMsg = res.statusText;
                }
            });
        this.analysisService.query().subscribe((res: HttpResponse<IDataset[]>) => {
            this.analysis = res.body;
        },
            (res: HttpErrorResponse) => {
                console.error(res.statusText);
                if (res.status === 0) {
                    this.errorMsg = 'Server did not reply to request. The server is most likely down or encountered an exception.';
                } else if (res.status == 500) {
                    this.errorMsg = res.error.error;
                } else {
                    this.errorMsg = res.statusText;
                }
            });
    }

    postNewJob() {
        let newJob: IBenchmark = {
            database: this.dataOptions.value.firstCtrl,
            dataset: this.dataOptions.value.secondCtrl,
            analysis: this.analysisOptions.value.firstCtrl,
            title: this.descriptionOptions.value.firstCtrl,
            description: this.descriptionOptions.value.secondCtrl,
        }
        console.debug(newJob);
        this.newJobService.create(newJob)
            .subscribe((res: HttpResponse<IBenchmark>) => {
                this.successMsg = '"' + newJob.title + '" successfully added to the pipeline!';
            }, (res: HttpErrorResponse) => {
                console.error(res.message)
                this.errorMsg = res.statusText;
            }
        );
    }
}
