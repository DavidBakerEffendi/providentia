import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { IResult, ResultService } from '../shared';

@Component({
    selector: 'prv-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit {

    recentResults: IResult[];
    flaskUpdate: number;
    springUpdate: number;

    constructor(
        private resultService: ResultService
    ) { }

    ngOnInit() {
        this.getRecentResults();
        this.getFlaskMetrics();
        this.getSpringMetrics();
    }

    getRecentResults() {
        this.resultService.query()
            .subscribe((res: HttpResponse<IResult[]>) => {
                this.recentResults = res.body;
            },
            (res: HttpErrorResponse) => console.error(res.message));
    }

    getFlaskMetrics() {
        // TODO
        this.flaskUpdate = Date.now();
    }

    getSpringMetrics() {
        // TODO
        this.springUpdate = Date.now();
    }
}
