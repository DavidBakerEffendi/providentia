import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ProvidentiaSharedModule } from 'app/shared';
/* jhipster-needle-add-admin-module-import - JHipster will add admin modules imports here */

import {
  adminState,
  AuditsComponent,
  LogsComponent,
  PrvMetricsMonitoringComponent,
  PrvHealthModalComponent,
  PrvHealthCheckComponent,
  PrvConfigurationComponent,
  PrvDocsComponent
} from './';

@NgModule({
  imports: [
    ProvidentiaSharedModule,
    /* jhipster-needle-add-admin-module - JHipster will add admin modules here */
    RouterModule.forChild(adminState)
  ],
  declarations: [
    AuditsComponent,
    LogsComponent,
    PrvConfigurationComponent,
    PrvHealthCheckComponent,
    PrvHealthModalComponent,
    PrvDocsComponent,
    PrvMetricsMonitoringComponent
  ],
  entryComponents: [PrvHealthModalComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ProvidentiaAdminModule {}
