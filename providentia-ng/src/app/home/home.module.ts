import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';

import { HomeComponent } from './home.component';
import { HOME_ROUTE } from './home.route';
import { AngularMaterialModule } from '../material.module';

@NgModule({
  imports: [
    RouterModule.forChild([HOME_ROUTE]),
    AngularMaterialModule,
    BrowserModule
  ],
  declarations: [HomeComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProvidentiaHomeModule { }
