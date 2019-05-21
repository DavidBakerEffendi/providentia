import { Component, OnInit } from '@angular/core';

import { HttpClient } from '@angular/common/http';

@Component({
    selector: 'prv-about',
    templateUrl: './about.component.html',
    styleUrls: ['about.scss']
})
export class AboutComponent implements OnInit {

    constructor(
        private http: HttpClient
    ) { }

    ngOnInit() {
    }
}
