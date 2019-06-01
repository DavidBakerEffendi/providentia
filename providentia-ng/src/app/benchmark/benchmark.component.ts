import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { DomSanitizer } from '@angular/platform-browser';
import { IBenchmark, BenchmarkService } from '../shared';
import { ActivatedRoute, Router } from "@angular/router";
import { MAT_STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';

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
                console.debug(this.benchmark);
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

}
