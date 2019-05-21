import { Component, OnInit } from '@angular/core';

import { HttpClient } from '@angular/common/http';

@Component({
    selector: 'prv-new-job',
    templateUrl: './new-job.component.html',
    styleUrls: ['new-job.scss']
})
export class NewJobComponent implements OnInit {

    constructor(
        private http: HttpClient
    ) { }

    ngOnInit() {
    }
}
