import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';
import { PerformanceComponent } from './performance.component';
import { PERFORMANCE_ROUTE } from './performance.route';
import { AngularMaterialModule } from '../material.module';

@NgModule({
  imports: [
    RouterModule.forChild([PERFORMANCE_ROUTE]),
    BrowserModule,
    AngularMaterialModule
  ],
  declarations: [PerformanceComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProvidentiaPerformanceModule { }
