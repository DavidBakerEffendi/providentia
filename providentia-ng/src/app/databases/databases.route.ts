import { Route } from '@angular/router';

import { DatabasesComponent } from './databases.component';

export const DATABASES_ROUTE: Route = {
  path: 'databases',
  component: DatabasesComponent,
  data: {
    authorities: [],
    pageTitle: 'Databases'
  }
};
