import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';
import { DatabasesComponent } from './databases.component';
import { DATABASES_ROUTE } from './databases.route';
import { AngularMaterialModule } from '../material.module';

@NgModule({
  imports: [
    RouterModule.forChild([DATABASES_ROUTE]),
    AngularMaterialModule,
    BrowserModule
  ],
  declarations: [DatabasesComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProvidentiaDatabasesModule { }
