import { Component, OnInit } from '@angular/core';

import { HttpClient } from '@angular/common/http';

@Component({
    selector: 'prv-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit {

    constructor(
        private http: HttpClient
    ) { }

    ngOnInit() {
    }
}
