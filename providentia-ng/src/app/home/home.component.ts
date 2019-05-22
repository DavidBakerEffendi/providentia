import { Component, OnInit } from '@angular/core';
import APP_CONFIG from '../app.config';
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
        this.http.get(APP_CONFIG.FLASK_API + 'home')
            .subscribe(res => console.debug(res['message']),
            err => console.debug(err));
    }
}
