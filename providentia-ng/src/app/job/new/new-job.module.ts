import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { NewJobComponent } from './new-job.component';
import { NEW_JOB_ROUTE } from './new-job.route';
import { AngularMaterialModule } from '../../material.module';

@NgModule({
  imports: [RouterModule.forChild([NEW_JOB_ROUTE]), AngularMaterialModule],
  declarations: [NewJobComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProvidentiaNewJobModule { }
