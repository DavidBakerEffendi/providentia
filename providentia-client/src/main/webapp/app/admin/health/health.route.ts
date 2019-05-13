import { Route } from '@angular/router';

import { PrvHealthCheckComponent } from './health.component';

export const healthRoute: Route = {
  path: 'prv-health',
  component: PrvHealthCheckComponent,
  data: {
    pageTitle: 'Health Checks'
  }
};
