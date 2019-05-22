import { Component, OnInit } from '@angular/core';

import { HttpClient } from '@angular/common/http';

@Component({
    selector: 'prv-history',
    templateUrl: './history.component.html',
    styleUrls: ['history.scss']
})
export class HistoryComponent implements OnInit {

    constructor(
        private http: HttpClient
    ) { }

    ngOnInit() {
    }
}
