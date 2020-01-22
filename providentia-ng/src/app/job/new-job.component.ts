import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { IDatabase, DatabaseService, IDataset, IAnalysis, AnalysisService, NewJobService } from '../shared';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { InfoMessage } from '../shared';

@Component({
    selector: 'prv-new-job',
    templateUrl: './new-job.component.html',
    styleUrls: ['new-job.scss']
})
export class NewJobComponent extends InfoMessage implements OnInit {

    public databases: IDatabase[];
    public analysis: IAnalysis[];
    public numJobs: Array<number> = [1, 5, 10, 15, 20, 30];

    public dataOptions: FormGroup;
    public descriptionOptions: FormGroup;

    constructor(
        private databaseService: DatabaseService,
        private analysisService: AnalysisService,
        private newJobService: NewJobService,
        private fb: FormBuilder
    ) {
        super();
        this.dataOptions = this.fb.group({
            hideRequired: false,
            floatLabel: 'auto',
            dbCtrl: new FormControl('', Validators.required),
            anCtrl: new FormControl('', Validators.required),
            nmCtrl: new FormControl('', Validators.required)
        });
    }

    ngOnInit() {
        this.databaseService.query().subscribe((res: HttpResponse<IDatabase[]>) => {
            this.databases = res.body.filter((db: IDatabase) => db.status === 'UP');
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
        const analysis = this.analysis.find((a: IAnalysis) => a.name === this.dataOptions.value.anCtrl);
        let newJob = {
            database: this.dataOptions.value.dbCtrl,
            dataset: analysis.dataset.name,
            analysis: this.dataOptions.value.anCtrl,
            status: 'WAITING'
        }
        this.newJobService.create(newJob, this.dataOptions.value.nmCtrl)
            .subscribe(() => {
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
