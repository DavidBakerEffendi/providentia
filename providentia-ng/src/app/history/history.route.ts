import { Route } from '@angular/router';

import { HistoryComponent } from './history.component';

export const HISTORY_ROUTE: Route = {
  path: 'history',
  component: HistoryComponent,
  data: {
    authorities: [],
    pageTitle: 'History | Providentia'
  }
};
