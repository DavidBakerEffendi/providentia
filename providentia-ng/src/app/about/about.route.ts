import { Route } from '@angular/router';

import { AboutComponent } from './about.component';

export const ABOUT_ROUTE: Route = {
  path: 'about',
  component: AboutComponent,
  data: {
    authorities: [],
    pageTitle: 'About - Providentia'
  }
};
