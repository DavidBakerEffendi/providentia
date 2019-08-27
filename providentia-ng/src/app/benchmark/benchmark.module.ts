import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';

import { BenchmarkComponent } from './benchmark.component';
import { BENCHMARK_ROUTE } from './benchmark.route';
import { AngularMaterialModule } from '../material.module';
import { KateResultComponent, QueryResultComponent, ReviewTrendsComponent } from './result-tab';

@NgModule({
  imports: [
    RouterModule.forChild([BENCHMARK_ROUTE]),
    AngularMaterialModule,
    BrowserModule,
  ],
  declarations: [
    BenchmarkComponent,
    KateResultComponent,
    QueryResultComponent,
    ReviewTrendsComponent,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProvidentiaBenchmarkModule { }
