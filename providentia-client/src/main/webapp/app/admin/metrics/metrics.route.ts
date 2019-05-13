import { Route } from '@angular/router';

import { PrvMetricsMonitoringComponent } from './metrics.component';

export const metricsRoute: Route = {
  path: 'prv-metrics',
  component: PrvMetricsMonitoringComponent,
  data: {
    pageTitle: 'Application Metrics'
  }
};
