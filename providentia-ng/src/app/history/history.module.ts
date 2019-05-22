import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { HistoryComponent } from './history.component';
import { HISTORY_ROUTE } from './history.route';
import { AngularMaterialModule } from '../material.module';

@NgModule({
  imports: [RouterModule.forChild([HISTORY_ROUTE]), AngularMaterialModule],
  declarations: [HistoryComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProvidentiaHistoryModule { }
