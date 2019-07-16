import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';
import { ClassifierComponent } from './classifier.component';
import { CLASSIFIER_ROUTE } from './classifier.route';
import { AngularMaterialModule } from '../material.module';

@NgModule({
  imports: [
    RouterModule.forChild([CLASSIFIER_ROUTE]),
    BrowserModule,
    AngularMaterialModule
  ],
  declarations: [ClassifierComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProvidentiaClassifierModule { }
