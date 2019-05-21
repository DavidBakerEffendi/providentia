import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AboutComponent } from './about.component';
import { ABOUT_ROUTE } from './about.route';
import { AngularMaterialModule } from '../material.module';

@NgModule({
  imports: [RouterModule.forChild([ABOUT_ROUTE]), AngularMaterialModule],
  declarations: [AboutComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProvidentiaAboutModule { }
