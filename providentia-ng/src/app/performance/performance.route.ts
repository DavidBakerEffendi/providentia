import { Route } from '@angular/router';

import { PerformanceComponent } from './performance.component';

export const PERFORMANCE_ROUTE: Route = {
  path: 'performance',
  component: PerformanceComponent,
  data: {
    authorities: [],
    pageTitle: 'Performance | Providentia'
  }
};
