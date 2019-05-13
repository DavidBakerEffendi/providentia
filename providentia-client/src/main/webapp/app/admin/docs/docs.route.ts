import { Route } from '@angular/router';

import { PrvDocsComponent } from './docs.component';

export const docsRoute: Route = {
  path: 'docs',
  component: PrvDocsComponent,
  data: {
    pageTitle: 'API'
  }
};
