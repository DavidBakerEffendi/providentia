import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';

import { HealthCheckComponent } from './health.component';
import { PrvHealthModalComponent } from './health-modal.component';
import { healthRoute } from './health.route';
import { AngularMaterialModule } from '../material.module';

@NgModule({
  imports: [
    RouterModule.forChild([healthRoute]),
    AngularMaterialModule,
    BrowserModule
  ],
  declarations: [HealthCheckComponent, PrvHealthModalComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProvidentiaHealthModule { }
