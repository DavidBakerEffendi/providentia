import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { IDatabase, DatabaseService, IDataset, DatasetService } from '../shared';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
    selector: 'prv-new-job',
    templateUrl: './new-job.component.html',
    styleUrls: ['new-job.scss']
})
export class NewJobComponent implements OnInit {

    databases: IDatabase[];
    datasets: IDataset[];

    dataOptions: FormGroup;
    analysisOptions: FormGroup;

    constructor(
        private datasetService: DatasetService,
        private databaseService: DatabaseService,
        private fb: FormBuilder
    ) {
        this.dataOptions = fb.group({
            hideRequired: false,
            floatLabel: 'auto',
            firstCtrl: ['', Validators.required],
        });
        this.analysisOptions = fb.group({
            hideRequired: false,
            floatLabel: 'auto',
            secondCtrl: ['', Validators.required]
        });
    }

    ngOnInit() {
        this.databaseService.query().subscribe((res: HttpResponse<IDatabase[]>) => {
            this.databases = res.body;
        },
        (res: HttpErrorResponse) => console.error(res.message));
        this.datasetService.query().subscribe((res: HttpResponse<IDataset[]>) => {
            this.datasets = res.body;
        },
        (res: HttpErrorResponse) => console.error(res.message));
    }
}
