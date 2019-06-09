import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';

import { BenchmarkComponent } from './benchmark.component';
import { BENCHMARK_ROUTE } from './benchmark.route';
import { AngularMaterialModule } from '../material.module';

@NgModule({
  imports: [RouterModule.forChild([BENCHMARK_ROUTE]), AngularMaterialModule, BrowserModule],
  declarations: [BenchmarkComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProvidentiaBenchmarkModule { }
