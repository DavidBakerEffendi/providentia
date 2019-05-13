import { Route } from '@angular/router';

import { PrvConfigurationComponent } from './configuration.component';

export const configurationRoute: Route = {
  path: 'prv-configuration',
  component: PrvConfigurationComponent,
  data: {
    pageTitle: 'Configuration'
  }
};
