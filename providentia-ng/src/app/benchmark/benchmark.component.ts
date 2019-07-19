import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { DomSanitizer } from '@angular/platform-browser';
import { IBenchmark, BenchmarkService, IServerLog, LogService } from '../shared';
import { ActivatedRoute, Router } from "@angular/router";
import { MAT_STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { Observable } from 'rxjs';

@Component({
    selector: 'prv-benchmark',
    templateUrl: './benchmark.component.html',
    styleUrls: ['benchmark.scss'],
    providers: [{
        provide: MAT_STEPPER_GLOBAL_OPTIONS, useValue: { displayDefaultIndicatorType: false }
    }]
})
export class BenchmarkComponent implements OnInit {

    benchmark: IBenchmark;
    performance: IServerLog[];
    id: string;

    public chartColors: Array<any> = [{
        backgroundColor: ['#FF4081', '#455A64'],
        hoverBackgroundColor: ['#FF80AB', '#90A4AE'],
        borderWidth: 1,
    }];

    constructor(
        private route: Router,
        private router: ActivatedRoute,
        private benchmarkService: BenchmarkService,
        private logService: LogService,
        private _sanitizer: DomSanitizer
    ) {
        this.router.params.subscribe(params => this.id = params['id']);
    }

    ngOnInit() {
        this.getResult(this.id);
    }

    getResult(id: string) {
        this.benchmarkService.find(id)
            .subscribe((res: HttpResponse<IBenchmark>) => {
                this.benchmark = res.body;
                // Get the performance of the server during the time executed
                this.getPerformance(new Date(this.benchmark.date_executed),
                    this.benchmark.query_time.valueOf() + this.benchmark.analysis_time.valueOf());
                // Authorize the BASE64 encoding of the icons
                this._sanitizer.bypassSecurityTrustResourceUrl('data:image/png;base64,' + this.benchmark.database.icon);
                this._sanitizer.bypassSecurityTrustResourceUrl('data:image/png;base64,' + this.benchmark.dataset.icon);
            },
                (res: HttpErrorResponse) => {
                    console.error(res.message)
                    if (res.status == 404) {
                        this.route.navigate(['/404']);
                    }
                }
            );
    }

    getPerformance(fromDate: Date, duration: number) {
        const toDate = new Date(fromDate.getTime() + duration);
        this.logService.getFromTo(fromDate, toDate).subscribe((res: HttpResponse<IServerLog[]>) => {
        // this.logService.getRecent().subscribe((res: HttpResponse<IServerLog[]>) => {
            this.performance = res.body;
        },
            (res: HttpErrorResponse) => {
                console.error(res.message)
            });
    }

}
