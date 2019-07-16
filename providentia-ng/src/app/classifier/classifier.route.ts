import { Route } from '@angular/router';

import { ClassifierComponent } from './classifier.component';

export const CLASSIFIER_ROUTE: Route = {
  path: 'classifier',
  component: ClassifierComponent,
  data: {
    authorities: [],
    pageTitle: 'Classifier | Providentia'
  }
};
