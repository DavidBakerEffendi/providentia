import { Route } from '@angular/router';

import { NewJobComponent } from './new-job.component';

export const NEW_JOB_ROUTE: Route = {
  path: 'new-job',
  component: NewJobComponent,
  data: {
    authorities: [],
    pageTitle: 'New Job - Providentia'
  }
};
