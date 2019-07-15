import { Route } from '@angular/router';

import { HealthCheckComponent } from './health.component';

export const healthRoute: Route = {
    path: 'health',
    component: HealthCheckComponent,
    data: {
        pageTitle: 'Health Checks'
    }
};
