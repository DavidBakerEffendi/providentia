import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';

import { NewJobComponent } from './new-job.component';
import { NEW_JOB_ROUTE } from './new-job.route';
import { AngularMaterialModule } from '../material.module';

@NgModule({
  imports: [
    RouterModule.forChild([NEW_JOB_ROUTE]),
    FormsModule,
    ReactiveFormsModule,
    BrowserModule,
    AngularMaterialModule,
  ],
  declarations: [NewJobComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProvidentiaNewJobModule { }
